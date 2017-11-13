package com.lightbend.akka.http.gariani.Component.WebApi

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AkkaActorSystemConfig {

  val config: Config = ConfigFactory.load("reference.conf")

  implicit val actorSystem: ActorSystem = ActorSystem("PdfConverterActor", config)

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val dispatcher: ExecutionContext = actorSystem.dispatcher

  implicit val timeout: Timeout = Timeout(5.seconds)

}
