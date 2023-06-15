package models.blockchainTransaction

import models.blockchain.Transaction
import models.blockchainTransaction.NFTSales._
import models.common.Coin
import models.masterTransaction.{SaleNFTTransaction, SaleNFTTransactions}
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFTSale(txHash: String, fromAddress: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): NFTSales.NFTSaleSerialized = NFTSales.NFTSaleSerialized(txHash = this.txHash, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, status = this.status, memo = this.memo, timeoutHeight = this.timeoutHeight, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

private[blockchainTransaction] object NFTSales {

  case class NFTSaleSerialized(txHash: String, fromAddress: String, toAddress: String, amount: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize()(implicit module: String, logger: Logger): NFTSale = NFTSale(txHash = txHash, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), status = status, memo = memo, timeoutHeight = timeoutHeight, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = txHash
  }

  class NFTSaleTable(tag: Tag) extends Table[NFTSaleSerialized](tag, "NFTSale") with ModelTable[String] {

    def * = (txHash, fromAddress, toAddress, amount, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTSaleSerialized.tupled, NFTSaleSerialized.unapply)

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
class NFTSales @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider,
                          userTransactions: UserTransactions,
                          saleNFTTransactions: SaleNFTTransactions,
                          blockchainTransactions: models.blockchain.Transactions,
                          utilitiesOperations: utilities.Operations,
                        )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTSaleTable, NFTSaleSerialized, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_NFT_SALE

  val tableQuery = new TableQuery(tag => new NFTSaleTable(tag))

  object Service {

    def fetchAll: Future[Seq[NFTSale]] = getAll.map(_.map(_.deserialize))

  }

  object Utility {

    def migrate: Future[Unit] = {
      val allNFTSaleTxs = Service.fetchAll

      def nftSales(txHashes: Seq[String]) = saleNFTTransactions.Service.getByTxHashes(txHashes)

      def bcTxs(txHashes: Seq[String]) = blockchainTransactions.Utility.getByHashes(txHashes)

      def update(allNFTSaleTxs: Seq[NFTSale], nftSales: Seq[SaleNFTTransaction], txs: Seq[Transaction]) = {
        val userTxs = nftSales.map(_.txHash).distinct.map { nftSaleTxHash =>
          val tx = allNFTSaleTxs.find(_.txHash == nftSaleTxHash).getOrElse(constants.Response.NFT_WHITELIST_SALE_NOT_FOUND.throwBaseException())
          val nftSale = nftSales.find(_.txHash == nftSaleTxHash).get
          UserTransaction(txHash = tx.txHash, accountId = nftSale.buyerAccountId, fromAddress = tx.fromAddress, status = tx.status, memo = tx.memo, timeoutHeight = tx.timeoutHeight, log = tx.log, txHeight = txs.find(_.hash == nftSale.txHash).map(_.height), txType = constants.Transaction.User.WHITELIST_SALE, createdBy = tx.createdBy, createdOnMillisEpoch = tx.createdOnMillisEpoch, updatedBy = tx.updatedBy, updatedOnMillisEpoch = tx.updatedOnMillisEpoch)
        }
        userTransactions.Service.add(userTxs)
      }

      (for {
        allNFTSaleTxs <- allNFTSaleTxs
        bcTxs <- bcTxs(allNFTSaleTxs.map(_.txHash))
        nftSales <- nftSales(allNFTSaleTxs.map(_.txHash))
        _ <- update(allNFTSaleTxs, nftSales, bcTxs)
      } yield ()
        ).recover {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
          logger.error("MIGRATION_NFT_SALE_FAILED")
      }
    }
  }

}