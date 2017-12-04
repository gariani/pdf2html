package com.lightbend.akka.http.gariani.WebService.Actors

import java.io.FileOutputStream
import java.util.UUID

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.Multipart
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.lightbend.akka.http.gariani.Component.ConfigDataPersistence
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor.GetMultiPart

import scala.concurrent.{ExecutionContext, Future}

object SaveFileActors {
	def props(implicit materializer: ActorMaterializer, dispatcher: ExecutionContext) = Props(new SaveFileActor())
}

class SaveFileActor(implicit materializer: ActorMaterializer, dispatcher: ExecutionContext)
	extends ConfigDataPersistence with Actor {

	override def receive: akka.actor.Actor.Receive = {
		case GetMultiPart(formPart) =>
			val originalSender = sender()
			saveFile(formPart) pipeTo originalSender
	}

	def saveFile(fileData: Multipart.FormData): Future[Option[PdfObject]] = {
		val fileName = UUID.randomUUID().toString + ".pdf"
		val temp = System.getProperty("java.io.tmpdir")
		val filePath = temp + "/" + fileName
		processFile(filePath, fileData).map {
			case size: Int if size > 0 =>
				val obj = PdfObject.apply(storageService.getBucketName, fileName, size)
				databaseService.collection("pdf2html").insert(obj)
				storageService.putObject(fileName, filePath)
				Some(obj)
			case _ => None
		}
	}

	private def processFile(filePath: String, fileData: Multipart.FormData): Future[Int] = {
		val fileOutput = new FileOutputStream(filePath)
		fileData.parts.mapAsync(1) { bodyPart ⇒
			def writeFileOnLocal(array: Array[Byte], byteString: ByteString): Array[Byte] = {
				val byteArray: Array[Byte] = byteString.toArray
				fileOutput.write(byteArray)
				array ++ byteArray
			}

			bodyPart.entity.dataBytes.runFold(Array[Byte]())(writeFileOnLocal)
		}.runFold(0)(_ + _.length)
	}

}
