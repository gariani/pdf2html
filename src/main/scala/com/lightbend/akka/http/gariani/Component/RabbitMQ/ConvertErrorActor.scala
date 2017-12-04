package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, Props}
import com.lightbend.akka.http.gariani.Component.ConfigRabbitMQComponent
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol
import com.lightbend.akka.http.gariani.Component.Json.JsonSupport._
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConvertErrorActor.ConvertError
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ErrorActor.GenerateAnswer
import com.spingo.op_rabbit.{Subscription, SubscriptionRef}
import com.sun.org.apache.xml.internal.utils.ThreadControllerWrapper

object ConvertErrorActor {

	def props: Props = Props(new ConvertErrorActor())

	case class ConvertError(error: String)

	case object Finished

}

class ConvertErrorActor extends Actor with ConfigRabbitMQComponent with PdfJsonProtocol {

	override def receive: Receive = {

		case ConvertError(error) => println(s"--------> error ${error}") //sendErrorMessage(error)
	}

	val errorActor = context.actorOf(ErrorActor.props)

	private var myQueueSubscription: Option[SubscriptionRef] = None

	private def sendErrorMessage(error: String) = {

		myQueueSubscription = Some(
			Subscription.run(rabbitMQService.rabbitControl) {
				import com.spingo.op_rabbit.Directives._
				import rabbitMQService._
				channel(qos = 3) {
					consume(queue(rabbitMQService.ERROR_QUEUE)) {
						(body(as[String])) { error =>
							println(s"Received ${error}")
							errorActor ! GenerateAnswer(error)
							ack
						}
					}
				}
			}
		)

	}

}
