package models.masterTransaction

import models.Trait._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class BuyNFTTransaction(txHash: String, nftId: String, buyerAccountId: String, sellerAccountId: String, saleId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = nftId
}

object BuyNFTTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_BUY_NFT_TRANSACTION

  class BuyNFTTransactionTable(tag: Tag) extends Table[BuyNFTTransaction](tag, "BuyNFTTransaction") with ModelTable2[String, String] {

    def * = (txHash, nftId, buyerAccountId, sellerAccountId, saleId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (BuyNFTTransaction.tupled, BuyNFTTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def buyerAccountId = column[String]("buyerAccountId")

    def sellerAccountId = column[String]("sellerAccountId")

    def saleId = column[String]("saleId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

  }

  val TableQuery = new TableQuery(tag => new BuyNFTTransactionTable(tag))

}

@Singleton
class BuyNFTTransactions @Inject()(
                                    protected val databaseConfigProvider: DatabaseConfigProvider,
                                    blockchainAccounts: models.blockchain.Accounts,
                                    blockchainTransactions: models.blockchain.Transactions,
                                    broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                    utilitiesOperations: utilities.Operations,
                                    getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                    getAccount: queries.blockchain.GetAccount,
                                  )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[BuyNFTTransactions.BuyNFTTransactionTable, BuyNFTTransaction, String, String](
    databaseConfigProvider,
    BuyNFTTransactions.TableQuery,
    executionContext,
    BuyNFTTransactions.module,
    BuyNFTTransactions.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftId: String, saleId: String): Future[Unit] = create(BuyNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = nftId, saleId = saleId, status = None))

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftIds: Seq[String], saleId: String): Future[Unit] = create(nftIds.map(x => BuyNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = x, saleId = saleId, status = None)))

    def tryGetBySaleIdAndBuyerAccountId(buyerAccountId: String, saleId: String): Future[Seq[BuyNFTTransaction]] = filter(x => x.saleId === saleId && x.buyerAccountId === buyerAccountId)

    def checkAlreadySold(nftId: String, saleId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filterAndExists(x => x.nftId === nftId && x.saleId === saleId && (x.status || x.status.? === nullStatus))
    }

    def checkAlreadySold(nftIds: Seq[String], saleId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId.inSet(nftIds) && x.saleId === saleId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def countBuyerNFTsFromSale(buyerAccountId: String, saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.saleId === saleId && (x.status.? === nullStatus || x.status))
    }

    def getByTxHash(txHash: String): Future[Seq[BuyNFTTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(BuyNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(BuyNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))
  }

}