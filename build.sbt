import com.typesafe.config.ConfigFactory
import com.typesafe.sbt.packager.docker._

val conf = com.typesafe.config.ConfigFactory.parseFile(new File("project/application.conf")).resolve()

lazy val akkaHttpVersion = "10.1.11"
lazy val akkaVersion = "2.6.5"
lazy val scodecBitsVersion = "1.1.12"
lazy val scodecCoreVersion = "1.11.4"

val artifactorySnapshots = Some("Artifactory Snapshot Realm" at "http://artifactory.mn8.ee/artifactory/libs-snapshot")
val artifactoryRelease = Some("Artifactory Realm" at "http://artifactory.mn8.ee/artifactory/libs-release")

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
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Artifactory Snapshot Realm" at "http://artifactory.mn8.ee/artifactory/libs-snapshot",
      "Artifactory Realm" at "http://artifactory.mn8.ee/artifactory/libs-release"
    ),
    resolvers += Resolver.bintrayRepo("akka", "snapshots"),
    credentials ++= Seq(
      Credentials("Artifactory Snapshot Realm", "artifactory.mn8.ee", "admin", conf.getString("artifactory_auth_token")),
      Credentials("Artifactory Realm", "artifactory.mn8.ee", "admin", conf.getString("artifactory_auth_token"))
    ),
    publishTo := {
      try {
        sys.env("VERSION")
        if (sys.env("VERSION").toString.contains("SNAPSHOT")) artifactorySnapshots
        else artifactoryRelease
      }
      catch {
        case ex: Exception =>
          if (version.toString().contains("SNAPSHOT")) artifactorySnapshots
          else artifactoryRelease
      }
    },
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
      "ee.mn8" %% "shaftconnectors" % "0.1.1",

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.1.1" % Test
    )
  ).enablePlugins(JavaAppPackaging)

mappings in Universal := {
  (mappings in Universal).value :+
    (file(s"${baseDirectory.value}/src/main/resources/elastic_certs/elastic-certificates.p12") -> "elastic_certs/elastic-certificates.p12")
}

dockerCommands ++= Seq(
  Cmd("USER", "root"),
  Cmd("EXPOSE", "8080"),
  Cmd("COPY", "/opt/docker/elastic_certs/elastic-certificates.p12", "./")
)
dockerExposedPorts ++= Seq(8080)