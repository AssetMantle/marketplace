package models.blockchainTransaction

import constants.Scheduler
import exceptions.BaseException
import models.traits._
import models.blockchain
import models.blockchain.Transaction
import models.common.Coin
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class SendCoin(accountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): SendCoins.SendCoinSerialized = SendCoins.SendCoinSerialized(accountId = this.accountId, txHash = this.txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, status = this.status, memo = this.memo, timeoutHeight = this.timeoutHeight, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object SendCoins {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_SEND_COIN

  case class SendCoinSerialized(accountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity2[String, String] {
    def deserialize: SendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawBytes = this.txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), status = status, memo = memo, timeoutHeight = timeoutHeight, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = accountId

    def id2: String = txHash
  }

  class SendCoinTable(tag: Tag) extends Table[SendCoinSerialized](tag, "SendCoin") with ModelTable2[String, String] {

    def * = (accountId, txHash, txRawBytes, fromAddress, toAddress, amount, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SendCoinSerialized.tupled, SendCoinSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

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

    def id1 = accountId

    def id2 = txHash
  }

  val TableQuery = new TableQuery(tag => new SendCoinTable(tag))

}

@Singleton
class SendCoins @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider,
                           blockchainAccounts: models.blockchain.Accounts,
                           blockchainTransactions: models.blockchain.Transactions,
                           broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                           utilitiesOperations: utilities.Operations,
                           getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                           getAccount: queries.blockchain.GetAccount,
                           getAbciInfo: queries.blockchain.GetABCIInfo,
                           blockchainBlocks: blockchain.Blocks,
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SendCoins.SendCoinTable, SendCoins.SendCoinSerialized, String, String](
    databaseConfigProvider,
    SendCoins.TableQuery,
    executionContext,
    SendCoins.module,
    SendCoins.logger
  ) {

  object Service {

    def add(accountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], memo: Option[String], timeoutHeight: Int): Future[SendCoin] = {
      val sendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, status = status, log = None, memo = memo, timeoutHeight = timeoutHeight)
      for {
        _ <- create(sendCoin.serialize())
      } yield sendCoin
    }

    def tryGet(accountId: String, txHash: String): Future[SendCoin] = tryGetById1AndId2(id1 = accountId, id2 = txHash).map(_.deserialize)

    def updateSendCoin(sendCoin: SendCoin): Future[SendCoin] = {
      for {
        _ <- updateById1AndId2(sendCoin.serialize())
      } yield sendCoin
    }

    def getAllPendingStatus: Future[Seq[SendCoin]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def markSuccess(txHashes: Seq[String]): Future[Int] = customUpdate(SendCoins.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(true))

    def markFailed(txHashes: Seq[String]): Future[Int] = customUpdate(SendCoins.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(false))

    def markFailedWithLog(txHashes: Seq[String], log: String): Future[Int] = customUpdate(SendCoins.TableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.log)).update((false, log)))
  }

  object Utility {

    def transaction(accountId: String, fromAddress: String, toAddress: String, amount: Seq[Coin], gasPrice: BigDecimal, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = amount)),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight
        )
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        for {
          sendCoin <- if (!unconfirmedTxHashes.contains(txHash)) Service.add(accountId = accountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, status = None, memo = Option(memo), timeoutHeight = timeoutHeight) else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        } yield sendCoin
      }

      def broadcastTxAndUpdate(sendCoin: SendCoin) = {

        val broadcastTx = broadcastTxSync.Service.get(sendCoin.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) Service.updateSendCoin(sendCoin.copy(status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) Service.updateSendCoin(sendCoin.copy(status = Option(false), log = Option(successResponse.get.result.log)))
        else Future(sendCoin)

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedSendCoin <- update(successResponse, errorResponse)
        } yield updatedSendCoin
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        sendCoin <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedSendCoin <- broadcastTxAndUpdate(sendCoin)
      } yield updatedSendCoin
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = SendCoins.module

      def runner(): Unit = {
        val sendCoins = Service.getAllPendingStatus

        def transactions(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def markSuccess(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markSuccess(hashes) else Future(0)

        def markFailed(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markFailed(hashes) else Future(0)

        def markFailedTimedOut(sendCoins: Seq[SendCoin], allTxs: Seq[Transaction]) = if (sendCoins.nonEmpty) {
          val notFoundTxHashes = sendCoins.map(_.txHash).diff(allTxs.map(_.hash))
          val timedoutFailedTxs = sendCoins.filter(x => notFoundTxHashes.contains(x.txHash) && x.timeoutHeight > 0 && blockchainBlocks.Service.getLatestHeight > x.timeoutHeight).map(_.txHash)
          if (timedoutFailedTxs.nonEmpty) Service.markFailedWithLog(timedoutFailedTxs, constants.Response.TRANSACTION_NOT_FOUND.logMessage) else Future(0)
        } else Future(0)

        val forComplete = (for {
          sendCoins <- sendCoins
          transactions <- transactions(sendCoins.map(_.txHash))
          _ <- markSuccess(transactions.filter(_.status).map(_.hash))
          _ <- markFailed(transactions.filter(!_.status).map(_.hash))
          _ <- markFailedTimedOut(sendCoins, transactions)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}