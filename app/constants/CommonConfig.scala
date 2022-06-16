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

  object AmazonS3 {
    val BucketName: String = config.get[String]("amazonS3.bucketName")
    val Region: String = config.get[String]("amazonS3.region")
    val AccessKeyID: String = config.get[String]("amazonS3.accessKeyID")
    val SecretKey: String = config.get[String]("amazonS3.secretKey")
    val MaxMultiPartUploadTime: Int = config.get[Int]("amazonS3.maxMultiPartUploadTime")
  }

  object IPFS {
    val JwtToken: String = config.get[String]("ipfs.jwtToken")
    val EndPoint: String = config.get[String]("ipfs.endPoint")
  }
}