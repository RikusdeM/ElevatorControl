package com.rikus

import java.math.BigInteger
import java.util.UUID
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  HttpMethods,
  HttpRequest,
  HttpResponse,
  MediaRanges,
  MediaTypes,
  StatusCodes
}
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.actor._
import akka.util.{ByteString, Timeout}
import akka.pattern.ask
import com.rikus.dao.{
  Direction,
  ElevatorSupervisor,
  createElevator,
  dropOffReq,
  pickupReq,
  status,
  step
}
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Routes()(implicit val system: ActorSystem)
    extends LazyLogging
    with Configuration {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  import QuickstartApp._
  import com.rikus.dao.Statuses._

  val elevatorSystemSupervisor = system.actorOf(
    Props(ElevatorSupervisor(config.totalFloors, config.initialState)),
    name = "ElevatorSupervisor"
  )

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id)
          if p.verify(
            config.routesPassword
          ) =>
        Some(id)
      case _ => None
    }

  val failPattern: Throwable => StandardRoute = (error: Throwable) => {
    logger.error(error.toString)
    complete {
      HttpResponse(
        StatusCodes.BadRequest,
        entity = HttpEntity(
          ContentTypes.`application/json`,
          error.toString
        )
      )
    }
  }

  val route: Route = Route {
    pathPrefix("ElevatorControl") {
      path("createElevator" / Segment) { elevatorId: String =>
        get {
          onComplete(
            elevatorSystemSupervisor ? createElevator(elevatorId.toInt)
          ) {
            case Success(_) =>
              complete {
                HttpEntity(ContentTypes.`application/json`, CREATING)
              }

            case Failure(error) =>
              failPattern(error)
          }
        }
      }
    } ~
      pathPrefix("ElevatorControl") {
        path("pickup") {
          parameters(
            'id.as[String],
            'floor.as[String].?,
            'direction.as[String].?
          ) { (id, floor, direction) =>
            get {
              onComplete {
                val dir = direction.getOrElse("up") match {
                  case "up"   => Direction.UP
                  case "down" => Direction.DOWN
                }
                elevatorSystemSupervisor ? pickupReq(
                  id.toInt,
                  floor.get.toInt,
                  dir
                )
              } {
                case Success(_) =>
                  complete {
                    HttpEntity(ContentTypes.`application/json`, PICKUP)
                  }

                case Failure(error) =>
                  failPattern(error)
              }
            }
          }
        }
      } ~
      pathPrefix("ElevatorControl") {
        path("dropOff") {
          parameters('id.as[String], 'floor.as[String].?) { (id, floor) =>
            get {
              onComplete {
                elevatorSystemSupervisor ? dropOffReq(
                  id.toInt,
                  floor.get.toInt
                )
              } {
                case Success(_) =>
                  complete {
                    HttpEntity(ContentTypes.`application/json`, DROPOFF)
                  }

                case Failure(error) =>
                  failPattern(error)
              }
            }
          }
        }
      } ~
      pathPrefix("ElevatorControl") {
        path("step") {
          get {
            onComplete {
              elevatorSystemSupervisor ? step()
            } {
              case Success(_) =>
                complete {
                  HttpEntity(ContentTypes.`application/json`, STEP)
                }

              case Failure(error) =>
                failPattern(error)
            }
          }
        }
      } ~
      pathPrefix("ElevatorControl") {
        path("status") {
          get {
            onComplete {
              elevatorSystemSupervisor ? status()
            } {
              case Success(_) =>
                complete {
                  HttpEntity(
                    ContentTypes.`application/json`,
                    STATUS
                  )
                }

              case Failure(error) =>
                failPattern(error)
            }
          }
        }
      }
  }
}
