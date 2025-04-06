ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.2"

val tapirVersion = "1.7.3"

lazy val root = (project in file("."))
  .settings(
    name := "rec-server-travel-designer"
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.13",
  "dev.zio" %% "zio-streams" % "2.0.13",

  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.2.10",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-redoc-bundle" % tapirVersion,
  "org.http4s" %% "http4s-blaze-server" % "0.23.14",

  "ch.qos.logback" % "logback-classic" % "1.4.7",

  // Circe
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",
  "com.softwaremill.sttp.tapir" %% "tapir-newtype" % "1.9.10",


  "dev.zio" %% "zio-interop-cats" % "23.1.0.0",
  "dev.zio" %% "zio-prelude" % "1.0.0-RC21",
  "dev.zio" %% "zio-test" % "2.0.15" % Test,

  // doobie
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
  "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC4",

  "com.github.pureconfig" %% "pureconfig" % "0.17.1",

  // testcontainers
  "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % "0.10.0",

  "org.apache.directory.studio" % "org.apache.commons.io" % "2.4",

  "joda-time" % "joda-time" % "2.9.3",

  "com.softwaremill.sttp.client3" %% "core" % "3.9.0",
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.9.0",
  "com.softwaremill.sttp.client3" %% "circe" % "3.9.0"
)

Compile / mainClass := Some("Main")
assembly / mainClass := Some("Main")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") => MergeStrategy.first
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
  //  case "pom.properties" => MergeStrategy.first
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x => MergeStrategy.first
}

assembly / assemblyJarName := "main-travel-designer.jar"