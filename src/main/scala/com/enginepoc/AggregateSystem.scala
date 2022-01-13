package com.enginepoc

import akka.actor
import akka.actor.{ActorSystem, Props}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object AggregateSystem extends App {
  val system: ActorSystem = ActorSystem("aggregateSystem")
  val aggregateMaintainer: actor.ActorRef = system.actorOf(Props[AggregateMaintainer], "aggregateMaintainer")
}
