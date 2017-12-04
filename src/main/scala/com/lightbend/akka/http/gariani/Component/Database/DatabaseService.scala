package com.lightbend.akka.http.gariani.Component.Database

import com.lightbend.akka.http.gariani.Component.Pdf.{EmptyClass, ObjectFileType}
import com.lightbend.akka.http.gariani.Custom.DataBaseError

import scala.util.Try

/**
 * Created by daniel on 28/10/17.
 */

trait DatabaseBasic[T <: DatabaseBasic[T]] {

	def insert(fileType: ObjectFileType): Either[Exception, ObjectFileType] = {
		Right(EmptyClass())
	}

	def getByFileName(fileName: String): Either[DataBaseError, ObjectFileType] = {
		Right(EmptyClass())
	}

	def collection(newCollectionName: String): T = ???

}

