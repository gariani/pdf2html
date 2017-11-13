package com.lightbend.akka.http.gariani.WebService.Actors

import java.io.{File, FileOutputStream, InputStream}
import java.util.UUID

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.Multipart
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.lightbend.akka.http.gariani.Component.ConfigDataPersistence
import com.lightbend.akka.http.gariani.Component.Storage.ObjectStat
import com.lightbend.akka.http.gariani.Custom.Pdf.{HtmlObject, ObjectFileType, PdfObject}
import com.lightbend.akka.http.gariani.Custom.{DataBaseError, Parameter, Parameters}
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.FileUtils

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._

case class GetMultiPart(formData: Multipart.FormData)

case class ConvertFile(param: Parameters)

object ConvertFileActors {
	def props(implicit materializer: ActorMaterializer, dispatcher: ExecutionContext) = Props(new ConvertFileActor())
}

class ConvertFileActor(implicit materializer: ActorMaterializer, dispatcher: ExecutionContext)
	extends ConfigDataPersistence with Actor with LazyLogging {

	override def receive = {
		case ConvertFile(param) =>
			val originalSender = sender()
			preparToConvertToHtml(param) pipeTo originalSender
	}

	def preparToConvertToHtml(param: Parameters): Future[Either[Exception, ObjectFileType]] = Future {
		try {
			val result = for {
				ep <- extractParameters(param)
				extracPath <- extractPath(ep.url)
				vfe <- validateFileExists(extracPath(2))
				vbe <- validateFileIsInTheStorage(extracPath(1), extracPath(2))
			} yield convertPdfToHtml(vbe, vfe, param)

			if (result.isRight) {
				result.right.get
			} else {
				throw new Exception(s"Error during convert PDF to HTML. Erro: ${result.left.get}")
			}
		} catch {
			case e: Exception => Left(e)
		}
	}

	private def convertPdfToHtml(bucket: ObjectStat, pdfObejct: PdfObject, param: Parameters): Either[Exception, HtmlObject] = {
		try {
			val process = for {
				file <- storageService.getObject(bucket.bucketName, pdfObejct.fileName)
				savedPath <- saveLocalFile(file, pdfObejct.fileName)
				seq <- generateOSParameters(param, savedPath)
				output <- generateHtmlFile(bucket, pdfObejct, seq)
				htmlFile <- sendToStorage(pdfObejct.fileName)
				htmlObject <- Right(HtmlObject.apply(bucket.bucketName, htmlFile))
				data <- databaseService.insert(htmlObject)
			} yield htmlObject

			if (process.isRight) {
				Right(process.right.get)
			} else {
				Left(process.left.get)
			}
		} catch {
			case e: Exception => Left(e)
		}
	}

	private def sendToStorage(fileName: String): Either[Exception, String] = {
		try {
			val htmlFile = replaceHtmlExt(fileName)
			val htmlPath = getTempPath + htmlFile
			storageService.putObject(htmlFile, htmlPath)
			delete(htmlPath)
			Right(htmlFile)
		} catch {
			case e: Exception => Left(e)
		}
	}

	private def delete(file: String): Either[Exception, Boolean] = {
		try {
			val result = FileUtils.deleteQuietly(new File(file))
			Right(result)
		} catch {
			case e: Exception => Left(e)
		}
	}

	private def replaceHtmlExt(filePath: String): String = {
		replace(filePath, "\\.[^.]*$")
	}

	private def replace(s: String, r: String): String = {
		s.replaceAll(r, "") + ".html"
	}

	private def generateHtmlFile(bucket: ObjectStat, pdfObejct: PdfObject, seq: Seq[String]): Either[Exception, String] = {
		try {
			val result = Process(seq).!!
			Right(result)
		} catch {
			case e: Exception => Left(e)
		}
	}

	private def generateOSParameters(param: Parameters, filePath: String): Either[Exception, Seq[String]] = {
		try {
			param.firstPage match {
				case Some(firstPage) =>
					val programName = Seq("pdf2htmlEX")
					val seq = returnListParam(param)
					Right(programName ++ seq ++ Seq(filePath) ++ Seq("--dest-dir") ++ Seq(getTempPath))
				case None => Right(Seq())
			}
		} catch {
			case e: Exception => Left(e)
		}
	}

	private def getTempPath: String = {
		System.getProperty("java.io.tmpdir") + File.separator
	}

	private def returnListParam(param: Parameters): Seq[String] = {
		var seq: Seq[String] = Seq()
		if (param.firstPage.isDefined) {
			seq ++= Seq("--first-page", param.firstPage.get.toString)
		}
		if (param.lastPage.isDefined) {
			seq ++= Seq("--last-page", param.lastPage.get.toString)
		}
		seq
	}

	private def saveLocalFile(fileIn: InputStream, fileName: String): Either[Exception, String] = {
		try {
			val path = s"${getTempPath}${fileName}"
			val fileOut = new FileOutputStream(path)
			org.apache.commons.io.IOUtils.copy(fileIn, fileOut)
			fileOut.close()
			Right(path)
		} catch {
			case e: Exception => Left(e)
		}
	}

	private def validateFileIsInTheStorage(bucketName: String, fileName: String): Either[Exception, ObjectStat] = {
		if (storageService.buckerExist(bucketName)) {
			storageService.statObject(bucketName, fileName)
		} else
			Left(throw new Exception(s"No bucket found with this name '${bucketName}'"))
	}

	private def extractPath(filePath: String): Either[Exception, List[String]] = {
		val path = filePath.split("/")
		if (path.tail.size.==(2)) {
			Right(path.toList)
		} else {
			Left(throw new Exception(s"There is not correspondent path like this ${path}"))
		}
	}

	private def validateFileExists(fileName: String): Either[DataBaseError, PdfObject] = {
		databaseService.getByFileName(fileName) match {
			case Right(found) => Right(found.asInstanceOf[PdfObject])
			case Left(l) => Left(l)
		}
	}

	private def extractParameters(parameters: Parameters): Either[DataBaseError, Parameters] = {
		Parameter.validParameters(parameters) match {
			case Right(param) => Right(param)
			case Left(fail) => Left(DataBaseError(fail))
		}
	}

}
