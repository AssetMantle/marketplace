package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.Wrap
import models.common.Coin
import models.traits._
import models.{blockchain, blockchainTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.OwnableID
import schema.id.base._
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class WrapTransaction(txHash: String, accountId: String, ownableId: String, isCoin: Boolean, amount: BigDecimal, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.accountId)

  def getOwnableID: OwnableID = if (this.ownableId == constants.Blockchain.StakingToken) CoinID(StringID(this.ownableId)) else AssetID(HashID(utilities.Secrets.base64URLDecode(this.ownableId)))
}

object WrapTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_WRAP_TRANSACTION

  class WrapTransactionTable(tag: Tag) extends Table[WrapTransaction](tag, "WrapTransaction") with ModelTable[String] {

    def * = (txHash, accountId, ownableId, isCoin, amount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (WrapTransaction.tupled, WrapTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def ownableId = column[String]("ownableId")

    def isCoin = column[Boolean]("isCoin")

    def amount = column[BigDecimal]("amount")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new WrapTransactionTable(tag))

}

@Singleton
class WrapTransactions @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider,
                                  blockchainIdentities: blockchain.Identities,
                                  broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                  utilitiesOperations: utilities.Operations,
                                  getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                  getAccount: queries.blockchain.GetAccount,
                                  getAbciInfo: queries.blockchain.GetABCIInfo,
                                  utilitiesNotification: utilities.Notification,
                                  blockchainTransactionWraps: blockchainTransaction.Wraps,
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[WrapTransactions.WrapTransactionTable, WrapTransaction, String](
    databaseConfigProvider,
    WrapTransactions.TableQuery,
    executionContext,
    WrapTransactions.module,
    WrapTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, ownableID: OwnableID, amount: BigDecimal, accountId: String): Future[String] = create(WrapTransaction(txHash = txHash, ownableId = ownableID.asString, isCoin = ownableID.isCoinId, amount = amount, accountId = accountId, status = None))

    def getByTxHash(txHash: String): Future[Seq[WrapTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(WrapTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(WrapTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[WrapTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)

    def getWithStatusTrueOrPending(ids: Seq[String]): Future[Seq[String]] = filter(x => x.accountId.inSet(ids) && (x.status || x.status.?.isEmpty)).map(_.map(_.accountId))
  }

  object Utility {

    def transaction(fromAddress: String, accountId: String, coin: Coin, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getWrapTokenMsg(
            fromAddress = fromAddress,
            fromID = utilities.Identity.getMantlePlaceIdentityID(accountId),
            coins = Seq(coin),
          )),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultWrapGasLimit),
          gasLimit = constants.Blockchain.DefaultWrapGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              wrap <- blockchainTransactionWraps.Service.add(txHash = txHash, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, ownableID = coin.getDenomOwnableID, amount = coin.amount.toMicroBigDecimal, accountId = accountId)
            } yield wrap
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          wrap <- checkAndAdd(unconfirmedTxHashes)
        } yield (wrap, txRawBytes)
      }

      def broadcastTxAndUpdate(wrap: Wrap, txRawBytes: Array[Byte]) = {

        val broadcastTx = broadcastTxSync.Service.get(wrap.getTxRawAsHexString(txRawBytes))

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionWraps.Service.markFailedWithLog(txHashes = Seq(wrap.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionWraps.Service.markFailedWithLog(txHashes = Seq(wrap.txHash), log = successResponse.get.result.log)
        else Future(0)

        for {
          (successResponse, errorResponse) <- broadcastTx
          _ <- update(successResponse, errorResponse)
        } yield ()
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        (wrap, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(wrap, txRawBytes)
      } yield wrap
    }

    private def checkTransactions() = {
      val wrapTxs = Service.getAllPendingStatus

      def checkAndUpdate(wrapTxs: Seq[WrapTransaction]) = utilitiesOperations.traverse(wrapTxs) { wrapTx =>
        val transaction = blockchainTransactionWraps.Service.tryGet(wrapTx.txHash)

        def onTxComplete(transaction: Wrap) = if (transaction.status.isDefined) {
          if (transaction.status.get) {
            val markSuccess = Service.markSuccess(wrapTx.txHash)
            val sendNotification = {
              val param = if (wrapTx.isCoin) {
                s"${wrapTx.amount / MicroNumber.factor} MNTL"
              } else ""
              utilitiesNotification.send(wrapTx.accountId, constants.Notification.WRAPPED_TOKEN_SUCCESSFULLY, param)()
            }

            (for {
              _ <- markSuccess
              _ <- sendNotification
            } yield ()
              ).recover {
              case exception: Exception => logger.error(exception.getLocalizedMessage)
                logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markMasterFailed = Service.markFailed(wrapTx.txHash)
            val sendNotification = {
              val param = if (wrapTx.getOwnableID.isCoinId) {
                s"${wrapTx.amount / MicroNumber.factor} MNTL"
              } else ""
              utilitiesNotification.send(wrapTx.accountId, constants.Notification.WRAPPED_TOKEN_FAILED, param)()
            }

            (for {
              _ <- markMasterFailed
              _ <- sendNotification
            } yield ()
              ).recover {
              case exception: Exception => logger.error(exception.getLocalizedMessage)
                logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          }
        } else Future()

        for {
          transaction <- transaction
          _ <- onTxComplete(transaction)
        } yield ()
      }

      for {
        wrapTxs <- wrapTxs
        _ <- checkAndUpdate(wrapTxs)
      } yield ()

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = WrapTransactions.module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.FiveMinutes

      def runner(): Unit = {

        val forComplete = (for {
          _ <- checkTransactions()
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}