package constants

import com.typesafe.config.ConfigFactory
import play.api.Configuration

object CommonConfig {
  val config: Configuration = Configuration(ConfigFactory.load())
  val baseUrl: String = config.get[String]("baseURL.webUrl")
}