package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.TakeOrder
import models.common.Coin
import models.master.{NFT, NFTOwner, SecondaryMarket}
import models.traits._
import models.{analytics, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.AttoNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class TakeOrderTransaction(txHash: String, nftId: String, buyerId: String, quantity: Int, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity3[String, String, String] {

  def id1: String = txHash

  def id2: String = nftId

  def id3: String = buyerId
}

object TakeOrderTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_TAKE_ORDER_TRANSACTION


  class TakeOrderTransactionTable(tag: Tag) extends Table[TakeOrderTransaction](tag, "TakeOrderTransaction") with ModelTable3[String, String, String] {

    def * = (txHash, nftId, buyerId, quantity, secondaryMarketId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (TakeOrderTransaction.tupled, TakeOrderTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def buyerId = column[String]("buyerId", O.PrimaryKey)

    def secondaryMarketId = column[String]("secondaryMarketId")

    def quantity = column[Int]("quantity")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

    def id3 = buyerId

  }

  val TableQuery = new TableQuery(tag => new TakeOrderTransactionTable(tag))

}

@Singleton
class TakeOrderTransactions @Inject()(
                                       protected val databaseConfigProvider: DatabaseConfigProvider,
                                       blockchainTransactionTakeOrders: blockchainTransaction.TakeOrders,
                                       broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                       utilitiesOperations: utilities.Operations,
                                       getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                       getAccount: queries.blockchain.GetAccount,
                                       getAbciInfo: queries.blockchain.GetABCIInfo,
                                       masterCollections: master.Collections,
                                       masterNFTs: master.NFTs,
                                       masterNFTProperties: master.NFTProperties,
                                       masterNFTOwners: master.NFTOwners,
                                       masterSecondaryMarkets: master.SecondaryMarkets,
                                       collectionsAnalysis: analytics.CollectionsAnalysis,
                                       utilitiesNotification: utilities.Notification,
                                     )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl3[TakeOrderTransactions.TakeOrderTransactionTable, TakeOrderTransaction, String, String, String](
    databaseConfigProvider,
    TakeOrderTransactions.TableQuery,
    executionContext,
    TakeOrderTransactions.module,
    TakeOrderTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, nftId: String, buyerId: String, quantity: Int, secondaryMarketId: String): Future[Unit] = create(TakeOrderTransaction(txHash = txHash, nftId = nftId, buyerId = buyerId, quantity = quantity, secondaryMarketId = secondaryMarketId, status = None))

    def checkAlreadySold(nftId: String, secondaryMarketId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId === nftId && x.secondaryMarketId === secondaryMarketId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def getByTxHash(txHash: String): Future[Seq[TakeOrderTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(TakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(TakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[TakeOrderTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx(secondaryMarketIDs: Seq[String]): Future[Seq[String]] = customQuery(TakeOrderTransactions.TableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIDs) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)

    def getBySecondaryMarketIDs(secondaryMarketIDs: Seq[String]): Future[Seq[TakeOrderTransaction]] = filter(_.secondaryMarketId.inSet(secondaryMarketIDs))
  }

  object Utility {
    def transaction(nftID: String, nftOwner: NFTOwner, buyerId: String, quantity: Int, fromAddress: String, secondaryMarket: SecondaryMarket, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount).recover {
        case exception: Exception => models.blockchain.Account(address = fromAddress, accountType = None, accountNumber = 0, sequence = 0, publicKey = None)
      }
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val nft = masterNFTs.Service.tryGet(nftID)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String], nft: NFT, nftOwner: NFTOwner) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val takeOrderMsg = utilities.BlockchainTransaction.getTakeOrderMsg(
          fromAddress = fromAddress,
          fromID = utilities.Identity.getMantlePlaceIdentityID(buyerId),
          takerOwnableSplit = AttoNumber((secondaryMarket.price * quantity).toMicroBigDecimal),
          orderID = secondaryMarket.getOrderID())
        val wrapCoinMsg = utilities.BlockchainTransaction.getWrapTokenMsg(
          fromAddress = fromAddress,
          fromID = utilities.Identity.getMantlePlaceIdentityID(buyerId),
          coins = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = secondaryMarket.price * quantity))
        )
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(wrapCoinMsg, takeOrderMsg),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultTakeOrderGasLimit),
          gasLimit = constants.Blockchain.DefaultTakeOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              takeOrder <- blockchainTransactionTakeOrders.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nftID, buyerId = buyerId, quantity = nftOwner.quantity.toInt, secondaryMarketId = secondaryMarket.id)
            } yield takeOrder
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          takeOrder <- checkAndAdd(unconfirmedTxHashes)
        } yield takeOrder
      }

      def broadcastTxAndUpdate(takerOrder: TakeOrder) = {
        val broadcastTx = broadcastTxSync.Service.get(takerOrder.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionTakeOrders.Service.markFailedWithLog(txHashes = Seq(takerOrder.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionTakeOrders.Service.markFailedWithLog(txHashes = Seq(takerOrder.txHash), log = successResponse.get.result.log)
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
        nft <- nft
        takeOrder <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase), nft, nftOwner)
        _ <- broadcastTxAndUpdate(takeOrder)
      } yield takeOrder
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = TakeOrderTransactions.module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {

        val takeOrderTxs = Service.getAllPendingStatus

        def getTxs(hashes: Seq[String]) = blockchainTransactionTakeOrders.Service.getByHashes(hashes)

        def checkAndUpdate(takeOrderTxs: Seq[TakeOrderTransaction], txs: Seq[TakeOrder]) = utilitiesOperations.traverse(takeOrderTxs) { takeOrderTx =>
          val transaction = txs.find(_.txHash == takeOrderTx.txHash).get
          val onTxComplete = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(takeOrderTx.txHash)
              val nft = masterNFTs.Service.tryGet(takeOrderTx.nftId)
              val oldNFTOwner = masterNFTOwners.Service.onSuccessfulTakeOrder(secondaryMarketId = takeOrderTx.secondaryMarketId, totalSold = takeOrderTx.quantity, buyerId = takeOrderTx.buyerId)
              val markOrderForDeletion = masterSecondaryMarkets.Service.markForDeletion(takeOrderTx.secondaryMarketId)

              def sendNotifications(oldNFTOwner: NFTOwner, nft: NFT) = {
                utilitiesNotification.send(oldNFTOwner.ownerId, constants.Notification.SELLER_TAKE_ORDER_SUCCESSFUL, nft.name)("")
                utilitiesNotification.send(takeOrderTx.buyerId, constants.Notification.BUYER_TAKE_ORDER_SUCCESSFUL, nft.name)(s"'${takeOrderTx.buyerId}', '${constants.View.COLLECTED}'")
              }

              (for {
                _ <- markSuccess
                nft <- nft
                oldNFTOwner <- oldNFTOwner
                _ <- markOrderForDeletion
                _ <- sendNotifications(oldNFTOwner, nft)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markFailed = Service.markFailed(takeOrderTx.txHash)
              val nft = masterNFTs.Service.tryGet(takeOrderTx.nftId)

              def sendNotifications(nft: NFT) = utilitiesNotification.send(takeOrderTx.buyerId, constants.Notification.BUYER_TAKE_ORDER_FAILED, nft.name)("")

              (for {
                _ <- markFailed
                nft <- nft
                _ <- sendNotifications(nft)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            }
          } else Future()

          for {
            _ <- onTxComplete
          } yield ()

        }

        val forComplete = (for {
          takeOrderTxs <- takeOrderTxs
          txs <- getTxs(takeOrderTxs.map(_.txHash).distinct)
          _ <- checkAndUpdate(takeOrderTxs, txs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}