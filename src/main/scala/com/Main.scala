package com

import akka.actor.typed.delivery.internal.ProducerControllerImpl.Request
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.io.Tcp.{Bind, Close, Command, ConfirmedClose, Message}
import akka.util.Timeout
import com.teoexample.AkkaTypedIncentives.{AddItem, ShoppingCartMessage, ValidateCart, shoppingBehavior}

import java.time.ZonedDateTime
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration.{Duration, DurationInt}

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

//  val builder = Vector.newBuilder[(String, (String, String))]
//
//  builder += ("1" -> ("orgId1", "value setting1"))
//  builder += ("2" -> ("orgId2", "value setting2"))
//
//  val aggregateSettings = builder.result()
//
//  println(aggregateSettings)
//  aggregateSettings.foreach { case (configKey, (orgId, settingValue)) =>
//    println(s"${configKey} => ${orgId}, ${settingValue}")
//  }

  val now =     ZonedDateTime.parse("2022-01-26T16:08:09.179062Z[UTC]").toEpochSecond
  val target =  ZonedDateTime.parse("2022-01-26T16:08Z[UTC]").toEpochSecond
  println(now - target)

  val now1 =     ZonedDateTime.parse("2022-01-26T16:09:09Z").toEpochSecond
  val target1 =  ZonedDateTime.parse("2022-01-26T16:08:10Z").toEpochSecond
  println(now1 - target1)
}