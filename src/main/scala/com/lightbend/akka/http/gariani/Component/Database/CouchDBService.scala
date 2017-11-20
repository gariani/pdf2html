package com.lightbend.akka.http.gariani.Component.Database

import akka.actor.ActorSystem
import akka.util.Timeout
import com.lightbend.akka.http.gariani.Component.Pdf.{ObjectFileType, PdfObject}
import com.lightbend.akka.http.gariani.Custom.DataBaseError
import com.softwaremill.tagging.@@
import gnieh.sohva.CouchClient

import scala.util.Try

class CouchDBService(val couchDB: CouchDBConfig @@ CouchDBService)(implicit system: ActorSystem, implicit val timeout: Timeout) extends DatabaseBasic {

  lazy val couch: CouchClient = new CouchClient(couchDB.host, couchDB.port)

  lazy val session = Try(couch.startBasicSession(couchDB.username, couchDB.password))

  override def insert(fileType: ObjectFileType): Either[Exception, ObjectFileType] = ???

  override def getByFileName(fileName: String): Either[DataBaseError, PdfObject] = ???

}