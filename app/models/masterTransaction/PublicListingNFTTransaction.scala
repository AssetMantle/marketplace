package models.masterTransaction

import models.Trait._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class PublicListingNFTTransaction(txHash: String, nftId: String, buyerAccountId: String, sellerAccountId: String, publicListingId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = nftId
}

object PublicListingNFTTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_SALE_NFT_TRANSACTION

  class PublicListingNFTTransactionTable(tag: Tag) extends Table[PublicListingNFTTransaction](tag, "PublicListingNFTTransaction") with ModelTable2[String, String] {

    def * = (txHash, nftId, buyerAccountId, sellerAccountId, publicListingId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (PublicListingNFTTransaction.tupled, PublicListingNFTTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def buyerAccountId = column[String]("buyerAccountId")

    def sellerAccountId = column[String]("sellerAccountId")

    def publicListingId = column[String]("publicListingId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

  }

  val TableQuery = new TableQuery(tag => new PublicListingNFTTransactionTable(tag))

}

@Singleton
class PublicListingNFTTransactions @Inject()(
                                              protected val databaseConfigProvider: DatabaseConfigProvider,
                                              blockchainAccounts: models.blockchain.Accounts,
                                              blockchainTransactions: models.blockchain.Transactions,
                                              broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                              utilitiesOperations: utilities.Operations,
                                              getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                              getAccount: queries.blockchain.GetAccount,
                                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[PublicListingNFTTransactions.PublicListingNFTTransactionTable, PublicListingNFTTransaction, String, String](
    databaseConfigProvider,
    PublicListingNFTTransactions.TableQuery,
    executionContext,
    PublicListingNFTTransactions.module,
    PublicListingNFTTransactions.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftIds: Seq[String], publicListingId: String): Future[Unit] = create(nftIds.map(x => PublicListingNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = x, publicListingId = publicListingId, status = None)))

    def tryGetByPublicListingIdAndBuyerAccountId(buyerAccountId: String, publicListingId: String): Future[Seq[PublicListingNFTTransaction]] = filter(x => x.publicListingId === publicListingId && x.buyerAccountId === buyerAccountId)

    def checkAlreadySold(nftId: String, publicListingId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filterAndExists(x => x.nftId === nftId && x.publicListingId === publicListingId && (x.status || x.status.? === nullStatus))
    }

    def checkAlreadySold(nftIds: Seq[String], publicListingId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId.inSet(nftIds) && x.publicListingId === publicListingId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def countBuyerNFTsFromPublicListing(buyerAccountId: String, publicListingId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.publicListingId === publicListingId && (x.status.? === nullStatus || x.status))
    }

    def getTotalPublicListingSold(publicListingId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.publicListingId === publicListingId && x.publicListingId === publicListingId && (x.status.? === nullStatus || x.status))
    }

    def getByTxHash(txHash: String): Future[Seq[PublicListingNFTTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(PublicListingNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(PublicListingNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))
  }

}