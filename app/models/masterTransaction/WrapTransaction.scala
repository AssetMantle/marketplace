package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchain
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.common.Coin
import models.masterTransaction.WrapTransactions.WrapTransactionTable
import models.traits._
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.OwnableID
import schema.id.base._
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class WrapTransaction(txHash: String, accountId: String, ownableId: String, isCoin: Boolean, amount: BigDecimal, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.accountId)

  def getOwnableID: OwnableID = if (this.ownableId == constants.Blockchain.StakingToken) CoinID(StringID(this.ownableId)) else AssetID(HashID(utilities.Secrets.base64URLDecode(this.ownableId)))
}

private[masterTransaction] object WrapTransactions {
  class WrapTransactionTable(tag: Tag) extends Table[WrapTransaction](tag, "WrapTransaction") with ModelTable[String] {

    def * = (txHash, accountId, ownableId, isCoin, amount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (WrapTransaction.tupled, WrapTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId")

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

}

@Singleton
class WrapTransactions @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                  blockchainIdentities: blockchain.Identities,
                                  utilitiesOperations: utilities.Operations,
                                  utilitiesNotification: utilities.Notification,
                                  userTransactions: UserTransactions,
                                )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[WrapTransactions.WrapTransactionTable, WrapTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_WRAP_TRANSACTION

  val tableQuery = new TableQuery(tag => new WrapTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, ownableID: OwnableID, amount: BigDecimal, accountId: String): Future[String] = create(WrapTransaction(txHash = txHash, ownableId = ownableID.asString, isCoin = ownableID.isCoinId, amount = amount, accountId = accountId, status = None)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[WrapTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[WrapTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    def transaction(fromAddress: String, accountId: String, coin: Coin, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getWrapTokenMsg(
            fromAddress = fromAddress,
            fromID = utilities.Identity.getMantlePlaceIdentityID(accountId),
            coins = Seq(coin),
          )),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Transaction.DefaultWrapGasLimit),
          gasLimit = constants.Transaction.DefaultWrapGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              userTransaction <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = accountId, fromAddress = fromAddress, memo = Option(memo), timeoutHeight = timeoutHeight, txType = constants.Transaction.User.WRAP)
              _ <- Service.addWithNoneStatus(txHash = txHash, ownableID = coin.getDenomOwnableID, amount = coin.amount.toMicroBigDecimal, accountId = accountId)
            } yield userTransaction
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          userTransaction <- checkAndAdd
        } yield (userTransaction, txRawBytes)
      }

      def broadcastTxAndUpdate(userTransaction: UserTransaction, txRawBytes: Array[Byte]) = userTransactions.Utility.broadcastTxAndUpdate(userTransaction, txRawBytes)

      for {
        (latestHeight, bcAccount, unconfirmedTxs) <- latestHeightAccountUnconfirmedTxs
        (userTransaction, txRawBytes) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedUserTransaction <- broadcastTxAndUpdate(userTransaction, txRawBytes)
      } yield updatedUserTransaction
    }

    private def checkTransactions() = {
      val wrapTxs = Service.getAllPendingStatus

      def checkAndUpdate(wrapTxs: Seq[WrapTransaction]) = utilitiesOperations.traverse(wrapTxs) { wrapTx =>
        val userTransaction = userTransactions.Service.tryGet(wrapTx.txHash)

        def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
          if (userTransaction.status.get) {
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
          userTransaction <- userTransaction
          _ <- onTxComplete(userTransaction)
        } yield ()
      }

      for {
        wrapTxs <- wrapTxs
        _ <- checkAndUpdate(wrapTxs)
      } yield ()

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

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