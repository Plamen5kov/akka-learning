package com.teoexample

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.io.Tcp.{Close, Command}

object AkkaTypedIncentives {
  def shoppingBehavior(items: Set[String]): Behavior[ShoppingCartMessage] = Behaviors.receive[ShoppingCartMessage] { (ctx, message) =>
    message match {
      case AddItem(item, replyTo) =>
        ctx.log.debug(s"Adding item $item.")
        replyTo ! Close
        shoppingBehavior(items + item)
      case RemoveItem(item, replyTo) =>
        ctx.log.debug(s"Removing item $item.")
        replyTo ! Close
        shoppingBehavior(items - item)
      case ValidateCart =>
        ctx.log.debug(s"The card is good. The items are: $items")
        Behaviors.same

    }
  }

  trait ShoppingCartMessage extends Command

  case class AddItem(item: String, replyTo: ActorRef[Command]) extends ShoppingCartMessage

  case class RemoveItem(item: String, replyTo: ActorRef[Command]) extends ShoppingCartMessage

  case object ValidateCart extends ShoppingCartMessage


}
