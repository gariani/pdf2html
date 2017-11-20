package com.lightbend.akka.http.gariani.Component.Json

import java.nio.charset.Charset

import com.spingo.op_rabbit._
import play.api.libs.json._

/**
	* Created by daniel on 23/09/17.
	* reference: https://github.com/SpinGo/op-rabbit/blob/master/addons/play-json/src/main/scala/com/spingo/op_rabbit/PlayJsonSupport.scala
	*/

object JsonSupport {

	private val utf8 = Charset.forName("UTF-8")

	implicit def playJsonRabbitMarshaller[T](implicit writer: Writes[T]): RabbitMarshaller[T] = {
		new RabbitMarshaller[T] {
			protected val contentType = "application/json"
			private val encoding = "UTF-8"
			protected val contentEncoding = Some(encoding)

			def marshall(value: T) =
				Json.stringify(writer.writes(value)).getBytes(utf8)
		}
	}

	implicit def playJsonRabbitUnmarshaller[T](implicit reads: Reads[T]): RabbitUnmarshaller[T] = {
		new RabbitUnmarshaller[T] {
			def unmarshall(value: Array[Byte], contentType: Option[String], charset: Option[String]): T = {
				contentType match {
					case Some(value) if (value != "application/json" && value != "text/json") =>
						throw MismatchedContentType(value, "application/json")
					case _ =>
						val str = try {
							new String(
								value,
								charset map (Charset.forName) getOrElse utf8
							)
						} catch {
							case ex: Throwable =>
								throw new GenericMarshallingException(
									s"Could not convert input to charset of type ${charset}; ${ex.toString}"
								)
						}

						val json = try {
							Json.parse(str)
						} catch {
							case ex: Throwable =>
								throw InvalidFormat(str, ex.toString)
						}

						Json.fromJson[T](json) match {
							case JsSuccess(v, _) =>
								v
							case JsError(errors) =>
								throw InvalidFormat(
									json.toString,
									JsError.toJson(errors).toString
								)
						}
				}
			}
		}
	}
}