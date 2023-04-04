package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.Unwrap
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

case class UnwrapTransaction(txHash: String, accountId: String, ownableId: String, amount: BigDecimal, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.accountId)

  def getOwnableID: OwnableID = if (this.ownableId == constants.Blockchain.StakingToken) CoinID(StringID(this.ownableId)) else AssetID(HashID(utilities.Secrets.base64URLDecode(this.ownableId)))
}

object UnwrapTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_UNWRAP_TRANSACTION

  class UnwrapTransactionTable(tag: Tag) extends Table[UnwrapTransaction](tag, "UnwrapTransaction") with ModelTable[String] {

    def * = (txHash, accountId, ownableId, amount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (UnwrapTransaction.tupled, UnwrapTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def ownableId = column[String]("ownableId")

    def amount = column[BigDecimal]("amount")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new UnwrapTransactionTable(tag))

}

@Singleton
class UnwrapTransactions @Inject()(
                                    protected val databaseConfigProvider: DatabaseConfigProvider,
                                    blockchainIdentities: blockchain.Identities,
                                    broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                    utilitiesOperations: utilities.Operations,
                                    getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                    getAccount: queries.blockchain.GetAccount,
                                    getAbciInfo: queries.blockchain.GetABCIInfo,
                                    utilitiesNotification: utilities.Notification,
                                    blockchainTransactionUnwraps: blockchainTransaction.Unwraps,
                                  )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[UnwrapTransactions.UnwrapTransactionTable, UnwrapTransaction, String](
    databaseConfigProvider,
    UnwrapTransactions.TableQuery,
    executionContext,
    UnwrapTransactions.module,
    UnwrapTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, ownableID: OwnableID, amount: BigDecimal, accountId: String): Future[String] = create(UnwrapTransaction(txHash = txHash, ownableId = ownableID.asString, amount = amount, accountId = accountId, status = None))

    def getByTxHash(txHash: String): Future[Seq[UnwrapTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(UnwrapTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(UnwrapTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[UnwrapTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)

    def getWithStatusTrueOrPending(ids: Seq[String]): Future[Seq[String]] = filter(x => x.accountId.inSet(ids) && (x.status || x.status.?.isEmpty)).map(_.map(_.accountId))
  }

  object Utility {

    def transaction(fromAddress: String, accountId: String, amount: BigDecimal, ownableId: OwnableID, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getUnwrapTokenMsg(
            fromAddress = fromAddress,
            fromID = utilities.Identity.getMantlePlaceIdentityID(accountId),
            ownableID = ownableId,
            amount = amount,
          )),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultUnwrapGasLimit),
          gasLimit = constants.Blockchain.DefaultUnwrapGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              unwrap <- blockchainTransactionUnwraps.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, ownableID = ownableId, amount = amount, accountId = accountId)
            } yield unwrap
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          unwrap <- checkAndAdd(unconfirmedTxHashes)
        } yield unwrap
      }

      def broadcastTxAndUpdate(unwrap: Unwrap) = {

        val broadcastTx = broadcastTxSync.Service.get(unwrap.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionUnwraps.Service.markFailedWithLog(txHashes = Seq(unwrap.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionUnwraps.Service.markFailedWithLog(txHashes = Seq(unwrap.txHash), log = successResponse.get.result.log)
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
        unwrap <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(unwrap)
      } yield unwrap
    }

    private def checkTransactions() = {
      val unwrapTxs = Service.getAllPendingStatus

      def checkAndUpdate(unwrapTxs: Seq[UnwrapTransaction]) = utilitiesOperations.traverse(unwrapTxs) { unwrapTx =>
        val transaction = blockchainTransactionUnwraps.Service.tryGet(unwrapTx.txHash)

        def onTxComplete(transaction: Unwrap) = if (transaction.status.isDefined) {
          if (transaction.status.get) {
            val markSuccess = Service.markSuccess(unwrapTx.txHash)
            val sendNotification = {
              val param = if (unwrapTx.getOwnableID.isCoinId) {
                s"${unwrapTx.amount / MicroNumber.factor} MNTL"
              } else ""
              utilitiesNotification.send(unwrapTx.accountId, constants.Notification.UNWRAPPED_TOKEN_SUCCESSFULLY, param)()
            }

            (for {
              _ <- markSuccess
              _ <- sendNotification
            } yield ()
              ).recover {
              case _: Exception => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markMasterFailed = Service.markFailed(unwrapTx.txHash)
            val sendNotification = {
              val param = if (unwrapTx.getOwnableID.isCoinId) {
                s"${unwrapTx.amount / MicroNumber.factor} MNTL"
              } else ""
              utilitiesNotification.send(unwrapTx.accountId, constants.Notification.UNWRAPPED_TOKEN_FAILED, param)()
            }

            (for {
              _ <- markMasterFailed
              _ <- sendNotification
            } yield ()
              ).recover {
              case _: Exception => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          }
        } else Future()

        for {
          transaction <- transaction
          _ <- onTxComplete(transaction)
        } yield ()
      }

      for {
        unwrapTxs <- unwrapTxs
        _ <- checkAndUpdate(unwrapTxs)
      } yield ()

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = UnwrapTransactions.module

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