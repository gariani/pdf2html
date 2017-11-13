lazy val akkaHttpVersion = "10.0.10"
lazy val akkaVersion = "2.5.4"
lazy val circeVersion = "0.8.0"

lazy val root = (project in file(".")).
	settings(

		inThisBuild(List(
			organization := "com.example",
			scalaVersion := "2.12.3"
		)),
		name := "pdf2html",
		libraryDependencies ++= Seq(
			"com.iheart" %% "ficus" % "1.4.2",
			"com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
			"com.softwaremill.macwire" %% "proxy" % "2.3.0",
			"com.softwaremill.macwire" % "macrosakka_2.12" % "2.3.0",
			"com.softwaremill.macwire" % "util_2.12" % "2.3.0",
			"ch.qos.logback" % "logback-classic" % "1.2.3",
			"com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
			"io.minio" % "minio" % "3.0.8",
			"org.mongodb.scala" % "mongo-scala-driver_2.12" % "2.1.0",
			"org.gnieh" %% "sohva" % "2.1.0",
			"com.github.scopt" %% "scopt" % "3.7.0",
			"com.typesafe.akka" %% "akka-actor" % akkaVersion,
			"com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
			"com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
			"com.typesafe.akka" %% "akka-stream" % akkaVersion,
			"com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
			"org.scalatest" %% "scalatest" % "3.0.1" % Test,
			"io.circe" %% "circe-core" % circeVersion,
			"io.circe" %% "circe-generic" % circeVersion,
			"io.circe" %% "circe-parser" % circeVersion,
			"com.wix" % "accord-core_2.12" % "0.7.1",
			"org.apache.pdfbox" % "pdfbox" % "2.0.8",
			"commons-io" % "commons-io" % "2.5"
		)
	)

initialize ~= {_ => System.setProperty("macwire.debug", "")}
