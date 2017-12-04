package com.lightbend.akka.http.gariani.Component.Storage

import java.io.InputStream

/**
 * Created by daniel on 21/10/17.
 */

case class ObjectStat(bucketName: String, name: String)

trait StorageBasic[T <: StorageBasic[T]] {

  def getBucketName: String

  def buckerExist(bucketName: String): Boolean

  def putObject(objectName: String, originalPathFile: String): Either[Exception, Boolean]

  def statObject(bucketName: String, fileName: String): Either[Exception, ObjectStat]

  def getObject(bucketName: String, fileName: String): Either[Exception, InputStream]

}

