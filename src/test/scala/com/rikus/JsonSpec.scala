package com.rikus

import org.scalatest.WordSpec
import org.scalatest.matchers.must.Matchers
import spray.json._
import JsonFormats._
import com.rikus.dao.elevatorStatus

class JsonSpec extends WordSpec with Matchers{
 println(new elevatorStatus(1,1).toJson.toString())
}
