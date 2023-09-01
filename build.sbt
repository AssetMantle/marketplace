name := "MantlePlace"

maintainer := "admin@assetmantle.one"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)
scalaVersion := "2.13.11"

version := sys.env.getOrElse("APP_VERSION", "dev")

lazy val GatlingTest = config("gatling") extend Test

GatlingTest / scalaSource := baseDirectory.value / "gatling/simulation"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Maven Central Server" at "https://repo1.maven.org/maven2"

libraryDependencies ++= Seq(ws, specs2 % Test, guice, caffeine)

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.8.4" % "test",
  "io.gatling" % "gatling-test-framework" % "3.8.4" % "test"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
  "org.postgresql" % "postgresql" % "42.5.4"
)

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.2",
  "org.json" % "json" % "20230227"
)

libraryDependencies ++= Seq(
  "com.sksamuel.scrimage" % "scrimage-core" % "4.0.33",
  "com.sksamuel.scrimage" %% "scrimage-scala" % "4.0.33"
)

libraryDependencies ++= Seq(
  "org.scodec" %% "scodec-bits" % "1.1.37",
  "org.scorexfoundation" %% "scrypto" % "2.3.0",
  "org.bitcoinj" % "bitcoinj-core" % "0.16.2",
  "org.bouncycastle" % "bcpg-jdk15on" % "1.70"
)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.12.470",
  "com.amazonaws" % "aws-java-sdk" % "1.12.470"
)

libraryDependencies += "com.google.protobuf" % "protobuf-java" % "3.22.2"

libraryDependencies += "com.typesafe" % "config" % "1.4.2"