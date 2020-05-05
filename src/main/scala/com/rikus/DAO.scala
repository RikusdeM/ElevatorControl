package com.rikus


import com.rikus.Direction.Direction
import com.rikus.QuickstartApp._
import java.io.ByteArrayOutputStream
import java.math.BigInteger

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import com.typesafe.scalalogging.LazyLogging
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}
import scodec._
import scodec.bits.BitVector
import scodec.codecs._

import scala.collection.mutable.ListBuffer

// Define message type for scodec
case class ElevatorControl(tpe: Int, aux: Int, elat: Int, elon: Int, temp: Int, volt: Int)

case class ElevatorControlDecoded(tpe: String, aux: String, lat: Double, lon: Double, temp: Double, volt: Double)

object ElevatorControl extends LazyLogging {
  val emptyElevatorControlMessage: ElevatorControlDecoded = ElevatorControlDecoded("0", "0", 0.0, 0.0, 0.0, 0.0)
  implicit val codec: scodec.Codec[ElevatorControl] = {
    ("type" | int8) :: ("aux" | int8) :: ("elat" | int32) :: ("elon" | int32) :: ("temp" | int8) :: ("volt" | int8)
    }.as[ElevatorControl]

  def encoded(msg: ElevatorControl): Attempt[BitVector] = codec.encode(msg)

  def decoded(bitVector: BitVector): Attempt[DecodeResult[ElevatorControl]] = codec.decode(bitVector)

  def decode(data: String) = {
    val bs: Array[Byte] = new BigInteger(data, 16).toByteArray
    val bits: BitVector = if (BitVector(bs).sizeLessThan(96)) BitVector(bs).padLeft(96) else BitVector(bs).slice(0, 96)
    val decodedMessage = decoded(bits).toOption.map(m => m.value) match {
      case Some(value) => ElevatorControlDecoded(value.tpe.toString, value.aux.toString, (value.elat / 100000d) - 90, (value.elon / 100000d) - 180, (value.temp + 100) * -1d, value.volt / -10d)
      case None => {
        logger.error(s"Could not decode encoded string: $data")
        emptyElevatorControlMessage
      }
    }
    decodedMessage
  }

  def apply(tpe: Int, aux: Int, elat: Int, elon: Int, volt: Int, temp: Int): ElevatorControl = new ElevatorControl(tpe, aux, elat, elon, volt, temp)

  def unapply(arg: ElevatorControl): Option[(Int, Int, Int, Int, Int, Int)] = Some(arg.tpe, arg.aux, arg.elat, arg.elon, arg.volt, arg.temp)

  val schema = AvroSchema[ElevatorControl]

  def serialize(sensorMsg: ElevatorControl) = {
    val byteArrStream = new ByteArrayOutputStream();
    val os = AvroOutputStream.data[ElevatorControl].to(byteArrStream).build(schema)
    os.write(Seq(sensorMsg))
    os.flush() // flush OutputStream
    os.close() // close OutputStream
    byteArrStream
  }

  def deserialize(byteArrStream: ByteArrayOutputStream) = {
    val byteArr = byteArrStream.toByteArray
    val is = AvroInputStream.data[ElevatorControl].from(byteArr).build(schema)
    val deserializedSensorMsg = is.tryIterator
    is.close() //close InputStream
    deserializedSensorMsg
  }

}

object ElevatorControlDecoded {
  def apply(tpe: String, aux: String, lat: Double, lon: Double, temp: Double, volt: Double): ElevatorControlDecoded = new ElevatorControlDecoded(tpe, aux, lat, lon, temp, volt)

  def unapply(arg: ElevatorControlDecoded): Option[(String, String, Double, Double, Double, Double)] = Some(arg.tpe, arg.aux, arg.lat, arg.lon, arg.temp, arg.volt)

  val schema = AvroSchema[ElevatorControlDecoded]

  def serialize(sensorMsg: ElevatorControlDecoded) = {
    val byteArrStream = new ByteArrayOutputStream();
    val os = AvroOutputStream.data[ElevatorControlDecoded].to(byteArrStream).build(schema)
    os.write(Seq(sensorMsg))
    os.flush() // flush OutputStream
    os.close() // close OutputStream
    byteArrStream
  }

  def deserialize(byteArrStream: ByteArrayOutputStream) = {
    val byteArr = byteArrStream.toByteArray
    val is = AvroInputStream.data[ElevatorControlDecoded].from(byteArr).build(schema)
    val deserializedSensorMsg = is.tryIterator
    is.close() //close InputStream
    deserializedSensorMsg
  }
}

object Direction extends Enumeration {
  type Direction = Value
  val UP, DOWN = Value
}

case class createElevator(id: Int)

case class elevatorStatus(currentFloor: Int, destinationFloor: Int)

case class pickup(floor: Int, direction: Direction)

case class step()

case class status()

case class Elevator(id: Int, initialState: (0, 0)) extends Actor with ActorLogging {
  var currentStatus: elevatorStatus = elevatorStatus(initialState._1, initialState._2)
  val destinations: scala.collection.mutable.ListBuffer[Int] = ListBuffer.empty[Int]

  def nextDestination = {
    destinations.headOption match {
      case Some(floorDest) =>
        destinations -= floorDest
        floorDest
      case None =>
        log.info("No known next destination")
        0
    }

  }

  def receive: Receive = {
    case statusRequest: status => sender() ! currentStatus
    case pickup(floor, direction) => destinations += floor
    case stepRequest: step => currentStatus = currentStatus.copy(currentStatus.destinationFloor,nextDestination) //change current status to next destination and remove that destination from pool
  }
}

case class ElevatorSupervisor() extends Actor with ActorLogging {
  def receive: Receive = {
    case createElevator(id) =>
      val elevatorRef = context.actorOf(Props(new Elevator(id, (0, 0))), name = s"elevator-${id}")
      log.info(s"${elevatorRef.toString()} has been created")
    case statusRequest: status => context.children.map(child => {
      val childStatus = (child ? statusRequest).asInstanceOf[Future[elevatorStatus]]
      childStatus
    }) //query all children elevators
    case stepRequest: step => context.children.foreach(child => child ! stepRequest) //step all children elevators
  }
}


