package com.rikus

import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}


class Avro4sSpec extends AnyFlatSpec with Matchers {

  "avro4s" should "make a avro record from a case class" in {
    val schema = AvroSchema[ElevatorControlDecoded]
    println(schema.toString(true))
  }

  "avro4s" should "serialize a record with a schema obtained form a case class" in {
    val schema = ElevatorControlDecoded.schema
    val ElevatorControlDecodedMsg = ElevatorControlDecoded("visiotrackV1","aux_1",-33.932127, 18.860150, 24.4, 3.3)

    val serialized = ElevatorControlDecoded.serialize(ElevatorControlDecodedMsg)
    val deserialized = ElevatorControlDecoded.deserialize(serialized)

    assert(deserialized.toList.head.get == ElevatorControlDecodedMsg)

  }

}
