package models.masterTransaction

import models.Trait._
import models.{analytics, blockchain, blockchainTransaction, master}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class SecondaryMarketTransaction(txHash: String, nftId: String, buyerAccountId: String, sellerAccountId: String, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash
}

object SecondaryMarketTransferTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_SECONDARY_MARKET_TRANSFER_TRANSACTION

  class SecondaryMarketTransactionTable(tag: Tag) extends Table[SecondaryMarketTransaction](tag, "SecondaryMarketTransaction") with ModelTable[String] {

    def * = (txHash, nftId, buyerAccountId, sellerAccountId, secondaryMarketId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SecondaryMarketTransaction.tupled, SecondaryMarketTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def buyerAccountId = column[String]("buyerAccountId")

    def sellerAccountId = column[String]("sellerAccountId")

    def secondaryMarketId = column[String]("secondaryMarketId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new SecondaryMarketTransactionTable(tag))

}

@Singleton
class SecondaryMarketTransferTransactions @Inject()(
                                                 protected val databaseConfigProvider: DatabaseConfigProvider,
                                                 blockchainAccounts: blockchain.Accounts,
                                                 blockchainTransactionSecondaryMarketTransfers: blockchainTransaction.SecondaryMarketTransfers,
                                                 broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                                 utilitiesOperations: utilities.Operations,
                                                 getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                                 getAccount: queries.blockchain.GetAccount,
                                                 getAbciInfo: queries.blockchain.GetABCIInfo,
                                                 masterCollections: master.Collections,
                                                 masterNFTs: master.NFTs,
                                                 masterNFTOwners: master.NFTOwners,
                                                 masterSecondaryMarkets: master.SecondaryMarkets,
                                                 collectionsAnalysis: analytics.CollectionsAnalysis,
                                                 utilitiesNotification: utilities.Notification,
                                               )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[SecondaryMarketTransferTransactions.SecondaryMarketTransactionTable, SecondaryMarketTransaction, String](
    databaseConfigProvider,
    SecondaryMarketTransferTransactions.TableQuery,
    executionContext,
    SecondaryMarketTransferTransactions.module,
    SecondaryMarketTransferTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftIds: Seq[String], secondaryMarketId: String): Future[Unit] = create(nftIds.map(x => SecondaryMarketTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = x, secondaryMarketId = secondaryMarketId, status = None)))

    def checkAlreadySold(nftIds: Seq[String], secondaryMarketId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId.inSet(nftIds) && x.secondaryMarketId === secondaryMarketId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def countBuyerNFTsFromSecondaryMarket(buyerAccountId: String, secondaryMarketId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.secondaryMarketId === secondaryMarketId && (x.status.? === nullStatus || x.status))
    }

    def getTotalSecondaryMarketSold(secondaryMarketId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.secondaryMarketId === secondaryMarketId && (x.status.? === nullStatus || x.status))
    }

    def getByTxHash(txHash: String): Future[Seq[SecondaryMarketTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(SecondaryMarketTransferTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(SecondaryMarketTransferTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[SecondaryMarketTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx(secondaryMarketIDs: Seq[String]): Future[Seq[String]] = customQuery(SecondaryMarketTransferTransactions.TableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIDs) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)
  }

}