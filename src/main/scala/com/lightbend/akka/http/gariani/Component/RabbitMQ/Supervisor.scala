package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy.Stop
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConsumerFileActor.{ConsumerFile, FinishedConvert}
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConsumerParamFileActor.{FinishedParam, ParamConsumerFile}
import com.lightbend.akka.http.gariani.Component.RabbitMQ.Supervisor.{Begin, End}

object Supervisor {

	case object Begin

	case object End

	def props = Props[Supervisor]
}

class Supervisor extends Actor {
	val queueConvert = context.actorOf(ConsumerFileActor.props)
	val queueParam = context.actorOf(ConsumerParamFileActor.props)

	override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
		case _: Exception => Stop
	}

	override def receive: Receive = {
		case Begin =>
			queueConvert ! ConsumerFile
			queueParam ! ParamConsumerFile
		case End =>
			queueConvert ! FinishedConvert
			queueParam ! FinishedParam
	}
}
