package com.rikus.dao

import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

trait ElevatorUtils extends LazyLogging {
  implicit val timeout: Timeout = 1.second
  def childAnswerResult[A](
      askRes: Future[A]
  )(implicit executionContext: ExecutionContext): Future[A] = {
    askRes
      .flatMap(res => Future.successful(res))
      .recoverWith {
        case error: Throwable =>
          logger.error(error.toString)
          Future.failed(error)
      }
  }
}
