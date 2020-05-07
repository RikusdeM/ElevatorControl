package com.rikus.dao

import akka.actor.{Actor, ActorLogging}
import com.rikus.dao.Direction.Direction

import scala.collection.mutable.ListBuffer

case class Elevator(id: Int, initialState: (0, 0)) extends Actor with ActorLogging {
  var currentStatus: elevatorStatus = elevatorStatus(initialState._1, initialState._2)
  val destinations: scala.collection.mutable.ListBuffer[(Int,Option[Direction])] = ListBuffer.empty[(Int, Option[Direction])] //todo : Fix to incorporate direction into destinations

  def nextDestination = {
    destinations.headOption match {
      case Some((floorDest,direction)) =>
        // todo : Fix the next destination smart logic 
        destinations -= floorDest
        floorDest
      case None =>
        log.info("No known next destination")
        0
    }
  }

  def addDestination(floor: Int, direction:Option[Direction]) = {

    if (!(destinations.contains((floor,None)) || destinations.contains((floor,Some(_)))))
      destinations += ((floor,direction))

//    if (!destinations.contains(floor))
//      destinations += floor
  }

  def receive: Receive = {
    case statusRequest: status =>
      log.info(s"Elevator ${this.id} sending status ...")
      sender() ! currentStatus
    case pickup(floor, direction) =>
      addDestination(floor,Some(direction)) //add destination with direction
      log.info(s"Destinations for ${self} now include : ${destinations.toList}")
    case stepRequest: step => currentStatus = currentStatus.copy(currentStatus.destinationFloor, nextDestination) //change current status to next destination and remove that destination from pool
    case dropOff(floor) =>
      addDestination(floor,None)
      log.info(s"Destinations for ${self} now include : ${destinations.toList}")
  }
}
