package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, Props}
import com.lightbend.akka.http.gariani.Component.ConfigRabbitMQComponent
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.spingo.op_rabbit.Message
import com.lightbend.akka.http.gariani.Component.Json.JsonSupport._
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol
import com.lightbend.akka.http.gariani.Component.RabbitMQ.PublisherFileActor.{GeneratorError, NewFileConvert, NewFileWithParamConvert}
import com.lightbend.akka.http.gariani.Custom.Parameters

object PublisherFileActor {

	def props: Props = Props(new PublisherFileActor())

	case class NewFileConvert(pdf: PdfObject)

	case class NewFileWithParamConvert(param: Parameters)

	case class GeneratorError(pdf: PdfObject)
}

class PublisherFileActor
	extends Actor with ConfigRabbitMQComponent with PdfJsonProtocol {

	override def receive: Receive = {
		case NewFileConvert(pdf) => publishNewFile(pdf, rabbitMQService.CONVERT_QUEUE)
		case GeneratorError(pdf) => publishNewFile(pdf, rabbitMQService.ERROR_QUEUE)
		case NewFileWithParamConvert(param) => publishFileParam(param, rabbitMQService.PARAM_QUEUE)
	}

	def actorName = {
		println(s"-----> Publishe: ${self.path.name}")
	}

	def publishFileParam(param: Parameters, queue: String) = {
		actorName
		rabbitMQService.rabbitControl ! Message.queue(param, queue)
	}

	def publishNewFile(pdf: PdfObject, queue: String) = {
		actorName
		rabbitMQService.rabbitControl ! Message.queue(pdf, queue)
	}
}
