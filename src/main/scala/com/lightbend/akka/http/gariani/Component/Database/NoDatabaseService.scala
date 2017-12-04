package com.lightbend.akka.http.gariani.Component.Database

import com.lightbend.akka.http.gariani.Component.Pdf.{EmptyClass, ObjectFileType}
import com.lightbend.akka.http.gariani.Custom.DataBaseError

class NoDatabaseService extends DatabaseBasic[NoDatabaseService] {

	override def insert(fileType: ObjectFileType): Either[Exception, ObjectFileType] = {
		Right(EmptyClass())
	}

	override def getByFileName(fileName: String): Either[DataBaseError, ObjectFileType] = {
		Right(EmptyClass())
	}

	override def collection(newCollectionName: String): NoDatabaseService = {
		this
	}

}
