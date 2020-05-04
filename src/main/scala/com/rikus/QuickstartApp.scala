package com.rikus

import scala.util.Failure
import scala.util.Success
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import ee.mn8.connectors._


object Configurations {
  val configFactory = ConfigFactory.load()
}

object QuickstartApp extends App {

  implicit val system = ActorSystem("ElevatorControlServer")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val routes = new Routes()(system)
  val servicePort = Configurations.configFactory.getInt("my-app.routes.servicePort")
  val futureBinding = Http().bindAndHandle(routes.route, "0.0.0.0", servicePort)
  val kafkaCon = new KafkaConn()
  val elasticCon = new ElasticConn(Configurations.configFactory.getString("elasticsearch.host"),
    Configurations.configFactory.getInt("elasticsearch.port"),
    Configurations.configFactory.getString("elasticsearch.jksPassword"),
    Configurations.configFactory.getString("elasticsearch.username"),
    Configurations.configFactory.getString("elasticsearch.password"))

  futureBinding.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
    case Failure(ex) =>
      system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
      system.terminate()
  }
}

