package com.lightbend.akka.http.gariani.Component.RabbitMQ

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.lightbend.akka.http.gariani.Component.RabbitMQ.Supervisor.Begin
import com.spingo.op_rabbit.{AirbrakeLogger, RabbitControl, RecoveryStrategy, Slf4jLogger}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import com.spingo.op_rabbit.PlayJsonSupport._

trait RabbitMQConfig {

	val rabbitMqConfig = ConfigFactory.load("reference.conf").getConfig("op-rabbit")
	val CONVERT_QUEUE = rabbitMqConfig.getString("convert-queue")
	val ERROR_QUEUE = rabbitMqConfig.getString("error-queue")
	val PARAM_QUEUE = rabbitMqConfig.getString("param-queue")
	val exchange = "amq.fanout"

}

class RabbitMQService(implicit val ac: ActorSystem, materializer: ActorMaterializer)
	extends RabbitMQConfig {

	implicit val recoveryStrategy = RecoveryStrategy.none

	implicit val rabbitErrorLogging = Slf4jLogger + AirbrakeLogger.fromConfig

	val rabbitControl = ac.actorOf(Props[RabbitControl])

}
