package models.blockchainTransaction

import akka.actor.Cancellable
import cosmos.bank.v1beta1.Tx
import exceptions.BaseException
import models.Trait.Logged
import models.Trait.{Entity2, GenericDaoImpl2, ModelTable2}
import models.common.Coin
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, ExecutionContext, Future}
import play.api.libs.json.Json
import transactions.blockchain.BroadcastTxSync
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters.IterableHasAsJava

case class SendCoin(accountId: String, txHash: String, txRawHex: String, fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], log: Option[String], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): SendCoins.SendCoinSerialized = SendCoins.SendCoinSerialized(accountId = this.accountId, txHash = this.txHash, txRawHex = this.txRawHex, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, log = this.log, createdBy = this.createdBy, createdOn = this.createdOn, createdOnTimeZone = this.createdOnTimeZone, updatedBy = this.updatedBy, updatedOn = this.updatedOn, updatedOnTimeZone = this.updatedOnTimeZone)

}

object SendCoins {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_SEND_COIN

  case class SendCoinSerialized(accountId: String, txHash: String, txRawHex: String, fromAddress: String, toAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], log: Option[String], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: SendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawHex = txRawHex, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, log = log, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = accountId

    def id2: String = txHash
  }

  class SendCoinTable(tag: Tag) extends Table[SendCoinSerialized](tag, "SendCoin") with ModelTable2[String, String] {

    def * = (accountId, txHash, txRawHex, fromAddress, toAddress, amount, broadcasted, status.?, log.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (SendCoinSerialized.tupled, SendCoinSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawHex = column[String]("txRawHex")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

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
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SendCoins.SendCoinTable, SendCoins.SendCoinSerialized, String, String](
    databaseConfigProvider,
    SendCoins.TableQuery,
    executionContext,
    SendCoins.module,
    SendCoins.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(accountId: String, txHash: String, txRawHex: String, fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean]): Future[SendCoin] = {
      val sendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawHex = txRawHex, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status, log = None)
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
  }

  object Utility {

    def transaction(accountId: String, fromAddress: String, toAddress: String, amount: Seq[Coin], gasPrice: Double, gasLimit: Int, ecKey: ECKey, memo: String = ""): Future[SendCoin] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, unconfirmedTxHashes: Seq[String]) = {
        val (txHash, txRawHex) = utilities.BlockchainTransaction.getTxHashAndRawHex(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = amount)),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          memo = memo)

        for {
          sendCoin <- if (!unconfirmedTxHashes.contains(txHash)) Service.add(accountId = accountId, txHash = txHash, txRawHex = txRawHex, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = false, status = None) else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        } yield sendCoin
      }

      def broadcastTxAndUpdate(sendCoin: SendCoin) = {

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) Service.updateSendCoin(sendCoin.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) Service.updateSendCoin(sendCoin.copy(broadcasted = true, status = Option(false), log = Option(successResponse.get.result.log)))
        else Service.updateSendCoin(sendCoin.copy(broadcasted = true))

        for {
          (successResponse, errorResponse) <- broadcastTxSync.Service.get(sendCoin.txRawHex)
          updatedSendCoin <- update(successResponse, errorResponse)
        } yield updatedSendCoin
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        sendCoin <- checkMempoolAndAddTx(bcAccount, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedSendCoin <- broadcastTxAndUpdate(sendCoin)
      } yield updatedSendCoin
    }

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val sendCoins = Service.getAllPendingStatus

        def checkAndUpdate(sendCoins: Seq[SendCoin]) = utilitiesOperations.traverse(sendCoins) { sendCoin =>
          val transaction = blockchainTransactions.Service.get(sendCoin.txHash)

          def update(transaction: Option[models.blockchain.Transaction]) = transaction.fold[Future[Option[SendCoin]]](Future(None))(tx => Service.updateSendCoin(sendCoin.copy(status = Option(tx.status), log = if (tx.rawLog != "") Option(tx.rawLog) else None)).map(Option(_)))

          for {
            transaction <- transaction
            _ <- update(transaction)
          } yield ()

        }

        val forComplete = (for {
          sendCoins <- sendCoins
          _ <- checkAndUpdate(sendCoins)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.CommonConfig.Scheduler.InitialDelay, delay = constants.CommonConfig.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}