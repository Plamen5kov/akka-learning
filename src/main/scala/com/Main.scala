package com

import akka.actor.typed.delivery.internal.ProducerControllerImpl.Request
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.io.Tcp.{Bind, Close, Command, ConfirmedClose, Message}
import akka.util.Timeout
import com.teoexample.AkkaTypedIncentives.{AddItem, ShoppingCartMessage, ValidateCart, shoppingBehavior}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration.DurationInt

//#main-class
object Main extends App {
  //  val greeterMain: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain(), "AkkaQuickStart")
  //  greeterMain ! GreeterMain.SayHello("Charles")
  //
  //  val system: ActorSystem[SimpleTypedActor.SimpleMessage] = ActorSystem(SimpleTypedActor(), "actorSystem")
  //  system ! SimpleTypedActor.SimpleMessage("plamen")


  implicit val context: ExecutionContextExecutor = ExecutionContext.global
  implicit val timeout: Timeout = 2.seconds


  val aggregateSystem = ActorSystem(
    Behaviors.setup[Message] { ctx: ActorContext[Message] =>
      // create children HERE
      val aggregateMaintainer = ctx.spawn(shoppingBehavior(Set()), "storeShoppingCart")

      implicit val scheduler: Scheduler = ctx.system.scheduler
      Behaviors.receiveMessage {
        case AddItem(item, replyTo) =>
          ctx.ask(aggregateMaintainer, AddItem(item, _))(t => t.get)
          replyTo ! ConfirmedClose
          Behaviors.same
        case ValidateCart =>
          aggregateMaintainer ! ValidateCart
          Behaviors.same
        case response =>
          ctx.log.debug(s"got response: ${response.toString}")
          Behaviors.same
      }
    },
    "onlineStore"
  )

  implicit val asd: Scheduler = aggregateSystem.scheduler
  (aggregateSystem ? ((replyTo: ActorRef[Command]) => AddItem("asd", replyTo))).onComplete(println(_))
//  rootOnlineStoreActor ! AddItem("dddd", rootOnlineStoreActor)
//  rootOnlineStoreActor ! ValidateCart
//  aggregateSystem ! Request()
}