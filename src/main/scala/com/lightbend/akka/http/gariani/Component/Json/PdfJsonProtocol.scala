package com.lightbend.akka.http.gariani.Component.Json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.lightbend.akka.http.gariani.Component.Pdf.{HtmlObject, PdfObject}
import com.lightbend.akka.http.gariani.Custom.{DataBaseError, Parameters}
import play.api.libs.json.Json
import spray.json.DefaultJsonProtocol

trait PdfJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val parametersFormat = jsonFormat3(Parameters)
	implicit val pdfObjectFormat = jsonFormat4(PdfObject.apply)
	implicit val pdfObject = Json.format[PdfObject]
	implicit val databaseErrorFormat = jsonFormat1(DataBaseError)
	implicit val htmlObject = jsonFormat3(HtmlObject.apply)
}
