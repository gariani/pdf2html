package com.lightbend.akka.http.gariani.Custom

import java.io.{File, FileOutputStream}
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Multipart
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.lightbend.akka.http.gariani.Component.ConfigDataPersistence
import com.lightbend.akka.http.gariani.Component.Pdf.PdfObject
import com.lightbend.akka.http.gariani.Component.RabbitMQ.PublisherFileActor.NewFileConvert
import com.lightbend.akka.http.gariani.Component.RabbitMQ.PublisherFileActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
	* Created by daniel on 21/10/17.
	* reference: https://gist.github.com/jrudolph/08d0d28e1eddcd64dbd0#file-testmultipartfileupload-scala-L52
	*/

class SaveFile(implicit ac: ActorSystem, afm: ActorMaterializer)
	extends ConfigDataPersistence {

	def saveFile(fileData: Multipart.FormData): Future[Option[PdfObject]] = {
		val fileName = UUID.randomUUID().toString + ".pdf"
		val temp = System.getProperty("java.io.tmpdir") + "/" + fileName
		processFile(temp, fileData).map {
			case size: Int if size > 0 =>
				val obj = PdfObject.apply(storageService.getBucketName, fileName, size)
				databaseService.collection("pdf2html").insert(obj)
				storageService.putObject(fileName, temp)
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
