package com.rikus

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import Configurations.configFactory


//#set-up
class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  lazy val routes = new Routes()(system).route


  "ElevatorControlRoutes" should {
    "be able to decode a ElevatorControlMessage (POST /decode)" in {

      val validCredentials = BasicHttpCredentials("rikus", configFactory.getString("my-app.routes.routesPassword"))

      val iotMsg = "2f000055816f012f66b685e0"

      // using the RequestBuilding DSL:
      val request = Post("/ElevatorControl/decode").withEntity(iotMsg)

      request ~> addCredentials(validCredentials) ~>
        routes ~> check {

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and we know what message we're expecting back:
        entityAs[ElevatorControlDecoded] should ===(ElevatorControlDecoded(aux = "0", tpe = "47", lat = -33.96305, lon = 18.837019999999995, temp = 23.0, volt = 3.2))
      }
    }
  }
}