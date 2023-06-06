package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchain.Split
import models.blockchainTransaction.TakeOrder
import models.common.Coin
import models.master.{NFT, SecondaryMarket}
import models.traits._
import models.{analytics, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.data.base.NumberData
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class TakeOrderTransaction(txHash: String, nftId: String, buyerId: String, quantity: BigInt, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize: TakeOrderTransactions.TakeOrderTransactionSerialize = TakeOrderTransactions.TakeOrderTransactionSerialize(txHash = this.txHash, nftId = this.nftId, buyerId = this.buyerId, quantity = BigDecimal(this.quantity), secondaryMarketId = this.secondaryMarketId, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

}

object TakeOrderTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_TAKE_ORDER_TRANSACTION

  case class TakeOrderTransactionSerialize(txHash: String, nftId: String, buyerId: String, quantity: BigDecimal, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity3[String, String, String] {

    def id1: String = txHash

    def id2: String = nftId

    def id3: String = buyerId

    def deserialize: TakeOrderTransaction = TakeOrderTransaction(txHash = this.txHash, nftId = this.nftId, buyerId = this.buyerId, quantity = this.quantity.toBigInt, secondaryMarketId = this.secondaryMarketId, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
  }


  class TakeOrderTransactionTable(tag: Tag) extends Table[TakeOrderTransactionSerialize](tag, "TakeOrderTransaction") with ModelTable3[String, String, String] {

    def * = (txHash, nftId, buyerId, quantity, secondaryMarketId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (TakeOrderTransactionSerialize.tupled, TakeOrderTransactionSerialize.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def buyerId = column[String]("buyerId", O.PrimaryKey)

    def secondaryMarketId = column[String]("secondaryMarketId")

    def quantity = column[BigDecimal]("quantity")

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
  extends GenericDaoImpl3[TakeOrderTransactions.TakeOrderTransactionTable, TakeOrderTransactions.TakeOrderTransactionSerialize, String, String, String](
    databaseConfigProvider,
    TakeOrderTransactions.TableQuery,
    executionContext,
    TakeOrderTransactions.module,
    TakeOrderTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, nftId: String, buyerId: String, quantity: BigInt, secondaryMarketId: String): Future[Unit] = create(TakeOrderTransaction(txHash = txHash, nftId = nftId, buyerId = buyerId, quantity = quantity, secondaryMarketId = secondaryMarketId, status = None).serialize)

    def getByTxHash(txHash: String): Future[Seq[TakeOrderTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def markSuccess(txHash: String): Future[Int] = customUpdate(TakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(TakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[TakeOrderTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def checkAnyPendingTx(secondaryMarketIDs: Seq[String]): Future[Seq[String]] = customQuery(TakeOrderTransactions.TableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIDs) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)

    def getBySecondaryMarketIDs(secondaryMarketIDs: Seq[String]): Future[Seq[TakeOrderTransaction]] = filter(_.secondaryMarketId.inSet(secondaryMarketIDs)).map(_.map(_.deserialize))
  }

  object Utility {
    def transaction(nftID: String, buyerId: String, quantity: Long, fromAddress: String, secondaryMarket: SecondaryMarket, gasPrice: BigDecimal, ecKey: ECKey, split: Option[Split], royaltyFees: MicroNumber, creatorAddress: String): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount).recover {
        case _: Exception => models.blockchain.Account(address = fromAddress, accountType = None, accountNumber = 0, sequence = 0, publicKey = None)
      }
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val takeOrderMsg = utilities.BlockchainTransaction.getTakeOrderMsg(
          fromAddress = fromAddress,
          fromID = utilities.Identity.getMantlePlaceIdentityID(buyerId),
          takerOwnableSplit = NumberData(secondaryMarket.price.value * quantity),
          orderID = secondaryMarket.getOrderID())
        val wrapCoinMsg = utilities.BlockchainTransaction.getWrapTokenMsg(
          fromAddress = fromAddress,
          fromID = utilities.Identity.getMantlePlaceIdentityID(buyerId),
          coins = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = secondaryMarket.price * quantity - split.fold(MicroNumber.zero)(_.getBalanceAsMicroNumber)))
        )
        val sendCoinMsg = utilities.BlockchainTransaction.getSendCoinMsgAsAny(
          fromAddress = fromAddress,
          toAddress = creatorAddress,
          amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = royaltyFees))
        )
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(wrapCoinMsg, takeOrderMsg, sendCoinMsg),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultTakeOrderGasLimit),
          gasLimit = constants.Blockchain.DefaultTakeOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              takeOrder <- blockchainTransactionTakeOrders.Service.add(txHash = txHash, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nftID, buyerId = buyerId, quantity = quantity, secondaryMarketId = secondaryMarket.id)
            } yield takeOrder
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          takeOrder <- checkAndAdd(unconfirmedTxHashes)
        } yield (takeOrder, txRawBytes)
      }

      def broadcastTxAndUpdate(takerOrder: TakeOrder, txRawBytes: Array[Byte]) = {
        val broadcastTx = broadcastTxSync.Service.get(takerOrder.getTxRawAsHexString(txRawBytes))

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
        (takeOrder, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(takeOrder, txRawBytes)
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
              val secondaryMarket = masterSecondaryMarkets.Service.tryGet(takeOrderTx.secondaryMarketId)

              def updateSecondaryMarket(oldSecondaryMarket: SecondaryMarket) = {
                if (oldSecondaryMarket.quantity == takeOrderTx.quantity) masterSecondaryMarkets.Service.markOnCompletion(takeOrderTx.secondaryMarketId)
                else if (oldSecondaryMarket.quantity > takeOrderTx.quantity) masterSecondaryMarkets.Service.update(oldSecondaryMarket.copy(quantity = oldSecondaryMarket.quantity - takeOrderTx.quantity))
                else constants.Response.TAKE_ORDER_MORE_QUANTITY.throwBaseException()
              }

              def collection(collectionId: String) = masterCollections.Service.tryGet(collectionId)

              def onSuccessfulTakeOrder(collection: master.Collection) = masterNFTOwners.Service.onSuccessfulTakeOrder(nftId = takeOrderTx.nftId, collection = collection, totalSold = takeOrderTx.quantity, buyerId = takeOrderTx.buyerId)

              def sendNotifications(sellerId: String, nft: NFT) = {
                utilitiesNotification.send(sellerId, constants.Notification.SELLER_TAKE_ORDER_SUCCESSFUL, nft.name)("")
                utilitiesNotification.send(takeOrderTx.buyerId, constants.Notification.BUYER_TAKE_ORDER_SUCCESSFUL, nft.name)(s"'${takeOrderTx.buyerId}', '${constants.View.COLLECTED}'")
              }

              (for {
                _ <- markSuccess
                nft <- nft
                secondaryMarket <- secondaryMarket
                collection <- collection(nft.collectionId)
                _ <- onSuccessfulTakeOrder(collection)
                _ <- updateSecondaryMarket(secondaryMarket)
                _ <- sendNotifications(secondaryMarket.sellerId, nft)
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
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
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
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

    def checkAlreadySold(nftId: String, secondaryMarket: SecondaryMarket): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      val takeOrderTxs = filter(x => x.nftId === nftId && x.secondaryMarketId === secondaryMarket.id && (x.status || x.status.? === nullStatus)).map(_.map(_.deserialize))
      for {
        takeOrderTxs <- takeOrderTxs
      } yield (secondaryMarket.quantity - takeOrderTxs.map(_.quantity).sum) == 0
    }
  }

}