package io.github.samanos.rcontrol

import akka.actor.ActorSystem
import akka.stream._

import io.github.samanos.gpio.Gpio
import io.github.samanos.mqtt

import spray.json._
import java.nio.charset.StandardCharsets

object Main extends App {

  case class RelayControl(relayId: String, connectionIdx: Int, state: Boolean)

  object JsonSupport extends DefaultJsonProtocol {
    implicit val relayControl = jsonFormat3(RelayControl)
  }
  import JsonSupport._

  final val Device1 = Gpio.Port.Gpio23
  final val Device2 = Gpio.Port.Gpio24

  implicit val sys = ActorSystem("RelayControl")
    implicit val mat = ActorMaterializer {
      ActorMaterializerSettings(sys).withSupervisionStrategy { ex =>
        sys.log.warning(s"Error in the stream: ${ex.nameAndMessage}. Restarting stream.")
        Supervision.restart
      }
    }

  sys.log.info("Starting controller stream.")

  val gpio = Gpio("rcontrol.gpio")
  val topic = sys.settings.config.getString("rcontrol.topic")
  val config = sys.settings.config.getConfig("mqtt")

  mqtt.Consumer
    .source(topic, config)
    .map { mqttMsg =>
      new String(mqttMsg.getPayload, StandardCharsets.UTF_8)
    }
    .map(_.parseJson.convertTo[RelayControl])
    .map { controlMsg =>
      sys.log.info("Received control message: {}", controlMsg)
      controlMsg
    }
    .runForeach { controlMsg =>
      if (controlMsg.relayId == "relay-1") {
        val device = controlMsg.connectionIdx match {
          case 0 => Some(Device1)
          case 1 => Some(Device2)
          case _ => None
        }

        device.foreach(gpio.relay(_, controlMsg.state))
      }
    }

}
