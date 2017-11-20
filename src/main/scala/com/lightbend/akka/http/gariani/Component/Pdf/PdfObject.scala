package com.lightbend.akka.http.gariani.Component.Pdf

import org.mongodb.scala.bson.ObjectId

sealed class ObjectFileType

object PdfObject {

	def apply(bucketName: String, fileName: String, size: Long): PdfObject =
    new PdfObject(new ObjectId().toString, bucketName, fileName, size)

  import com.wix.accord._
  import dsl._

	implicit private val pdfValidator = validator[PdfObject] { pdf =>
		pdf._id as "id" is notNull
		pdf.bucketName as "bucket name" is notNull
  }

	def validaPdf(pdf: PdfObject): Result = {
    validate(pdf)
  }
}

case class PdfObject(_id: String, bucketName: String, fileName: String, size: Long) extends ObjectFileType

object HtmlObject {
  def apply(bucketName: String, fileName: String): HtmlObject =
    new HtmlObject(new ObjectId().toString, bucketName, fileName)
}

case class HtmlObject(_id: String, bucketName: String, fileName: String) extends ObjectFileType

