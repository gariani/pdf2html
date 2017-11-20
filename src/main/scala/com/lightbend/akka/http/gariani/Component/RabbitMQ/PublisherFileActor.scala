package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, Props}
import com.lightbend.akka.http.gariani.Component.ConfigRabbitMQComponent
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.spingo.op_rabbit.Message
import com.lightbend.akka.http.gariani.Component.Json.JsonSupport._
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol

case class NewFileConvert(pdf: PdfObject)

object PublisherFileActor {

	def props: Props = Props(new PublisherFileActor())

}

class PublisherFileActor
	extends Actor with ConfigRabbitMQComponent with PdfJsonProtocol {

	override def receive: Receive = {

		case NewFileConvert(pdf: PdfObject) => publishNewFile(pdf: PdfObject)

	}

	def publishNewFile(pdf: PdfObject) = {
		rabbitMQService.rabbitControl ! Message.queue(pdf, rabbitMQService.QUEUE)
		context.stop(self)
	}
}
