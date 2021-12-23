package com

import akka.actor.typed.scaladsl.Behaviors

object SimpleTypedActor {

  case class SimpleMessage(message: String)

  def apply(): Behaviors.Receive[SimpleMessage] = Behaviors.receive[SimpleMessage] { (context, message) =>
    message match {
        case SimpleMessage(x) => context.log.debug(s"simple message received: $x")
        Behaviors.same
    }
  }
}
