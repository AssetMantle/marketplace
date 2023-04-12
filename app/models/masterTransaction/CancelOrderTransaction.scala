package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.CancelOrder
import models.master.SecondaryMarket
import models.traits._
import models.{blockchain, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CancelOrderTransaction(txHash: String, secondaryMarketId: String, sellerId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

object CancelOrderTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_CANCEL_ORDER_TRANSACTION

  class CancelOrderTransactionTable(tag: Tag) extends Table[CancelOrderTransaction](tag, "CancelOrderTransaction") with ModelTable[String] {

    def * = (txHash, secondaryMarketId, sellerId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CancelOrderTransaction.tupled, CancelOrderTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def secondaryMarketId = column[String]("secondaryMarketId", O.PrimaryKey)

    def sellerId = column[String]("sellerId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new CancelOrderTransactionTable(tag))

}

@Singleton
class CancelOrderTransactions @Inject()(
                                         protected val databaseConfigProvider: DatabaseConfigProvider,
                                         blockchainIdentities: blockchain.Identities,
                                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                         masterSecondaryMarkets: master.SecondaryMarkets,
                                         masterNFTs: master.NFTs,
                                         utilitiesOperations: utilities.Operations,
                                         getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                         getAccount: queries.blockchain.GetAccount,
                                         getAbciInfo: queries.blockchain.GetABCIInfo,
                                         utilitiesNotification: utilities.Notification,
                                         blockchainTransactionCancelOrders: blockchainTransaction.CancelOrders,
                                       )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[CancelOrderTransactions.CancelOrderTransactionTable, CancelOrderTransaction, String](
    databaseConfigProvider,
    CancelOrderTransactions.TableQuery,
    executionContext,
    CancelOrderTransactions.module,
    CancelOrderTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, secondaryMarketId: String, sellerId: String): Future[String] = create(CancelOrderTransaction(txHash = txHash, secondaryMarketId = secondaryMarketId, sellerId = sellerId, status = None))

    def getByTxHash(txHash: String): Future[Seq[CancelOrderTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(CancelOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(CancelOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[CancelOrderTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)

    def getWithStatusTrueOrPending(secondaryMarketIds: Seq[String]): Future[Seq[String]] = filter(x => x.secondaryMarketId.inSet(secondaryMarketIds) && (x.status || x.status.?.isEmpty)).map(_.map(_.secondaryMarketId))
  }

  object Utility {

    def transaction(secondaryMarket: SecondaryMarket, fromAddress: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(
            utilities.BlockchainTransaction.getCancelOrderMsg(fromAddress = fromAddress, fromID = utilities.Identity.getMantlePlaceIdentityID(secondaryMarket.sellerId), orderID = secondaryMarket.getOrderID())
          ),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultCancelOrderGasLimit),
          gasLimit = constants.Blockchain.DefaultCancelOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              cancelOrder <- blockchainTransactionCancelOrders.Service.add(txHash = txHash, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, secondaryMarketId = secondaryMarket.id, sellerId = secondaryMarket.sellerId)
            } yield cancelOrder
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          cancelOrder <- checkAndAdd(unconfirmedTxHashes)
        } yield (cancelOrder, txRawBytes)
      }

      def broadcastTxAndUpdate(cancelOrder: CancelOrder, txRawBytes: Array[Byte]) = {

        val broadcastTx = broadcastTxSync.Service.get(cancelOrder.getTxRawAsHexString(txRawBytes))

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionCancelOrders.Service.markFailedWithLog(txHashes = Seq(cancelOrder.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionCancelOrders.Service.markFailedWithLog(txHashes = Seq(cancelOrder.txHash), log = successResponse.get.result.log)
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
        (cancelOrder, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(cancelOrder, txRawBytes)
      } yield cancelOrder
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = CancelOrderTransactions.module

      def runner(): Unit = {
        val cancelOrderTxs = Service.getAllPendingStatus

        def checkAndUpdate(cancelOrderTxs: Seq[CancelOrderTransaction]) = utilitiesOperations.traverse(cancelOrderTxs) { cancelOrderTx =>
          val transaction = blockchainTransactionCancelOrders.Service.tryGet(cancelOrderTx.txHash)

          def onTxComplete(transaction: CancelOrder) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(cancelOrderTx.txHash)
              val markForDeletion = masterSecondaryMarkets.Service.markForDeletion(cancelOrderTx.secondaryMarketId)
              val sendNotifications = utilitiesNotification.send(cancelOrderTx.sellerId, constants.Notification.CANCEL_ORDER_SUCCESSFUL)("")

              (for {
                _ <- markSuccess
                _ <- markForDeletion
                _ <- sendNotifications
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(cancelOrderTx.txHash)
              val sendNotifications = utilitiesNotification.send(cancelOrderTx.sellerId, constants.Notification.CANCEL_ORDER_FAILED)("")

              (for {
                _ <- markMasterFailed
                _ <- sendNotifications
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()

        }

        val forComplete = (for {
          cancelOrderTxs <- cancelOrderTxs
          _ <- checkAndUpdate(cancelOrderTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}