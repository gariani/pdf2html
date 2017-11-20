package com.lightbend.akka.http.gariani.Component.WebApi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.lightbend.akka.http.gariani.Component.RabbitMQ.Supervisor
import com.lightbend.akka.http.gariani.Component.RabbitMQ.Supervisor.{Begin, End}
import com.lightbend.akka.http.gariani.Component.WebApi.Router.RouteService
import com.softwaremill.tagging._
import org.slf4j.LoggerFactory

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

/**
 * Created by daniel on 21/10/17.
 * reference: https://github.com/ketilovre/akka-http-macwire
 */

trait WebApiBasic

trait WebApiAkka extends WebApiBasic {

  def bind: Future[ServerBinding]

  def afterStart(binding: ServerBinding): Unit

  def beforeStop(binding: ServerBinding): Unit

}

class AkkaService(val route: RouteService, val akkaConfig: AkkaConfig @@ AkkaService)
								 (implicit val ac: ActorSystem, afm: ActorMaterializer, ec: ExecutionContext)
	extends WebApiAkka {

  private val logger = LoggerFactory.getLogger("server")

	val supervisor = ac.actorOf(Supervisor.props)

  def bind: Future[ServerBinding] = {
    Http(ac).bindAndHandle(route.route, akkaConfig.host, akkaConfig.port)
  }

  def afterStart(binding: ServerBinding): Unit = {
    logger.info(s"Server started on ${binding.localAddress.toString}")
		supervisor ! Begin
  }

  def beforeStop(binding: ServerBinding): Unit = {
    Await.ready({
			supervisor ! End
      binding.unbind().map { _ =>
        logger.info("Shutting down")
      }
    }, 1.minute)
  }

}