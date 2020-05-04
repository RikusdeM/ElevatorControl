import com.typesafe.config.ConfigFactory
import com.typesafe.sbt.packager.docker._

val conf = com.typesafe.config.ConfigFactory.parseFile(new File("project/application.conf")).resolve()

lazy val akkaHttpVersion = "10.1.11"
lazy val akkaVersion = "2.6.5"
lazy val scodecBitsVersion = "1.1.12"
lazy val scodecCoreVersion = "1.11.4"

dockerBaseImage := "openjdk:10.0"
packageName in Docker := "gitlab.mn8.ee:4567/dev/ElevatorControl"
version in Docker := version.value

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.rikus",
      scalaVersion := "2.13.1"
    )),
    name := "ElevatorControl",
    version := conf.getString("version"),
    resolvers ++= Seq(
      "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    ),
    resolvers += Resolver.bintrayRepo("akka", "snapshots"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "org.scodec" %% "scodec-bits" % scodecBitsVersion,
      "org.scodec" %% "scodec-core" % scodecCoreVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.sksamuel.avro4s" %% "avro4s-core" % "3.0.9",

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.1.1" % Test
    )
  ).enablePlugins(JavaAppPackaging)

dockerCommands ++= Seq(
  Cmd("USER", "root"),
  Cmd("EXPOSE", "8080")
)
dockerExposedPorts ++= Seq(8080)