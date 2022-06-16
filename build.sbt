name := "MantlePlace"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)
scalaVersion := "2.13.8"

version := "1.0"

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
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.7.6" % "test",
  "io.gatling" % "gatling-test-framework" % "3.7.6" % "test"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.postgresql" % "postgresql" % "42.3.4"
)

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.3"

libraryDependencies ++= Seq(
  "org.scodec" %% "scodec-bits" % "1.1.31",
  "org.scorexfoundation" %% "scrypto" % "2.2.1",
  "org.bitcoinj" % "bitcoinj-core" % "0.16.1",
  "org.bouncycastle" % "bcpg-jdk15on" % "1.70"
)