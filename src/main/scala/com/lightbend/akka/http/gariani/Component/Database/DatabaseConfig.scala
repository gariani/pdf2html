package com.lightbend.akka.http.gariani.Component.Database

import com.softwaremill.tagging.@@

/**
 * Created by daniel on 18/10/17.
 */

class DataBaseConfig(val mongoDB: MongoDBConfig @@ MongoDBService, val couchDB: CouchDBConfig @@ CouchDBService)

case class CouchDBConfig(host: String, port: Int, username: String, password: String, databaseName: String)

case class MongoDBConfig(uri: String, username: String = "", password: String = "", databaseName: String,
												 collection: List[String])