package com.lightbend.akka.http.gariani.Component.WebApi.Router

import java.io.File
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.http.javadsl.server.directives.FileInfo
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import akka.util.Timeout
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol
import com.lightbend.akka.http.gariani.Component.Pdf.HtmlObject
import com.lightbend.akka.http.gariani.Component.RabbitMQ.ConvertErrorActor.ConvertError
import com.lightbend.akka.http.gariani.Component.RabbitMQ.PublisherFileActor
import com.lightbend.akka.http.gariani.Component.RabbitMQ.PublisherFileActor.{NewFileConvert, NewFileWithParamConvert}
import com.lightbend.akka.http.gariani.Custom._
import com.lightbend.akka.http.gariani.Help.HelpPdf2Html
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor
import com.lightbend.akka.http.gariani.WebService.Actors.ConvertFileActor.{ConvertPdfDirectly, ConvertWithParam}
import com.softwaremill.tagging.@@

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
	* Created by daniel on 21/10/17.
	* reference: https://github.com/DanielaSfregola/quiz-management-service/
	* reference: https://github.com/knoldus/akka-http-file-upload/blob/master/src/main/scala/com/rishi/FileUpload.scala
	*/

trait SaveFileType

trait ConvertFileType

class RouteService(val saveFileActor: ActorRef @@ SaveFileType, val convertFile: ActorRef @@ ConvertFileType)(implicit val ac: ActorSystem, afm: ActorMaterializer, ec: ExecutionContext)
	extends Resource with PdfJsonProtocol {

	val publish = ac.actorOf(PublisherFileActor.props)

	val route: Route = {
		path("help") {
			get {
				complete(HttpEntity(ContentTypes.`application/json`, HelpPdf2Html.help))
			}
		} ~
			path("convert") {
				post {
					entity(as[Parameters]) { param =>
						implicit val timeout = Timeout(60 seconds)
						val pdfObject: Future[Either[Exception, HtmlObject]] =
							ask(convertFile, ConvertWithParam(param)).mapTo[Either[Exception, HtmlObject]]
						onSuccess(pdfObject) {
							case Right(pdf) => complete(pdf)
							case Left(error) =>
								print(error)
								complete(DataBaseError(error.getMessage))
						}
					}
				}
			} ~
			path("upload") {
				post {
					entity(as[Multipart.FormData]) { fileData =>
						val saveFile = new SaveFile()
						val saved = saveFile.saveFile(fileData)
						complete(saved)
					}
				}
			} ~
			path("uploadAndConvert") {
				post {
					entity(as[Multipart.FormData]) { (fileData: Multipart.FormData) =>
						val saveFile = new SaveFile()
						val saved = saveFile.saveFile(fileData)
						onComplete(saved) {
							case Success(s) =>
								publish ! NewFileConvert(s.get)
								complete(s)
							case Failure(s) =>
								publish ! ConvertError(s.toString)
								complete(s)
						}
					}
				}
			} ~
			path("convertWithParams") {
				post {
					entity(as[Parameters]) { param =>
						publish ! NewFileWithParamConvert(param)
						complete(StatusCodes.OK)
					}
				}
			} ~
			path("convertAllTogether") {
				post {
					def temDir(fileInfo: FileInfo): File =
						File.createTempFile(UUID.randomUUID().toString, ".pdf")

					storeUploadedFile("pdf", temDir) {
						case (metadata, file) =>
							implicit val timeout = Timeout(120 seconds)
							val convert = ac.actorOf(ConvertFileActor.props)
							val tmp = ask(convert, ConvertPdfDirectly(file.getAbsolutePath())).mapTo[Either[Exception, String]]
							convert ! PoisonPill
							onComplete(tmp) {
								case Success(c) =>
									c match {
										case Right(r) =>
											val htmlFile = new File(r).toPath
											val src = FileIO.fromPath(htmlFile).watchTermination() { (mat, futDone) =>
												futDone.onComplete { done =>
													htmlFile.toFile.delete()
													file.delete()
												}
												mat
											}
											complete(HttpEntity.Chunked.fromData(ContentType(MediaTypes.`application/octet-stream`), src))
										case Left(e) =>
											file.delete()
											complete(e.getMessage)
									}
								case Failure(f) =>
									print(s"${metadata}     ${file}")
									file.delete()
									complete(s"${f.getMessage}")
							}
					}
				}
			}
	}
}
