package constants

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.i18n.Lang

import scala.concurrent.duration.{Duration, MILLISECONDS}

object CommonConfig {
  private val config: Configuration = Configuration(ConfigFactory.load())
  val LogLang: Lang = Lang(config.get[String]("play.log.lang"))
  val WebAppUrl: String = config.get[String]("webApp.url")
  val WebAppCacheDuration: Duration = Duration(config.get[Int]("webApp.cacheDuration"), MILLISECONDS)
  val PushNotificationURL: String = config.get[String]("webApp.pushNotification.url")
  val PushNotificationAuthorizationKey: String = config.get[String]("webApp.pushNotification.authorizationKey")
  val AppVersion: String = config.get[String]("app.version")
  val AppSecret: Array[Byte] = config.get[String]("app.secret").getBytes()
  val ExplorerUrl = "https://explorer.assetmantle.one"
  val WaitTillShutdown = 20000 // milliseconds

  val SessionTokenTimeout: Int = config.get[Int]("play.http.session.token.timeout")

  def initialDelay: Int = config.get[Int]("scheduler.initialDelay")

  def fixedDelay: Int = config.get[Int]("scheduler.fixedDelay")

  object Campaign {
    val nftMintAirDropEnabled: Boolean = config.get[Boolean]("webApp.campaign.enableNFTMintAirDrop")
  }

  object Blockchain {
    case class IBCDenom(hash: String, name: String)

    def name: String = config.get[String]("blockchain.name")

    def ChainId: String = config.get[String]("blockchain.chainId")

    def StakingToken: String = config.get[String]("blockchain.stakingToken")

    def RPCEndPoint: String = config.get[String]("blockchain.rpcURL")

    def RestEndPoint: String = config.get[String]("blockchain.restURL")

    def TransactionMode: String = config.get[String]("blockchain.transactionMode")

    def IBCDenoms: Seq[IBCDenom] = config.get[Seq[Configuration]]("blockchain.ibcDenomList").map { ibcDenoms => IBCDenom(hash = ibcDenoms.get[String]("hash"), name = ibcDenoms.get[String]("name")) }

    def LowGasPrice: Double = config.get[Double]("blockchain.lowGasPrice")

    def MediumGasPrice: Double = config.get[Double]("blockchain.mediumGasPrice")

    def HighGasPrice: Double = config.get[Double]("blockchain.highGasPrice")

    def UpgradeHeight: Int = config.get[Int]("blockchain.upgradeHeight")
  }

  object Pagination {
    val CollectionsPerPage: Int = config.get[Int]("webApp.collectionsPerPage")
    val NFTsPerPage: Int = config.get[Int]("webApp.nftsPerPage")
    val WhitelistPerPage: Int = config.get[Int]("webApp.whitelistPerPage")
    val NotificationsPerPage: Int = config.get[Int]("webApp.notificationsPerPage")
    val OrdersPerPage: Int = config.get[Int]("webApp.ordersPerPage")
  }

  val MaxCollectionDrafts: Int = config.get[Int]("webApp.maxCollectionDrafts")
  val MaxNFTDrafts: Int = config.get[Int]("webApp.maxNFTDrafts")

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
    val s3BucketURL: String = config.get[String]("amazonS3.s3BucketURL")
  }

  object IPFS {
    val JwtToken: String = config.get[String]("ipfs.jwtToken")
    val UploadEndPoint: String = config.get[String]("ipfs.uploadEndPoint")
    val DownloadEndPoint: String = config.get[String]("ipfs.downloadEndPoint")
    val DownloadAccessToken: String = config.get[String]("ipfs.accessToken")
  }

  object Telegram {
    val BotToken: String = "5863635272:AAEiBbSbtdmqjB6qQLbQ9Wgbs_iJIkSKMFE"
    val ChatId: String = "-902538655"
  }
}