package com.lightbend.akka.http.gariani.Component.WebApi

import com.softwaremill.tagging._

/**
 * Created by daniel on 21/10/17.
 */

class WebApiConfig(val akka: AkkaConfig @@ AkkaService)

case class AkkaConfig(host: String, port: Int)