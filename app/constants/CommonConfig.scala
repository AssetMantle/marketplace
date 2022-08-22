package constants

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.i18n.Lang

import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration, MILLISECONDS}

object CommonConfig {
  private val config: Configuration = Configuration(ConfigFactory.load())
  val logLang: Lang = Lang(config.get[String]("play.log.lang"))
  val webAppUrl: String = config.get[String]("webApp.url")
  val WebAppCacheDuration: Duration = Duration(config.get[Int]("webApp.cacheDuration"), MILLISECONDS)
  val DefaultPublicFolder: String = config.get[String]("webApp.defaultPublicFolder")

  val sessionTokenTimeout: Int = config.get[Int]("play.http.session.token.timeout")

  object Scheduler {
    val InitialDelay: FiniteDuration = config.get[Int]("scheduler.initialDelay").millis
    val FixedDelay: FiniteDuration = config.get[Int]("scheduler.fixedDelay").millis
  }

  object Blockchain {
    case class IBCDenom(hash: String, name: String)

    val ChainId: String = config.get[String]("blockchain.chainId")
    val StakingToken: String = config.get[String]("blockchain.stakingToken")
    val RPCEndPoint: String = config.get[String]("blockchain.rpcURL")
    val RestEndPoint: String = config.get[String]("blockchain.restURL")
    val TransactionMode: String = config.get[String]("blockchain.transactionMode")
    val IBCDenoms: Seq[IBCDenom] = config.get[Seq[Configuration]]("blockchain.ibcDenomList").map { ibcDenoms => IBCDenom(hash = ibcDenoms.get[String]("hash"), name = ibcDenoms.get[String]("name")) }
    val LowGasPrice: Int = config.get[Int]("blockchain.lowGasPrice")
    val MediumGasPrice: Int = config.get[Int]("blockchain.mediumGasPrice")
    val HighGasPrice: Int = config.get[Int]("blockchain.highGasPrice")
  }

  object Collections {
    val CollectionsPerPage: Int = config.get[Int]("webApp.collectionsPerPage")
    val NFTsPerPage: Int = config.get[Int]("webApp.nftsPerPage")
  }

  object Files {
    val RootFilePath: String = config.get[String]("upload.rootFilePath")
    val CollectionPath: String = config.get[String]("upload.collectionPath")
  }

  object AmazonS3 {
    val BucketName: String = config.get[String]("amazonS3.bucketName")
    val Region: String = config.get[String]("amazonS3.region")
    val AccessKeyID: String = config.get[String]("amazonS3.accessKeyID")
    val SecretKey: String = config.get[String]("amazonS3.secretKey")
    val MaxMultiPartUploadTime: Int = config.get[Int]("amazonS3.maxMultiPartUploadTime")
  }

  object IPFS {
    val JwtToken: String = config.get[String]("ipfs.jwtToken")
    val UploadEndPoint: String = config.get[String]("ipfs.uploadEndPoint")
    val DownloadEndPoint: String = config.get[String]("ipfs.downloadEndPoint")
  }
}