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
import com.rikus.QuickstartApp.system
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

class Routes()(implicit val system: ActorSystem) extends LazyLogging {


  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  import QuickstartApp._
  import Configurations.configFactory

  val elevatorSystemSupervisor = system.actorOf(Props[ElevatorSupervisor],name="ElevatorSupervisor")

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p@Credentials.Provided(id) if p.verify(configFactory.getString("my-app.routes.routesPassword")) => Some(id)
      case _ => None
    }

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  val route: Route = Route.seal {
    authenticateBasic(realm = "secure site", myUserPassAuthenticator) { userName =>
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~
        pathPrefix("ElevatorControl") {
          path("createElevator"/ Segment) { elevatorId:Int =>
            get {
                  val createFut = elevatorSystemSupervisor ? createElevator(elevatorId)
              complete(HttpEntity(ContentTypes.`application/json`, decodedMessage.toJson.toString()))
            }
          }
        } ~
        pathPrefix("ElevatorControl") {
          path("decode") {
            post {
              entity(as[String]) { data =>
                val decodedMessage = ElevatorControl.decode(data)
                complete(HttpEntity(ContentTypes.`application/json`, decodedMessage.toJson.toString()))
              }
            }
          }
        }
    }
  }
}