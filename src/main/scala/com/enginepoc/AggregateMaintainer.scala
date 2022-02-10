package com.enginepoc

import akka.actor.Actor
import akka.actor.typed.ActorSystem

class AggregateMaintainer extends Actor {

  override def preStart() = {
    //obrain typed actor instance
    val system: ActorSystem[AggregateMaintenancePersistence.Hello] = ActorSystem(AggregateMaintenancePersistence(), "AggregateMaintenancePersistence")

    //send message to typed actor
    system ! AggregateMaintenancePersistence.Hello("world")
  }

  override def receive: Receive = {
    case message => println("recieved message")
  }
}

object AggregateMaintainer {

  trait Message

  case object Hello extends Message

}
