package com.lightbend.akka.http.gariani.Component.Storage

import java.io.InputStream

import com.softwaremill.tagging.@@
import io.minio.MinioClient

import scala.util.{Failure, Success, Try}

class MinioService(val storage: MinioConfig @@ MinioService) extends StorageBasic[MinioService] {

	private lazy val minio: Try[MinioClient] = Try(new MinioClient(url, storage.username, storage.password, storage.security))

	private val url = s"${storage.url}:${storage.port}"

	override def getBucketName: String = {
		storage.bucket
	}

	override def buckerExist(bucketName: String): Boolean = {
		minio match {
			case Success(m) => m.bucketExists(bucketName)
			case Failure(m) => throw new Exception(s"An error occurred when checking if bucket exists: ${m.getMessage}")
		}
	}

	override def putObject(objectName: String, originalFilePath: String): Either[Exception, Boolean] = {
		try {
			minio match {
				case Success(m) =>
					m.putObject(storage.bucket, objectName, originalFilePath)
					Right(true)
				case Failure(m) => throw new Exception(s"An error occurred when put object: ${m.getMessage}")
			}
		} catch {
			case e: Exception => Left(e)
		}
	}

	override def statObject(bucketName: String, fileName: String): Either[Exception, ObjectStat] = {
		try {
			minio match {
				case Success(m) =>
					val objectStat = Try(m.statObject(bucketName, fileName))
					objectStat match {
						case Success(os) => Right(ObjectStat(os.bucketName(), os.name()))
						case Failure(fail) => throw new Exception(s"An error occurred when stat object: ${fail.getMessage}")
					}
				case Failure(m) => throw new Exception(s"An error occurred when stat object: ${m.getMessage}")
			}
		} catch {
			case e: Exception => Left(e)
		}
	}

	override def getObject(bucketName: String, fileName: String): Either[Exception, InputStream] = {
		try {
			minio match {
				case Success(m) => Right(m.getObject(bucketName, fileName))
				case Failure(m) => throw new Exception(s"An error occurred when get object: ${m} \n Message: ${m.getMessage}")
			}
		} catch {
			case e: Exception => Left(e)
		}
	}

}
