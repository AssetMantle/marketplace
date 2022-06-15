package constants

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.i18n.Lang

import scala.concurrent.duration.{Duration, MILLISECONDS}

object CommonConfig {
  private val config: Configuration = Configuration(ConfigFactory.load())
  val logLang: Lang = Lang(config.get[String]("play.log.lang"))
  val webAppUrl: String = config.get[String]("webApp.url")
  val webAppCacheDuration: Duration = Duration(config.get[Int]("webApp.cacheDuration"), MILLISECONDS)
}