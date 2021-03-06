package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, Props}
import com.lightbend.akka.http.gariani.Component.ConfigRabbitMQComponent
import com.lightbend.akka.http.gariani.Component.Json.JsonSupport._
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConsumerFileActor.{ConsumerFile, FinishedConvert}
import com.lightbend.akka.http.gariani.Custom.Parameters
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor.{ConvertPdfFile, ConvertWithParam}
import com.spingo.op_rabbit.{Subscription, SubscriptionRef}

object ConsumerFileActor {

	def props: Props = Props(new ConsumerFileActor())

	case object ConsumerFile

	case object FinishedConvert

}

class ConsumerFileActor
	extends Actor with ConfigRabbitMQComponent with PdfJsonProtocol {

	val convertFileActor = context.actorOf(ConvertFileActor.props)
	var convertSubscription: Option[SubscriptionRef] = None

	override def receive = {
		case ConsumerFile =>
			println("chamou o consumidor!!!")
			processMessage
		case FinishedConvert => finishConsumer
	}

	def processMessage = {

		convertSubscription = Some(
			Subscription.run(rabbitMQService.rabbitControl) {
				import com.spingo.op_rabbit.Directives._
				import rabbitMQService._
				channel(qos = 3) {
					consume(queue(rabbitMQService.CONVERT_QUEUE)) {
						(body(as[PdfObject])) { pdf =>
							convertFileActor ! ConvertPdfFile(pdf)
							ack
						}
					}
				}
			}
		)
	}

	def finishConsumer = {
		convertSubscription.foreach(_.close())
	}

}
