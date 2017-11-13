package com.lightbend.akka.http.gariani.Configuration

import com.lightbend.akka.http.gariani.Component.Database.DataBaseConfig
import com.lightbend.akka.http.gariani.Component.Storage.StorageConfig
import com.lightbend.akka.http.gariani.Component.WebApi.WebApiConfig
import com.softwaremill.tagging._
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.readers.ValueReader

/**
 * Created by daniel on 28/10/17.
 */

class ApplicationConfig(val webservice: WebApiConfig, val storage: StorageConfig, val database: DataBaseConfig)

object ApplicationConfig {

  lazy val rawConfig = ConfigFactory.load()

  import net.ceedubs.ficus.Ficus._
  import net.ceedubs.ficus.readers.ArbitraryTypeReader._

  implicit def taggedReader[TType: ValueReader, TTag] = implicitly[ValueReader[TType]].map(_.taggedWith[TTag])

  lazy val config: ApplicationConfig = rawConfig.as[ApplicationConfig]("app")

}

