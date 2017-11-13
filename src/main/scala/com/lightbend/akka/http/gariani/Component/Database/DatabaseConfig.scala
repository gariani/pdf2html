package com.lightbend.akka.http.gariani.Component.Database

import com.softwaremill.tagging.@@

/**
 * Created by daniel on 18/10/17.
 */

class DataBaseConfig(val mongoDB: MongoDBConfig @@ MongoDBService, val couchDB: CouchDBConfig @@ CouchDBService)

case class CouchDBConfig(val host: String, val port: Int, val username: String, val password: String, val databaseName: String)

case class MongoDBConfig(val uri: String)