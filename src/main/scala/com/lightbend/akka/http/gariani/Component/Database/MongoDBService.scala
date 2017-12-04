package com.lightbend.akka.http.gariani.Component.Database

import com.lightbend.akka.http.gariani.Component.Pdf.{ObjectFileType, PdfObject}
import com.lightbend.akka.http.gariani.Custom.DataBaseError
import com.mongodb.MongoCredential._
import com.softwaremill.tagging.@@
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{MongoClient, MongoCollection, _}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

class MongoDBService(val mongoDB: MongoDBConfig @@ MongoDBService) extends DatabaseBasic[MongoDBService] {

	private lazy val codec = fromRegistries(fromProviders(classOf[ObjectFileType]), DEFAULT_CODEC_REGISTRY)
	private val clusterSettings: ClusterSettings = ClusterSettings
		.builder().hosts(List(new ServerAddress(mongoDB.uri)).asJava).build()
	private val password: Array[Char] = mongoDB.password.toCharArray
	private val credential: MongoCredential = createCredential(mongoDB.username, mongoDB.databaseName, password)
	private val settings: MongoClientSettings = MongoClientSettings
		.builder().clusterSettings(clusterSettings).credentialList(List(credential).asJava).build()
	private val mongoClient: MongoClient = MongoClient(settings)
	private val dataBase: MongoDatabase = mongoClient.getDatabase(mongoDB.databaseName).withCodecRegistry(codec)
	private var collectionName: String = "pdf2html"

	override def collection(newCollectionName: String): MongoDBService = {
		collectionName = newCollectionName
		this
	}

	override def insert(fileType: ObjectFileType): Either[Exception, ObjectFileType] = {
		try {
			val totalItems = for {
				a1 <- mongoColl.insertOne(fileType)
				inCollection <- mongoColl.count()
			} yield a1
			val res = Await.result(totalItems.toFuture(), 5.seconds)
			Right(fileType)
		} catch {
			case e: Exception =>
				print(e.getCause + "    " + e.printStackTrace())
				Left(e);
		}
	}

	private def mongoColl: MongoCollection[ObjectFileType] = {
		val mongoCollection: MongoCollection[ObjectFileType] = dataBase.getCollection(collectionName)
		mongoCollection
	}

	override def getByFileName(fileName: String): Either[DataBaseError, ObjectFileType] = {
		try {
			val findObservable = mongoColl.find(equal("fileName", fileName))
			val findPDF = for (pdf <- findObservable) yield pdf
			val found = Await.result(findPDF.toFuture(), 2.seconds)
			found.isEmpty match {
				case true => Left(DataBaseError(s"File '${fileName}' not found."))
				case false => Right(found.head.asInstanceOf[PdfObject])
			}
		} catch {
			case e: Exception => Left(DataBaseError(e.getMessage))
		}
	}

	private def close() = {
		mongoClient.close()
	}

}