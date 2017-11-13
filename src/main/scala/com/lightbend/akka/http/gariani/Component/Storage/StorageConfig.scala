package com.lightbend.akka.http.gariani.Component.Storage

import com.lightbend.akka.http.gariani.Component.Database.CouchDBService
import com.softwaremill.tagging._

/**
 * Created by daniel on 21/10/17.
 */

class StorageConfig(val minio: MinioConfig @@ MinioService, val s3: S3Config @@ S3Service)

case class MinioConfig(security: Boolean, url: String, port: Int, username: String, password: String, bucket: String)

case class S3Config(url: String, username: String, password: String, teste: String, teste2: String, listaOutraCoisa: Option[String], bucket: String)
