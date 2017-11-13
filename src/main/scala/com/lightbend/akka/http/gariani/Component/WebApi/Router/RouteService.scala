package com.lightbend.akka.http.gariani.Component.WebApi.Router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol
import com.lightbend.akka.http.gariani.Custom.Pdf.{HtmlObject, ObjectFileType, PdfObject}
import com.lightbend.akka.http.gariani.Custom._
import com.lightbend.akka.http.gariani.Help.HelpPdf2Html
import com.lightbend.akka.http.gariani.WebService.Actors.{ConvertFile, GetMultiPart}
import com.softwaremill.tagging.@@

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by daniel on 21/10/17.
 * reference: https://github.com/DanielaSfregola/quiz-management-service/
 * reference: https://github.com/knoldus/akka-http-file-upload/blob/master/src/main/scala/com/rishi/FileUpload.scala
 */

trait SaveFileType
trait ConvertFileType

class RouteService(val saveFileActor: ActorRef @@ SaveFileType, val convertFile: ActorRef @@ ConvertFileType)
                  (implicit val ac: ActorSystem, afm: ActorMaterializer, ec: ExecutionContext)
    extends Resource with PdfJsonProtocol {

  val route = {
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
              ask(convertFile, ConvertFile(param)).mapTo[Either[Exception, HtmlObject]]
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
            implicit val timeout = Timeout(60 seconds)
            val pdfObject: Future[Option[PdfObject]] = ask(
              saveFileActor,
              GetMultiPart(fileData)
            ).mapTo[Option[PdfObject]]
            onSuccess(pdfObject) { result =>
              complete(result.get)
            }
          }
        } ~
          complete("I don't understand!")
      }
  }
}
