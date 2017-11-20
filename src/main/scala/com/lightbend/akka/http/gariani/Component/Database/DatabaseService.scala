package com.lightbend.akka.http.gariani.Component.Database

import com.lightbend.akka.http.gariani.Component.Pdf.ObjectFileType
import com.lightbend.akka.http.gariani.Custom.DataBaseError

import scala.util.Try

/**
 * Created by daniel on 28/10/17.
 */

case class Get(dbName: String, document: String)

case class Post(dbName: String, document: String)

case class Put(dbName: String, document: String)

case class Delete(dbName: String, document: String)

trait DatabaseBasic {

  def insert(fileType: ObjectFileType): Either[Exception, ObjectFileType]

  def getByFileName(fileName: String): Either[DataBaseError, ObjectFileType]

}

