package models.blockchainTransaction

import models.blockchain.Transaction
import models.blockchainTransaction.NFTPublicListings._
import models.common.Coin
import models.masterTransaction.{PublicListingNFTTransaction, PublicListingNFTTransactions}
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFTPublicListing(txHash: String, fromAddress: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction[NFTPublicListing] {

  def serialize(): NFTPublicListings.NFTPublicListingSerialized = NFTPublicListings.NFTPublicListingSerialized(txHash = this.txHash, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, status = this.status, memo = this.memo, timeoutHeight = this.timeoutHeight, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  val txHeight: Option[Int] = None

  def withUpdatedLog(log: Option[String]): NFTPublicListing = this.copy(status = if (log.isDefined) Option(false) else None, log = log)
}

private[blockchainTransaction] object NFTPublicListings {

  case class NFTPublicListingSerialized(txHash: String, fromAddress: String, toAddress: String, amount: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize()(implicit module: String, logger: Logger): NFTPublicListing = NFTPublicListing(txHash = txHash, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), status = status, memo = memo, timeoutHeight = timeoutHeight, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = txHash
  }

  class NFTPublicListingTable(tag: Tag) extends Table[NFTPublicListingSerialized](tag, "NFTPublicListing") with ModelTable[String] {

    def * = (txHash, fromAddress, toAddress, amount, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTPublicListingSerialized.tupled, NFTPublicListingSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def timeoutHeight = column[Int]("timeoutHeight")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash
  }

}

@Singleton
class NFTPublicListings @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                   publicListingNFTTransactions: PublicListingNFTTransactions,
                                   userTransactions: UserTransactions,
                                   blockchainTransactions: models.blockchain.Transactions,
                                   utilitiesOperations: utilities.Operations,
                                 )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTPublicListingTable, NFTPublicListingSerialized, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_NFT_PUBLIC_LISTING

  val tableQuery = new TableQuery(tag => new NFTPublicListingTable(tag))

  object Service {

    def fetchAll: Future[Seq[NFTPublicListing]] = getAll.map(_.map(_.deserialize))

  }

  object Utility {

    def migrate: Future[Unit] = {
      val allNFTPublicListingTxs = Service.fetchAll

      def bcTxs(txHashes: Seq[String]) = blockchainTransactions.Utility.getByHashes(txHashes)

      def publicListingNFTTxs(txHashes: Seq[String]) = publicListingNFTTransactions.Service.getByTxHashes(txHashes)

      def update(allNFTPublicListingTxs: Seq[NFTPublicListing], publicListingNFTTxs: Seq[PublicListingNFTTransaction], txs: Seq[Transaction]) = {
        val userTxs = publicListingNFTTxs.map(_.txHash).distinct.map { publicListingNFTTxHash =>
          val tx = allNFTPublicListingTxs.find(_.txHash == publicListingNFTTxHash).getOrElse(constants.Response.NFT_PUBLIC_LISTING_SALE_NOT_FOUND.throwBaseException())
          val publicListing = publicListingNFTTxs.find(_.txHash == publicListingNFTTxHash).get
          UserTransaction(txHash = tx.txHash, accountId = publicListing.buyerAccountId, fromAddress = tx.fromAddress, status = tx.status, timeoutHeight = tx.timeoutHeight, log = tx.log, txHeight = txs.find(_.hash == publicListing.txHash).map(_.height), txType = constants.Transaction.User.PUBLIC_SALE, createdBy = tx.createdBy, createdOnMillisEpoch = tx.createdOnMillisEpoch, updatedBy = tx.updatedBy, updatedOnMillisEpoch = tx.updatedOnMillisEpoch)
        }
        userTransactions.Service.add(userTxs)
      }


      (for {
        allNFTPublicListingTxs <- allNFTPublicListingTxs
        bcTxs <- bcTxs(allNFTPublicListingTxs.map(_.txHash))
        publicListingNFTTxs <- publicListingNFTTxs(allNFTPublicListingTxs.map(_.txHash))
        _ <- update(allNFTPublicListingTxs, publicListingNFTTxs, bcTxs)
      } yield ()
        ).recover {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
          logger.error("MIGRATION_NFT_PUBLIC_LISTING_FAILED")
      }
    }

  }

}