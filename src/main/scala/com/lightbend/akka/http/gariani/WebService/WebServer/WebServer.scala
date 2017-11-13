package com.lightbend.akka.http.gariani.WebService.WebServer

import com.lightbend.akka.http.gariani.Component.ConfigAkkaComponent
import com.lightbend.akka.http.gariani.Component.WebApi.{ WebApiAkka }

object WebServer extends App with ConfigAkkaComponent {

  lazy val webApi = webApiService.asInstanceOf[WebApiAkka]

  webApi.bind.foreach { binding =>
    webApi.afterStart(binding)
    sys.addShutdownHook {
      webApi.beforeStop(binding)
    }
  }
}
