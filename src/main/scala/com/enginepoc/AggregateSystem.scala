package com.enginepoc

import akka.actor
import akka.actor.{ActorSystem, Props}

object AggregateSystem extends App {
  val system: ActorSystem = ActorSystem("aggregateSystem")
  val aggregateMaintainer: actor.ActorRef = system.actorOf(Props[AggregateMaintainer], "aggregateMaintainer")
}
