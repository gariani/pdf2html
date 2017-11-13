package com.lightbend.akka.http.gariani.Component

/**
 * Created by daniel on 25/10/17.
 */

import akka.actor.ActorRef
import com.lightbend.akka.http.gariani.Component.Database.{DatabaseBasic, MongoDBService}
import com.lightbend.akka.http.gariani.Component.Storage.{MinioService, StorageBasic}
import com.lightbend.akka.http.gariani.Component.WebApi.Router.{ConvertFileType, RouteService, SaveFileType}
import com.lightbend.akka.http.gariani.Component.WebApi._
import com.lightbend.akka.http.gariani.WebService.Actors.{ConvertFileActors, SaveFileActor, SaveFileActors}
import com.softwaremill.macwire._
import com.softwaremill.macwire.akkasupport._
import com.softwaremill.tagging._

trait ConfigDataPersistence {

  import com.lightbend.akka.http.gariani.Configuration.ApplicationConfig.config.storage._
  import com.lightbend.akka.http.gariani.Configuration.ApplicationConfig.config.database._

  def storageService: StorageBasic = wire[MinioService]
  def databaseService: DatabaseBasic = wire[MongoDBService]
}

trait ConfigAkkaComponent extends AkkaActorSystemConfig {

  val saveFileActor: ActorRef @@ SaveFileType =
		actorSystem.actorOf(SaveFileActors.props, "saveFileActor").taggedWith[SaveFileType]
  val converFileActor: ActorRef @@ ConvertFileType =
		actorSystem.actorOf(ConvertFileActors.props, "converFileActor").taggedWith[ConvertFileType]

  import com.lightbend.akka.http.gariani.Configuration.ApplicationConfig.config.webservice._

  val routeService: RouteService = wire[RouteService]
  val webApiService: WebApiBasic = wire[AkkaService]
}