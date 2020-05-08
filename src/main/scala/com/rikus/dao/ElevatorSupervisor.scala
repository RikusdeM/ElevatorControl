package com.rikus.dao

import akka.actor.{Actor, ActorLogging, Props}
import com.rikus.dao
import com.rikus.JsonFormats._
import spray.json._

case class ElevatorSupervisor(numberOfFloors: Int) extends Actor with ActorLogging {

  def receive: Receive = {
    case createElevator(id) =>
      val elevatorRef = context.actorOf(Props(new Elevator(id, (0, 0), numberOfFloors)), name = s"elevator-${id}")
      log.info(Console.YELLOW + s"${elevatorRef.toString()} has been created" + Console.WHITE)
    case statusRequest: status => context.children.map(child => {
      log.info(s"Calling Child status : ${child.toString()}")
      child ! statusRequest
    }) //query all children elevators
    case stepRequest: step => context.children.foreach(child => child ! stepRequest) //step all children elevators
    case elevatorStatus: elevatorStatus => log.info(Console.GREEN + s"${sender()} : ${elevatorStatus.toJson.toString}" + Console.WHITE)
    case pickupReq(id, floor, direction) => context.child(s"elevator-${id}") match {
      case Some(childRef) => childRef ! dao.pickup(floor, direction)
      case None => log.info(s"No such child with id # ${id}")
    }
    case dropOffReq(id, floor) => context.child(s"elevator-${id}") match {
      case Some(childRef) => childRef ! dropOff(floor)
      case None => log.info(s"No such child with id # ${id}")
    }
  }
}
