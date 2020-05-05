package com.rikus

import java.math.BigInteger
import java.util.UUID

import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, MediaRanges, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.actor._
import akka.util.{ByteString, Timeout}
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import spray.json._
import scala.concurrent.duration._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class Routes()(implicit val system: ActorSystem) extends LazyLogging {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  import QuickstartApp._
  import Configurations.configFactory

  val elevatorSystemSupervisor = system.actorOf(Props[ElevatorSupervisor], name = "ElevatorSupervisor")

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p@Credentials.Provided(id) if p.verify(configFactory.getString("my-app.routes.routesPassword")) => Some(id)
      case _ => None
    }

  val route: Route = Route {
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~
      pathPrefix("ElevatorControl") {
        path("createElevator" / Segment) { elevatorId: String =>
          get {
            elevatorSystemSupervisor ! createElevator(elevatorId.toInt)
            complete(HttpEntity(ContentTypes.`application/json`, "creating"))
          }
        }
      } ~
      pathPrefix("ElevatorControl") {
        path("step") {
          get {
            elevatorSystemSupervisor ! new step()
            complete(HttpEntity(ContentTypes.`application/json`, "step"))
          }
        }
      } ~
      pathPrefix("ElevatorControl") {
        path("status") {
          get {
            val futResult = Await.result(elevatorSystemSupervisor ? new status(),10.seconds)
            futResult.toString()
            complete(HttpEntity(ContentTypes.`application/json`, futResult.toString()))
          }
        }
      }

    //      pathPrefix("ElevatorControl") {
    //        path("decode") {
    //          post {
    //            entity(as[String]) { data =>
    //              val decodedMessage = ElevatorControl.decode(data)
    //              complete(HttpEntity(ContentTypes.`application/json`, decodedMessage.toJson.toString()))
    //            }
    //          }
    //        }
    //      }
  }
}