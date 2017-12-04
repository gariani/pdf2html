package com.lightbend.akka.http.gariani.Component.Storage

import java.io.InputStream

class NoStorageService extends StorageBasic[NoStorageService] {

	def getBucketName: String = "documentos"

	override def buckerExist(bucketName: String): Boolean = true

	override def putObject(objectName: String, originalPathFile: String): Either[Exception, Boolean] = {
		Right(false)
	}

	override def statObject(bucket: String, fileName: String): Either[Exception, ObjectStat] = {
		Right(ObjectStat("", ""))
	}

	override def getObject(bucket: String, fileName: String): Either[Exception, InputStream] = ???

}
