package com.rikus.dao

import scala.language.implicitConversions

object Statuses extends Enumeration {
  type Statuses = Value
  val CREATING, PICKUP, DROPOFF, STEP, STATUS = Value
}
