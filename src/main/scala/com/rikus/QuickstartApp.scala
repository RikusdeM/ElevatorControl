package com.rikus

import scala.util.Failure
import scala.util.Success
import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory


object Configurations {
  val configFactory = ConfigFactory.load()
}

object QuickstartApp extends App {

  implicit val system = ActorSystem("ElevatorControlServer")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(3.seconds)
//  val elevatorSystem = system.actorOf(Props[ElevatorSupervisor],name="ElevatorSupervisor")

  val routes = new Routes()(system)
  val servicePort = Configurations.configFactory.getInt("my-app.routes.servicePort")
  val futureBinding = Http().bindAndHandle(routes.route, "0.0.0.0", servicePort)


  futureBinding.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
    case Failure(ex) =>
      system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
      system.terminate()
  }
}

