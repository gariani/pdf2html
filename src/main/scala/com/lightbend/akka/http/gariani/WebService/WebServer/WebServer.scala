package com.lightbend.akka.http.gariani.WebService.WebServer

import com.lightbend.akka.http.gariani.Component.{ConfigAkkaComponent, ConfigRabbitMQComponent}
import com.lightbend.akka.http.gariani.Component.WebApi.WebApiAkka

object WebServer
  extends App with ConfigAkkaComponent with ConfigRabbitMQComponent {

  lazy val webApi = webApiService.asInstanceOf[WebApiAkka]

  webApi.bind.foreach { binding =>
    webApi.afterStart(binding)
    sys.addShutdownHook {
      webApi.beforeStop(binding)
    }
  }
}
