name := "MantlePlace"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)
scalaVersion := "2.13.8"

version := "0.1.2"

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

resolvers ++= Seq(
  Resolver.url("Edulify Repository", url("https://edulify.github.io/modules/releases/"))(Resolver.ivyStylePatterns)
)


libraryDependencies ++= Seq(
  "com.edulify" %% "sitemap-module" % "2.1.0"
)


libraryDependencies ++= Seq(ws, specs2 % Test, guice, caffeine)

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.7.6" % "test",
  "io.gatling" % "gatling-test-framework" % "3.7.6" % "test"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.2",
  "org.postgresql" % "postgresql" % "42.3.6"
)

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.3",
  "org.json" % "json" % "20220320"
)

libraryDependencies ++= Seq(
  "com.sksamuel.scrimage" % "scrimage-core" % "4.0.31",
  "com.sksamuel.scrimage" %% "scrimage-scala" % "4.0.31"
)

libraryDependencies ++= Seq(
  "org.scodec" %% "scodec-bits" % "1.1.34",
  "org.scorexfoundation" %% "scrypto" % "2.2.1",
  "org.bitcoinj" % "bitcoinj-core" % "0.16.1",
  "org.bouncycastle" % "bcpg-jdk15on" % "1.70"
)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.12.272",
  "com.amazonaws" % "aws-java-sdk" % "1.12.272"
)

libraryDependencies += "com.google.protobuf" % "protobuf-java" % "3.21.5"