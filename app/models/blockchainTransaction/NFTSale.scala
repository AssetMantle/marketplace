package models.blockchainTransaction

import akka.actor.Cancellable
import exceptions.BaseException
import models.Trait._
import models.common.Coin
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class NFTSale(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): NFTSales.NFTSaleSerialized = NFTSales.NFTSaleSerialized(txHash = this.txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, memo = this.memo, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object NFTSales {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_NFT_SALE

  case class NFTSaleSerialized(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize: NFTSale = NFTSale(txHash = txHash, txRawBytes = this.txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = txHash
  }

  class NFTSaleTable(tag: Tag) extends Table[NFTSaleSerialized](tag, "NFTSale") with ModelTable[String] {

    def * = (txHash, txRawBytes, fromAddress, toAddress, amount, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTSaleSerialized.tupled, NFTSaleSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash
  }

  val TableQuery = new TableQuery(tag => new NFTSaleTable(tag))

}

@Singleton
class NFTSales @Inject()(
                          protected val databaseConfigProvider: DatabaseConfigProvider,
                          blockchainTransactions: models.blockchain.Transactions,
                        )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTSales.NFTSaleTable, NFTSales.NFTSaleSerialized, String](
    databaseConfigProvider,
    NFTSales.TableQuery,
    executionContext,
    NFTSales.module,
    NFTSales.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String]): Future[NFTSale] = {
      val nftSale = NFTSale(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status, log = None, memo = memo)
      for {
        _ <- create(nftSale.serialize())
      } yield nftSale
    }

    def tryGet(txHash: String): Future[NFTSale] = tryGetById(txHash).map(_.deserialize)

    def updateNFTSale(nftSale: NFTSale): Future[NFTSale] = {
      for {
        _ <- updateById(nftSale.serialize())
      } yield nftSale
    }

    def getAllPendingStatus: Future[Seq[NFTSale]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def markSuccess(txHashes: Seq[String]): Future[Int] = customUpdate(NFTPublicListings.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(true))

    def markFailed(txHashes: Seq[String]): Future[Int] = customUpdate(NFTPublicListings.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(false))

  }

  object Utility {

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val nftSales = Service.getAllPendingStatus

        def getTransactions(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def markSuccess(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markSuccess(hashes) else Future(0)

        def markFailed(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markFailed(hashes) else Future(0)

        val forComplete = (for {
          nftSales <- nftSales
          txs <- getTransactions(nftSales.map(_.txHash))
          _ <- markSuccess(txs.filter(_.status).map(_.hash))
          _ <- markFailed(txs.filter(x => !x.status).map(_.hash))
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    def start: Cancellable = actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.Scheduler.InitialDelay, delay = constants.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}