organization := "io.github.samanos"
name := "rcontrol"

scalaVersion := "2.11.8"
scalacOptions += "-feature"

val Akka = "2.4.4"

libraryDependencies ++= Seq(
  "io.github.samanos" %% "gpio"       % "0.0.1-4-g305c1a5",
  "io.github.samanos" %% "mqtt"       % "0.0.1",
  "io.spray"          %% "spray-json" % "1.3.2"
)

resolvers += Resolver.bintrayRepo("samanos", "maven")

enablePlugins(GitVersioning)
git.useGitDescribe := true

enablePlugins(JavaAppPackaging)
