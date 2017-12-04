package com.lightbend.akka.http.gariani.Component

/**
 * Created by daniel on 25/10/17.
 */

import akka.actor.ActorRef
import com.lightbend.akka.http.gariani.Component.Database.{DatabaseBasic, MongoDBService, NoDatabaseService}
import com.lightbend.akka.http.gariani.Component.RabbitMQ.RabbitMQService
import com.lightbend.akka.http.gariani.Component.Storage.{MinioService, NoStorageService, StorageBasic}
import com.lightbend.akka.http.gariani.Component.WebApi.Router.{ConvertFileType, RouteService, SaveFileType}
import com.lightbend.akka.http.gariani.Component.WebApi._
import com.lightbend.akka.http.gariani.WebService.Actors.{ConvertFileActor, SaveFileActors}
import com.softwaremill.macwire._
import com.softwaremill.tagging._

trait ConfigDataPersistence {

  import com.lightbend.akka.http.gariani.Configuration.ApplicationConfig.config.storage._
  import com.lightbend.akka.http.gariani.Configuration.ApplicationConfig.config.database._

  def storageService: StorageBasic[NoStorageService] = wire[NoStorageService]

  def databaseService: DatabaseBasic[NoDatabaseService] = wire[NoDatabaseService]
}

trait ConfigAkkaComponent extends AkkaActorSystemConfig {

  val saveFileActor: ActorRef @@ SaveFileType =
    actorSystem.actorOf(SaveFileActors.props, "saveFileActor").taggedWith[SaveFileType]
  val converFileActor: ActorRef @@ ConvertFileType =
    actorSystem.actorOf(ConvertFileActor.props, "converFileActor").taggedWith[ConvertFileType]

  import com.lightbend.akka.http.gariani.Configuration.ApplicationConfig.config.webservice._

  val routeService: RouteService = wire[RouteService]
  val webApiService: WebApiBasic = wire[AkkaService]
}

trait ConfigRabbitMQComponent extends AkkaActorSystemConfig {

  val rabbitMQService: RabbitMQService = wire[RabbitMQService]

}