package com.rikus

//#json-formats
import com.rikus.dao._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._
  implicit val elevatorStatusFormat:RootJsonFormat[elevatorStatus] = jsonFormat2(elevatorStatus)
}
//#json-formats
