package com.enginepoc

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext

object AggregateMaintenancePersistence {

  trait Message
  case class Hello(name: String) extends Message

  def apply(): Behavior[Message] = Behaviors.setup[Message] { context =>
    implicit val ec: ExecutionContext = context.executionContext

    Behaviors.receiveMessage {
      case message: Message => context.log.debug(s"message: $message received")
        Behaviors.same
    }
  }
}