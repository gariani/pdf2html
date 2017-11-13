package com.lightbend.akka.http.gariani.Component.WebApi.Router

import akka.http.scaladsl.marshalling.{ Marshal, ToResponseMarshallable }
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.{ Directives, Route }
import com.lightbend.akka.http.gariani.Component.Json.PdfJsonProtocol

import scala.concurrent.{ ExecutionContext, Future }

trait Resource extends Directives with PdfJsonProtocol {

  def completeWithLocationHeader[T](resourceUrl: Future[Option[T]], ifDefinedStatus: Int, ifEmptyStatus: Int): Route =
    onSuccess(resourceUrl) {
      case Some(t) => completeWithLocationHeader(ifDefinedStatus, t)
      case None => complete(ifEmptyStatus, None)
    }

  def completeWithLocationHeader[T](status: Int, resourceUrl: T): Route =
    extractRequestContext { requestContext =>
      val request = requestContext.request
      val location = request.uri.copy(path = request.uri.path / resourceUrl.toString)
      respondWithHeader(Location(location)) {
        complete(status, None)
      }

    }
}