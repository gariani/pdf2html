package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy.Stop
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConsumerFileActor.{ConsumerFile, Finished}
import com.lightbend.akka.http.gariani.Component.RabbitMQ.Supervisor.{Begin, End}

object Supervisor {

	case object Begin

	case object End

	def props = Props[Supervisor]
}

class Supervisor extends Actor {
	val queueListener = context.actorOf(ConsumerFileActor.props)

	override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
		case _: Exception => Stop
	}

	override def receive: Receive = {
		case Begin => queueListener ! ConsumerFile
		case End => queueListener ! Finished
	}
}
