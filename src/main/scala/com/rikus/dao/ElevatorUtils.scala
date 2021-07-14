package com.rikus.dao

import akka.actor.{ActorContext, ActorRef}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

trait ElevatorUtils extends LazyLogging {
  implicit val timeout: Timeout = 1.second
  def childAnswerResult[A](
      askRes: Future[A],
      sender: ActorRef
  )(implicit
      executionContext: ExecutionContext,
      context: ActorContext
  ): Future[A] = {
    askRes
      .flatMap { res =>
        val success = Future.successful(res)
        sender.forward(res)
        success
      }
      .recoverWith {
        case error: Throwable =>
          val failure = Future.failed(error)
          logger.error(error.toString)
          sender.forward(failure)
          failure
      }
  }
}
