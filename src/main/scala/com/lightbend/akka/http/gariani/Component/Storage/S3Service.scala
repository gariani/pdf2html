package com.lightbend.akka.http.gariani.Component.Storage

import java.io.InputStream

import com.softwaremill.tagging.@@


class S3Service(storage: S3Config @@ S3Service) extends StorageBasic[S3Service] {

	def getBucketName: String = ???

	override def buckerExist(bucketName: String): Boolean = true

	override def putObject(objectName: String, originalPathFile: String): Either[Exception, Boolean] = ???

	override def statObject(bucket: String, fileName: String) = ???

	override def getObject(bucket: String, fileName: String): Either[Exception, InputStream] = ???

}
