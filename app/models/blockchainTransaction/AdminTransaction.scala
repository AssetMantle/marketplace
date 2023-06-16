package models.blockchainTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchain._
import models.blockchainTransaction.AdminTransactions.AdminTransactionTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import queries.blockchain._
import queries.responses.blockchain.UnconfirmedTxsResponse
import slick.jdbc.H2Profile.api._
import transactions.blockchain._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class AdminTransaction(txHash: String, fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], txHeight: Option[Int], txType: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction with Entity[String] {

  def id: String = txHash
}

private[blockchainTransaction] object AdminTransactions {

  class AdminTransactionTable(tag: Tag) extends Table[AdminTransaction](tag, "AdminTransaction") with ModelTable[String] {

    def * = (txHash, fromAddress, status.?, memo.?, timeoutHeight, log.?, txHeight.?, txType, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (AdminTransaction.tupled, AdminTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def fromAddress = column[String]("fromAddress")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def timeoutHeight = column[Int]("timeoutHeight")

    def log = column[String]("log")

    def txHeight = column[Int]("txHeight")

    def txType = column[String]("txType")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash
  }

}

@Singleton
class AdminTransactions @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                   getUnconfirmedTxs: GetUnconfirmedTxs,
                                   getAccount: GetAccount,
                                   getAbciInfo: GetABCIInfo,
                                   broadcastTxSync: BroadcastTxSync,
                                   blockchainTransactions: Transactions,
                                   blockchainBlocks: Blocks,
                                   utilitiesOperations: utilities.Operations,
                                 )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[AdminTransactionTable, AdminTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_ADMIN_TRANSACTION

  val tableQuery = new TableQuery(tag => new AdminTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, fromAddress: String, memo: Option[String], timeoutHeight: Int, txType: String): Future[AdminTransaction] = {
      val tx = AdminTransaction(txHash = txHash, fromAddress = fromAddress, status = None, log = None, memo = memo, timeoutHeight = timeoutHeight, txHeight = None, txType = txType)
      for {
        _ <- create(tx)
      } yield tx
    }

    def tryGet(txHash: String): Future[AdminTransaction] = tryGetById(txHash)

    def markSuccess(txHashes: Seq[String], height: Int): Future[Int] = customUpdate(tableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.txHeight)).update((true, height)))

    def markFailed(txHashes: Seq[String], height: Int): Future[Int] = customUpdate(tableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.txHeight)).update((false, height)))

    def markFailedWithLog(txHash: String, log: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(x => (x.status, x.log)).update((false, log)))

    def markFailedWithLog(txHashes: Seq[String], log: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.log)).update((false, log)))

    def getAllPendingStatus: Future[Seq[AdminTransaction]] = filter(_.status.?.isEmpty)

    def checkExists(txHash: String): Future[Boolean] = filterAndExists(_.txHash === txHash)

  }

  object Utility {

    def getLatestHeightAccountAndUnconfirmedTxs(address: String): Future[(Int, Account, UnconfirmedTxsResponse.Response)] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(address).map(_.account.toSerializableAccount).recover {
        case _: Exception => models.blockchain.Account(address = address, accountType = None, accountNumber = 0, sequence = 0, publicKey = None)
      }
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
      } yield (abciInfo.result.response.last_block_height.toInt, bcAccount, unconfirmedTxs)
    }


    def broadcastTxAndUpdate(adminTransaction: AdminTransaction, txRawBytes: Array[Byte]): Future[AdminTransaction] = {

      val broadcastTx = broadcastTxSync.Service.get(adminTransaction.getTxRawAsHexString(txRawBytes))

      def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = {
        val log = if (errorResponse.nonEmpty) Option(errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) Option(successResponse.get.result.log)
        else None

        val updateUserTx = if (log.nonEmpty) Service.markFailedWithLog(adminTransaction.txHash, log.get) else Future()
        for {
          _ <- updateUserTx
        } yield adminTransaction.copy(status = if (log.isDefined) Option(false) else None, log = log)
      }

      for {
        (successResponse, errorResponse) <- broadcastTx
        updatedAdminTransaction <- update(successResponse, errorResponse)
      } yield updatedAdminTransaction
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val adminTransactions = Service.getAllPendingStatus

        def getTransactions(hashes: Seq[String]) = if (hashes.nonEmpty) blockchainTransactions.Utility.getByHashes(hashes) else Future(Seq())

        def markSuccess(txs: Seq[Transaction]) = if (txs.nonEmpty) {
          val txsGroup = txs.groupBy(_.height)
          utilitiesOperations.traverse(txsGroup.keys.toSeq)(txHeight => Service.markSuccess(txHashes = txsGroup.getOrElse(txHeight, Seq()).map(_.hash), height = txHeight)).map(_.sum)
        } else Future(0)

        def markFailed(txs: Seq[Transaction]) = if (txs.nonEmpty) {
          val txsGroup = txs.groupBy(_.height)
          utilitiesOperations.traverse(txsGroup.keys.toSeq)(txHeight => Service.markFailed(txHashes = txsGroup.getOrElse(txHeight, Seq()).map(_.hash), height = txHeight)).map(_.sum)
        } else Future(0)

        def markFailedTimedOut(adminTransactions: Seq[AdminTransaction], allTxs: Seq[Transaction]) = if (adminTransactions.nonEmpty) {
          val notFoundTxHashes = adminTransactions.map(_.txHash).diff(allTxs.map(_.hash))
          val timedoutFailedTxs = adminTransactions.filter(x => notFoundTxHashes.contains(x.txHash) && x.timeoutHeight > 0 && blockchainBlocks.Service.getLatestHeight > x.timeoutHeight).map(_.txHash)
          if (timedoutFailedTxs.nonEmpty) Service.markFailedWithLog(timedoutFailedTxs, constants.Response.TRANSACTION_BROADCASTING_FAILED_AND_TIMED_OUT.message) else Future(0)
        } else Future(0)

        val forComplete = (for {
          adminTransactions <- adminTransactions
          txs <- getTransactions(adminTransactions.map(_.txHash))
          _ <- markSuccess(txs.filter(_.status))
          _ <- markFailed(txs.filter(!_.status))
          _ <- markFailedTimedOut(adminTransactions, txs)
        } yield ()).recover {
          case _: BaseException => logger.error("ADMIN_TRANSACTION_UPDATE_FAILED")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}