package com.rikus.dao

import akka.actor.{Actor, ActorLogging}
import com.rikus.dao.Direction.Direction

import scala.collection.mutable.ListBuffer

case class Elevator(id: Int, initialState: (Int, Int), numberOfFloors: Int) extends Actor with ActorLogging {
  var currentStatus: elevatorStatus = elevatorStatus(initialState._1, initialState._2)
  val destinations: scala.collection.mutable.ListBuffer[(Int, Option[Direction])] = ListBuffer.empty[(Int, Option[Direction])]

  def nextDestination = {

    val acceleration = currentStatus.destinationFloor - currentStatus.currentFloor
    val ascendingDestinations = destinations.sortBy(dest => dest._1)
    val nextDest = destinations.headOption match {
      case Some((floorDest, direction)) => //always go to the oldest destination first
        val futureDestination = acceleration match {
          case x if x > 0 => {
            log.info(Console.CYAN + "going up" + Console.WHITE)
            val oldDestination = (floorDest, direction)
            //if there is a destination floor smaller and on the same direction(for pickups) go there
            val nextStepUp = ascendingDestinations.headOption match {
              case Some(destination) =>
                destination match {
                  case (floor, Some(direction)) =>
                    if (direction == Direction.UP)
                      destination
                    else
                      oldDestination
                  case (floor, None) =>
                    destination
                }
              case None => oldDestination
            }
            log.info("Next destionation UP : " + nextStepUp)
            ListBuffer(nextStepUp)
          } //going up

          case x if x == 0 => {
            log.info(Console.CYAN + "standing still" + Console.WHITE)
            val oldDestination = (floorDest, direction)

            val nextStep =
              if (currentStatus.currentFloor <= numberOfFloors / 2) // bottom half
                ascendingDestinations.headOption match { // next destination up
                  case Some(destination) => destination
                  case None => oldDestination
                }
              else //top half
                ascendingDestinations.reverse.headOption match { // next destination down
                  case Some(destination) => destination
                  case None => oldDestination
                }
            ListBuffer(nextStep)
          } //standstill

          case x if x < 0 => {
            log.info(Console.CYAN + "going down" + Console.WHITE)
            val oldDestination = (floorDest, direction)
            //if there is a destination floor smaller and on the same direction(for pickups) go there
            val nextStepDown = ascendingDestinations.reverse.headOption match {
              case Some(destination) =>
                destination match {
                  case (floor, Some(direction)) =>
                    if (direction == Direction.DOWN)
                      destination
                    else
                      oldDestination
                  case (floor, None) =>
                    destination
                }
              case None => oldDestination
            }
            log.info("Next destionation DOWN : " + nextStepDown)
            ListBuffer(nextStepDown)
          } // going down
        }
        log.info("Future Destination : " + futureDestination)
        val goingTo = futureDestination.headOption match {
          case Some(destination) => destination
          case None => destinations.head
        }
        log.info(Console.BLUE + "Going to " + goingTo.toString() + Console.WHITE)
        destinations -= goingTo
        goingTo
      case None =>
        log.info("No known next destination")
        (currentStatus.destinationFloor, None)
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
      log.info(Console.MAGENTA + s"Destinations for ${self} now include : ${destinations.toList}" + Console.WHITE)
    case stepRequest: step =>
      log.info(s"Stepping to next point")
      currentStatus = currentStatus.copy(currentStatus.destinationFloor, nextDestination._1) //change current status to next destination and remove that destination from pool
    case dropOff(floor) =>
      addDestination(floor, None)
      log.info(s"Destinations for ${self} now include : ${destinations.toList}")
  }
}
