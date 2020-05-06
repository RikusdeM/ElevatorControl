package com.rikus


import com.rikus.dao.Direction.Direction
import com.rikus.QuickstartApp._
import java.io.ByteArrayOutputStream
import java.math.BigInteger

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.rikus.dao.{Elevator, createElevator, dropOff, dropOffReq, elevatorStatus, pickup, pickupReq, status, step}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import com.typesafe.scalalogging.LazyLogging
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}
import scodec._
import scodec.bits.BitVector
import scodec.codecs._

import scala.collection.mutable.ListBuffer
import spray.json._

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


