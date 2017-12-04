package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, Props}
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ErrorActor.GenerateAnswer

object ErrorActor {

	def props: Props = Props(new ErrorActor())

	case class GenerateAnswer(error: String)

}

class ErrorActor extends Actor {

	override def receive: Receive = {

		case GenerateAnswer(error) => generateAnswer(error)

	}

	private def generateAnswer(error: String) = {

	}

}
