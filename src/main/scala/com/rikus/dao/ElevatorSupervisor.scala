package com.rikus.dao

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import com.rikus.dao
import com.rikus.JsonFormats._
import com.typesafe.scalalogging.LazyLogging
import spray.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

case class ElevatorSupervisor(numberOfFloors: Int, initialState: (Int, Int))(
    implicit executionContext: ExecutionContext
) extends Actor
    with ActorLogging
    with ElevatorUtils {

  def receive: Receive = {
    case createElevator(id) =>
      val elevatorRef = context.actorOf(
        Props(Elevator(id, initialState, numberOfFloors)),
        name = s"elevator-${id}"
      )
      log.info(
        Console.YELLOW + s"${elevatorRef.toString()} has been created" + Console.WHITE
      )
      sender().forward(elevatorRef.toString())
    case statusRequest: status =>
      context.children.map(child => {
        log.info(s"Calling Child status : ${child.toString()}")
        childAnswerResult(child ? statusRequest, sender())
      }) //query all children elevators
    case stepRequest: step =>
      context.children.foreach(child =>
        childAnswerResult(child ? stepRequest, sender())
      ) //step all children elevators
    case elevatorStatus: elevatorStatus =>
      log.info(
        Console.GREEN + s"${sender()} : ${elevatorStatus.toJson.toString}" + Console.WHITE
      )
    case pickupReq(id, floor, direction) =>
      context.child(s"elevator-${id}") match {
        case Some(childRef) =>
          childAnswerResult(childRef ? dao.pickup(floor, direction), sender())
        case None => log.info(s"No such child with id # ${id}")
      }
    case dropOffReq(id, floor) =>
      context.child(s"elevator-${id}") match {
        case Some(childRef) => {
          childAnswerResult(childRef ? dropOff(floor), sender())
        }
        case None => log.info(s"No such child with id # ${id}")
      }
  }
}
