name := "MantlePlace"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)
scalaVersion := "2.13.8"

version := "1.0"

lazy val GatlingTest = config("gatling") extend Test

GatlingTest / scalaSource  := baseDirectory.value / "gatling/simulation"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)

libraryDependencies ++= Seq(ws, specs2 % Test, guice, caffeine)

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.7.6" % "test"

libraryDependencies += "io.gatling" % "gatling-test-framework" % "3.7.6" % "test"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.postgresql" % "postgresql" % "42.3.4"
)