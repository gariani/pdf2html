package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{Actor, Props}
import com.lightbend.akka.http.gariani.Component.ConfigRabbitMQComponent
import com.lightbend.akka.http.gariani.Component.Json.JsonSupport._
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConsumerParamFileActor.{FinishedParam, ParamConsumerFile}
import com.lightbend.akka.http.gariani.Custom.Parameters
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor.ConvertWithParam
import com.spingo.op_rabbit.{Subscription, SubscriptionRef}

object ConsumerParamFileActor {

	def props: Props = Props(new ConsumerParamFileActor())

	case object ParamConsumerFile

	case object FinishedParam

}

class ConsumerParamFileActor
	extends Actor with ConfigRabbitMQComponent with PdfJsonProtocol {

	val convertFileActor = context.actorOf(ConvertFileActor.props)
	var convertSubscription: Option[SubscriptionRef] = None

	override def receive = {
		case ParamConsumerFile => paramConsumerFile
		case FinishedParam => finishConsumer
	}

	def paramConsumerFile = {
		convertSubscription = Some(
			Subscription.run(rabbitMQService.rabbitControl) {
				import com.spingo.op_rabbit.Directives._
				import rabbitMQService._
				channel(qos = 3) {
					consume(queue(rabbitMQService.PARAM_QUEUE)) {
						(body(as[Parameters])) { param =>
							convertFileActor ! ConvertWithParam(param)
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
