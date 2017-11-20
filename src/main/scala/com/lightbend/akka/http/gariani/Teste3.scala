package com.lightbend.akka.http.gariani

import akka.actor.{ActorSystem, Inbox}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object Teste3 extends App {

	implicit val actorSystem: ActorSystem = ActorSystem("akka")

	implicit val materialize: ActorMaterializer = ActorMaterializer()

	implicit val dispatcher: ExecutionContext = actorSystem.dispatcher

	//implicit val i: Inbox = inbox()

}
