package com.rikus.dao

import akka.actor.{Actor, ActorLogging}
import com.rikus.dao.Direction.Direction

import scala.collection.mutable.ListBuffer

case class Elevator(id: Int, initialState: (0, 0)) extends Actor with ActorLogging {
  var currentStatus: elevatorStatus = elevatorStatus(initialState._1, initialState._2)
  val destinations: scala.collection.mutable.ListBuffer[(Int, Option[Direction])] = ListBuffer.empty[(Int, Option[Direction])]

  def nextDestination = {

    val acceleration = currentStatus.destinationFloor - currentStatus.currentFloor
    val nextDest = destinations.headOption match {
      case Some((floorDest, direction)) => //always go to the oldest destination first
        val futureDestination = acceleration match {
          case x if x > 0 => {
            val oldDestination = (floorDest, direction)
            //if there is a destination floor smaller and on the same direction(for pickups) go there
            val nextDestUp = destinations.map {
              case (floor, Some(dir)) => {
                if ((floor <= floorDest) && (dir == Direction.UP))
                  (floor, Some(dir))
                else
                  oldDestination
              } //pickUp
              case (floor, None) => {
                if(floor <= floorDest)
                  (floor,None)
              } //dropOff
            }.asInstanceOf[ListBuffer[(Int,Option[Direction])]]
            nextDestUp
          } //going up
          case x if x == 0 => ListBuffer(destinations.head) //standstill
          case x if x < 0 => {
            val oldDestination = (floorDest, direction)
            //if there is a destination floor smaller and on the same direction(for pickups) go there
            val nextDestDown = destinations.map {
              case (floor, Some(dir)) => {
                if ((floor >= floorDest) && (dir == Direction.DOWN))
                  (floor, Some(dir))
                else
                  oldDestination
              } //pickUp
              case (floor, None) => {
                if(floor >= floorDest)
                  (floor,None)
              } //dropOff
            }.asInstanceOf[ListBuffer[(Int,Option[Direction])]]
            nextDestDown
          } // going down
        }
       val goingTo =  futureDestination.headOption match {
          case Some(destination) => destination
          case None => destinations.head
        }
        destinations -= goingTo
        goingTo
      case None =>
        log.info("No known next destination")
        (0,None)
    }
    nextDest
  }

  def addDestination(floor: Int, direction: Option[Direction]) = {
    if (!(destinations.contains((floor, None)) || destinations.contains((floor, Some(_: Direction)))))
      destinations += ((floor, direction))
  }

  def receive: Receive = {
    case statusRequest: status =>
      log.info(s"Elevator ${this.id} sending status ...")
      sender() ! currentStatus
    case pickup(floor, direction) =>
      addDestination(floor, Some(direction)) //add destination with direction
      log.info(s"Destinations for ${self} now include : ${destinations.toList}")
    case stepRequest: step => currentStatus = currentStatus.copy(currentStatus.destinationFloor, nextDestination._1) //change current status to next destination and remove that destination from pool
    case dropOff(floor) =>
      addDestination(floor, None)
      log.info(s"Destinations for ${self} now include : ${destinations.toList}")
  }
}
