package com.rikus
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

case class ElevatorConfig(
    totalFloors: Int,
    initialState: (Int, Int),
    routesPassword: String,
    servicePort: Int
)

trait Configuration extends LazyLogging {
  implicit val config: ElevatorConfig = {
    Try {
      val configFactory: Config = ConfigFactory.load()
      val totalFloors: Int =
        configFactory.getInt("myApp.elevator.totalFloors")
      val initialState: (Int, Int) = (
        configFactory.getInt("myApp.elevator.initialStartingFloor"),
        configFactory.getInt(
          "myApp.elevator.initialDestinationFloor"
        )
      )
      val routesPassword =
        configFactory.getString("myApp.routes.routesPassword")
      val servicePort = configFactory.getInt("myApp.routes.servicePort")
      ElevatorConfig(totalFloors, initialState, routesPassword, servicePort)
    } match {
      case Success(value) => value
      case Failure(exception) => {
        logger.error("Could not load config")
        logger.error(exception.toString)
        ElevatorConfig(10, (0, 0), "", 8080)
      }
    }
  }
}
