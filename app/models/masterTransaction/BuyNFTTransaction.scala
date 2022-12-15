package models.masterTransaction

import models.Trait._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class BuyNFTTransaction(buyerAccountId: String, sellerAccountId: String, txHash: String, nftId: String, saleId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity3[String, String, String] {
  def id1: String = buyerAccountId

  def id2: String = sellerAccountId

  def id3: String = txHash
}

object BuyNFTTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_BUY_NFT_TRANSACTION

  class BuyNFTTransactionTable(tag: Tag) extends Table[BuyNFTTransaction](tag, "BuyNFTTransaction") with ModelTable3[String, String, String] {

    def * = (buyerAccountId, sellerAccountId, txHash, nftId, saleId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (BuyNFTTransaction.tupled, BuyNFTTransaction.unapply)

    def buyerAccountId = column[String]("buyerAccountId", O.PrimaryKey)

    def sellerAccountId = column[String]("sellerAccountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def saleId = column[String]("saleId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = buyerAccountId

    def id2 = sellerAccountId

    def id3 = txHash
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
  extends GenericDaoImpl3[BuyNFTTransactions.BuyNFTTransactionTable, BuyNFTTransaction, String, String, String](
    databaseConfigProvider,
    BuyNFTTransactions.TableQuery,
    executionContext,
    BuyNFTTransactions.module,
    BuyNFTTransactions.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftId: String, saleId: String): Future[Unit] = create(BuyNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = nftId, saleId = saleId, status = None))

    def tryGetBySaleIdAndBuyerAccountId(buyerAccountId: String, saleId: String): Future[Seq[BuyNFTTransaction]] = filter(x => x.saleId === saleId && x.buyerAccountId === buyerAccountId)

    def checkAlreadySold(nftId: String, saleId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filterAndExists(x => x.nftId === nftId && x.saleId === saleId && (x.status || x.status.? === nullStatus))
    }

    def countBuyerNFTsFromSale(buyerAccountId: String, saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.saleId === saleId && (x.status.? === nullStatus || x.status))
    }

    def tryGetByTxHash(txHash: String): Future[BuyNFTTransaction] = filterHead(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(BuyNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(BuyNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))
  }

}