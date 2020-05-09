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
          //          case x if x > 0 => {
          //            log.info(Console.CYAN + "going up" + Console.WHITE)
          //            val oldDestination = (floorDest, direction)
          //            //if there is a destination floor smaller and on the same direction(for pickups) go there
          //            val nextStepUp = ascendingDestinations.headOption match {
          //              case Some(destination) =>
          //                destination match {
          //                  case (floor, Some(direction)) =>
          //                    if (direction == Direction.UP)
          //                      destination
          //                    else
          //                      oldDestination
          //                  case (floor, None) =>
          //                    destination
          //                }
          //              case None => oldDestination
          //            }
          //            log.info("Next destionation UP : " + nextStepUp)
          //            nextStepUp
          //          } //going up

          case x => {
            log.info(Console.CYAN + "standing still" + Console.WHITE)
            val oldDestination = (floorDest, direction)

            val nextStep =
              if (currentStatus.destinationFloor <= numberOfFloors / 2) // bottom half //todo:
                ascendingDestinations.headOption match { // next destination up
                  case Some(destination) => {
                    destination match {
                      case ((floor, Some(direction))) => { //pickup
                        if (destinations.tail.nonEmpty) {
                          if (floor <= ascendingDestinations.tail.head._1 && (direction == Direction.UP))
                            destination
                          else
                            oldDestination
                        }
                        else destination
                      }
                      case ((floor, None)) => { //dropOff
                        if (destinations.tail.nonEmpty) {
                          if (floor <= ascendingDestinations.tail.head._1)
                            destination
                          else
                            oldDestination
                        }
                        else destination
                      }
                    }
                  } // only if smaller than head and going up
                  case None => oldDestination
                }
              else //top half
                ascendingDestinations.reverse.headOption match { // next destination down
                  case Some(destination) => {
                    destination match {
                      case ((floor, Some(direction))) => { //pickup
                        if (destinations.tail.nonEmpty) {
                          if (floor >= ascendingDestinations.reverse.tail.head._1 && (direction == Direction.DOWN))
                            destination
                          else
                            oldDestination
                        }
                        else destination
                      }
                      case ((floor, None)) => { //dropOff
                        if (destinations.tail.nonEmpty) {
                          if (floor >= ascendingDestinations.reverse.tail.head._1)
                            destination
                          else
                            oldDestination
                        }
                        else destination
                      }
                    }
                  } // only if smaller than head and going up
                  case None => oldDestination
                }
            nextStep
          } //standstill

        }
        log.info("Future Destination : " + futureDestination)

        val goingTo = futureDestination
        log.info(Console.BLUE + "Going to " + goingTo.toString() + Console.WHITE)
        destinations -= goingTo
        log.info(Console.MAGENTA + s"Destinations for ${self} now include : ${destinations.toList}" + Console.WHITE)
        goingTo
      case None =>
        log.info("No known next destination")
        (currentStatus.destinationFloor, None)
    }
    nextDest
  }

  def addDestination(floor: Int, direction: Option[Direction]) = {
    if (!(destinations.contains((floor, None)) ||
      destinations.contains((floor, Some(Direction.UP))) ||
      destinations.contains((floor, Some(Direction.DOWN)))))
      destinations += ((floor, direction))
    log.info(Console.MAGENTA + s"Destinations for ${self} now include : ${destinations.toList}" + Console.WHITE)
  }

  def receive: Receive = {
    case statusRequest: status =>
      log.info(s"Elevator ${this.id} sending status ...")
      sender() ! currentStatus
    case pickup(floor, direction) =>
      addDestination(floor, Some(direction)) //add destination with direction

    case stepRequest: step =>
      log.info(s"Stepping to next point")
      currentStatus = currentStatus.copy(currentStatus.destinationFloor, nextDestination._1) //change current status to next destination and remove that destination from pool
    case dropOff(floor) =>
      addDestination(floor, None)
  }
}
