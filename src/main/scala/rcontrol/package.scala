package io.github.samanos

import akka.event.Logging

package object rcontrol {

  implicit class ThrowableOps(t: Throwable) {
    def nameAndMessage = s"${Logging.simpleName(t)}: ${t.getMessage}"
  }

}
