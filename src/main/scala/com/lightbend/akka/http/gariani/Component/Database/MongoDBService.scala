package com.lightbend.akka.http.gariani.Component.Database

import com.lightbend.akka.http.gariani.Custom.Pdf.{ ObjectFileType, PdfObject }
import com.lightbend.akka.http.gariani.Custom.{ DataBaseError }
import com.softwaremill.tagging.@@
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{ MongoClient, MongoCollection, MongoDatabase, _ }

import scala.concurrent.Await
import scala.concurrent.duration._

class MongoDBService(val mongoDB: MongoDBConfig @@ MongoDBService) extends DatabaseBasic {

  private lazy val mongoClient: MongoClient = MongoClient.apply(mongoDB.uri)

  private lazy val codec = fromRegistries(fromProviders(classOf[ObjectFileType]), DEFAULT_CODEC_REGISTRY)

  private lazy val database: MongoDatabase = mongoClient.getDatabase("pdf2html").withCodecRegistry(codec)

  private lazy val collection: MongoCollection[ObjectFileType] = database.getCollection("pdf2html")

  override def insert(fileType: ObjectFileType): Either[Exception, ObjectFileType] = {
    try {
      val totalItems = for {
        a1 <- collection.insertOne(fileType)
        inCollection <- collection.count()
      } yield inCollection
      val res = Await.result(totalItems.toFuture(), 2.seconds)
      Right(fileType)
    } catch {
      case e: Exception => Left(e);
    }
  }

  override def getByFileName(fileName: String): Either[DataBaseError, ObjectFileType] = {
    try {
      val findObservable = collection.find(equal("fileName", fileName))
      val findPDF = for (pdf <- findObservable) yield pdf
      val found = Await.result(findPDF.toFuture(), 2.seconds)
      found.isEmpty match {
        case true => Left(DataBaseError(s"File '${fileName}' not found."))
        case false => Right(found.head)
      }
    } catch {
      case e: Exception => Left(DataBaseError(e.getMessage))
    }
  }

}