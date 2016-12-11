
name := "users-service"

organization := "com.hamlazot"

version := "SNAPSHOT-0.1.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.10"


libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.hamlazot" %% "common-domain" % "0.1.0-SNAPSHOT",
  "com.hamlazot" %% "common-http" % "0.1.0-SNAPSHOT",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
  "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",
  "com.lihaoyi" %% "upickle" % "0.4.3",
  "com.websudos" %% "phantom-dsl" % "1.29.5",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % Test,
  "org.specs2" %% "specs2-core" % "3.8.6" % Test
)

resolvers ++= Seq(
  Classpaths.typesafeReleases,
  Resolver.bintrayRepo("websudos", "oss-releases")
)