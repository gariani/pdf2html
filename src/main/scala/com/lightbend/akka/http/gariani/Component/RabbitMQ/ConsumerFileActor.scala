package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, Props}
import com.lightbend.akka.http.gariani.Component.ConfigRabbitMQComponent
import com.lightbend.akka.http.gariani.Component.Json.JsonSupport._
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConsumerFileActor.{ConsumerFile, Finished}
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor.ConvertPdfFile
import com.spingo.op_rabbit.{Subscription, SubscriptionRef}

object ConsumerFileActor {

	def props: Props = Props(new ConsumerFileActor())

	case object ConsumerFile

	case object Finished

}

class ConsumerFileActor
	extends Actor with ConfigRabbitMQComponent with PdfJsonProtocol {

	var myQueueSubscription: Option[SubscriptionRef] = None

	val convertFileActor = context.actorOf(ConvertFileActor.props)

	override def receive = {
		case ConsumerFile => processMessage
		case Finished => finishConsumer
	}

	def processMessage = {

		myQueueSubscription = Some(
			Subscription.run(rabbitMQService.rabbitControl) {
				import com.spingo.op_rabbit.Directives._
				import rabbitMQService._
				channel(qos = 3) {
					consume(queue(rabbitMQService.QUEUE)) {
						(body(as[PdfObject])) { pdf =>
							println(s"Received ${pdf}")
							convertFileActor ! ConvertPdfFile(pdf)
							//context.stop(convertFileActor)
							ack
						}
					}
				}
			}
		)
	}

	def finishConsumer = myQueueSubscription.foreach(_.close())

}
