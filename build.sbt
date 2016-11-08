name := "users-service"

version := "1.0"

scalaVersion := "2.11.8"

val akka = "2.4.10"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.akka" %% "akka-actor" % akka,
  "org.scalaz" %% "scalaz-core" % "7.2.2",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.hamlazot" %% "common-domain" % "SNAPSHOT-0.1.0"
)