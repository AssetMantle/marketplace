package models.blockchainTransaction

import constants.Scheduler
import exceptions.BaseException
import models.traits._
import models.blockchain
import models.blockchain.Transaction
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class ProvisionAddress(txHash: String, txRawBytes: Array[Byte], fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction with Entity[String] {

  def id: String = txHash
}

object ProvisionAddresses {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_PROVISION_ADDRESS

  class ProvisionAddressTable(tag: Tag) extends Table[ProvisionAddress](tag, "ProvisionAddress") with ModelTable[String] {

    def * = (txHash, txRawBytes, fromAddress, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (ProvisionAddress.tupled, ProvisionAddress.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

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

  val TableQuery = new TableQuery(tag => new ProvisionAddressTable(tag))

}

@Singleton
class ProvisionAddresses @Inject()(
                                 protected val databaseConfigProvider: DatabaseConfigProvider,
                                 blockchainTransactions: models.blockchain.Transactions,
                                 blockchainBlocks: blockchain.Blocks,
                               )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[ProvisionAddresses.ProvisionAddressTable, ProvisionAddress, String](
    databaseConfigProvider,
    ProvisionAddresses.TableQuery,
    executionContext,
    ProvisionAddresses.module,
    ProvisionAddresses.logger
  ) {

  object Service {

    def add(txHash: String, txRawBytes: Array[Byte], fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int): Future[ProvisionAddress] = {
      val provisionAddress = ProvisionAddress(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, status = status, log = None, memo = memo, timeoutHeight = timeoutHeight)
      for {
        _ <- create(provisionAddress)
      } yield provisionAddress
    }

    def tryGet(txHash: String): Future[ProvisionAddress] = tryGetById(txHash)

    def markSuccess(txHashes: Seq[String]): Future[Int] = customUpdate(ProvisionAddresses.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(true))

    def markFailed(txHashes: Seq[String]): Future[Int] = customUpdate(ProvisionAddresses.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(false))

    def markFailedWithLog(txHashes: Seq[String], log: String): Future[Int] = customUpdate(ProvisionAddresses.TableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.log)).update((false, log)))

    def getAllPendingStatus: Future[Seq[ProvisionAddress]] = filter(_.status.?.isEmpty)

    def checkExists(txHash: String): Future[Boolean] = filterAndExists(_.txHash === txHash)

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = ProvisionAddresses.module

      def runner(): Unit = {
        val provisionAddresses = Service.getAllPendingStatus

        def getTransactions(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def markSuccess(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markSuccess(hashes) else Future(0)

        def markFailed(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markFailed(hashes) else Future(0)

        def markFailedTimedOut(provisionAddresses: Seq[ProvisionAddress], allTxs: Seq[Transaction]) = if (provisionAddresses.nonEmpty) {
          val notFoundTxHashes = provisionAddresses.map(_.txHash).diff(allTxs.map(_.hash))
          val timedoutFailedTxs = provisionAddresses.filter(x => notFoundTxHashes.contains(x.txHash) && x.timeoutHeight > 0 && blockchainBlocks.Service.getLatestHeight > x.timeoutHeight).map(_.txHash)
          if (timedoutFailedTxs.nonEmpty) Service.markFailedWithLog(timedoutFailedTxs, constants.Response.TRANSACTION_NOT_FOUND.logMessage) else Future(0)
        } else Future(0)

        val forComplete = (for {
          provisionAddresses <- provisionAddresses
          txs <- getTransactions(provisionAddresses.map(_.txHash))
          _ <- markSuccess(txs.filter(_.status).map(_.hash))
          _ <- markFailed(txs.filter(!_.status).map(_.hash))
          _ <- markFailedTimedOut(provisionAddresses, txs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}