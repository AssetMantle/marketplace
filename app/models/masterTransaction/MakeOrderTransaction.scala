package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.MakeOrder
import models.master.{NFT, NFTOwner}
import models.traits._
import models.{analytics, blockchain, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.{AssetID, OrderID}
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.{AttoNumber, MicroNumber}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MakeOrderTransaction(txHash: String, nftId: String, sellerId: String, buyerId: Option[String], denom: String, makerOwnableSplit: AttoNumber, expiresIn: Long, takerOwnableSplit: AttoNumber, secondaryMarketId: String, status: Option[Boolean], creationHeight: Option[Int], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getExpiresIn(max: Int, endHours: Int): Option[Int] = this.creationHeight.map(x => x + (max * endHours) / constants.Blockchain.MaxOrderHours)

  def getQuantity: Int = (this.makerOwnableSplit.value / constants.Blockchain.SmallestDec).toInt

  def getPrice: MicroNumber = MicroNumber((this.takerOwnableSplit / (this.getQuantity * MicroNumber.factor)).value)

  def getExchangeRate: AttoNumber = AttoNumber((this.takerOwnableSplit.value / this.getQuantity) / constants.Blockchain.SmallestDec)

  def getOrderID(creationHeight: Int, assetID: AssetID): OrderID = utilities.Order.getOrderID(
    classificationID = constants.Blockchain.MantlePlaceOrderClassificationID,
    properties = constants.Orders.ImmutableMetas,
    makerID = utilities.Identity.getMantlePlaceIdentityID(this.sellerId),
    makerOwnableID = assetID,
    makerOwnableSplit = this.makerOwnableSplit.toBigDecimal,
    creationHeight = creationHeight,
    takerID = if (buyerId.isEmpty) constants.Blockchain.EmptyIdentityID else utilities.Identity.getMantlePlaceIdentityID(buyerId.get),
    takerOwnableID = constants.Blockchain.StakingTokenCoinID,
    takerOwnableSplit = this.takerOwnableSplit.toBigDecimal
  )

  def serialize: MakeOrderTransactions.MakeOrderTransactionSerialized = MakeOrderTransactions.MakeOrderTransactionSerialized(
    txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, buyerId = this.buyerId, denom = this.denom, makerOwnableSplit = this.makerOwnableSplit.toBigDecimal, expiresIn = this.expiresIn, takerOwnableSplit = this.takerOwnableSplit.toBigDecimal, secondaryMarketId = this.secondaryMarketId, status = this.status, creationHeight = this.creationHeight, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )
}

object MakeOrderTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_MAKE_ORDER_TRANSACTION

  case class MakeOrderTransactionSerialized(txHash: String, nftId: String, sellerId: String, buyerId: Option[String], denom: String, makerOwnableSplit: BigDecimal, expiresIn: Long, takerOwnableSplit: BigDecimal, secondaryMarketId: String, status: Option[Boolean], creationHeight: Option[Int], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity3[String, String, String] {
    def id1: String = txHash

    def id2: String = nftId

    def id3: String = sellerId

    def deserialize: MakeOrderTransaction = MakeOrderTransaction(
      txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, buyerId = this.buyerId, denom = this.denom, makerOwnableSplit = AttoNumber(this.makerOwnableSplit), expiresIn = this.expiresIn, takerOwnableSplit = AttoNumber(this.takerOwnableSplit), secondaryMarketId = this.secondaryMarketId, status = this.status, creationHeight = this.creationHeight, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  class MakeOrderTransactionTable(tag: Tag) extends Table[MakeOrderTransactionSerialized](tag, "MakeOrderTransaction") with ModelTable3[String, String, String] {

    def * = (txHash, nftId, sellerId, buyerId.?, denom, makerOwnableSplit, expiresIn, takerOwnableSplit, secondaryMarketId, status.?, creationHeight.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MakeOrderTransactionSerialized.tupled, MakeOrderTransactionSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def sellerId = column[String]("sellerId", O.PrimaryKey)

    def buyerId = column[String]("buyerId")

    def denom = column[String]("denom")

    def makerOwnableSplit = column[BigDecimal]("makerOwnableSplit")

    def expiresIn = column[Long]("expiresIn")

    def takerOwnableSplit = column[BigDecimal]("takerOwnableSplit")

    def secondaryMarketId = column[String]("secondaryMarketId")

    def status = column[Boolean]("status")

    def creationHeight = column[Int]("creationHeight")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

    def id3 = sellerId

  }

  val TableQuery = new TableQuery(tag => new MakeOrderTransactionTable(tag))

}

@Singleton
class MakeOrderTransactions @Inject()(
                                       protected val databaseConfigProvider: DatabaseConfigProvider,
                                       blockchainAccounts: blockchain.Accounts,
                                       blockchainBlocks: blockchain.Blocks,
                                       blockchainTransactions: blockchain.Transactions,
                                       blockchainTransactionMakeOrders: blockchainTransaction.MakeOrders,
                                       blockchainOrders: blockchain.Orders,
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
  extends GenericDaoImpl3[MakeOrderTransactions.MakeOrderTransactionTable, MakeOrderTransactions.MakeOrderTransactionSerialized, String, String, String](
    databaseConfigProvider,
    MakeOrderTransactions.TableQuery,
    executionContext,
    MakeOrderTransactions.module,
    MakeOrderTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, nftId: String, sellerId: String, buyerId: Option[String], denom: String, makerOwnableSplit: AttoNumber, expiresIn: Long, takerOwnableSplit: AttoNumber, secondaryMarketId: String): Future[Unit] = create(MakeOrderTransaction(txHash = txHash, nftId = nftId, sellerId = sellerId, buyerId = buyerId, denom = denom, makerOwnableSplit = makerOwnableSplit, expiresIn = expiresIn, takerOwnableSplit = takerOwnableSplit, secondaryMarketId = secondaryMarketId, status = None, creationHeight = None).serialize)

    def getByTxHash(txHash: String): Future[Seq[MakeOrderTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def tryGetByNFTAndSecondaryMarket(nftId: String, secondaryMarketId: String): Future[MakeOrderTransaction] = filterHead(x => x.nftId === nftId && x.secondaryMarketId === secondaryMarketId).map(_.deserialize)

    def markSuccessAndCreationHeight(txHash: String, creationHeight: Int): Future[Int] = customUpdate(MakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(x => (x.status, x.creationHeight)).update((true, creationHeight)))

    def markFailed(txHash: String): Future[Int] = customUpdate(MakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[MakeOrderTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def checkAnyPendingTx(secondaryMarketIDs: Seq[String]): Future[Seq[String]] = customQuery(MakeOrderTransactions.TableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIDs) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)

    def getBySecondaryMarketIDs(secondaryMarketIDs: Seq[String]): Future[Seq[MakeOrderTransaction]] = filter(_.secondaryMarketId.inSet(secondaryMarketIDs)).map(_.map(_.deserialize))
  }

  object Utility {
    def transaction(nftID: String, nftOwner: NFTOwner, quantity: Int, fromAddress: String, endHours: Int, price: MicroNumber, buyerId: Option[String], secondaryMarketId: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
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
        val expiresIn = ((constants.Blockchain.MaxOrderExpiry * endHours) / constants.Blockchain.MaxOrderHours).toLong
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getMakeOrderMsg(
            fromAddress = fromAddress,
            fromID = utilities.Identity.getMantlePlaceIdentityID(nftOwner.ownerId),
            classificationID = constants.Blockchain.MantlePlaceOrderClassificationID,
            takerID = if (buyerId.isEmpty) constants.Blockchain.EmptyIdentityID else utilities.Identity.getMantlePlaceIdentityID(buyerId.get),
            makerOwnableID = nft.getAssetID,
            makerOwnableSplit = AttoNumber(quantity * constants.Blockchain.SmallestDec),
            expiresIn = expiresIn,
            takerOwnableID = constants.Blockchain.StakingTokenCoinID,
            takerOwnableSplit = AttoNumber((price * quantity).toMicroBigDecimal),
            immutableMetas = constants.Orders.ImmutableMetas,
            mutableMetas = Seq(),
            immutables = Seq(),
            mutables = Seq())),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultMakeOrderGasLimit),
          gasLimit = constants.Blockchain.DefaultMakeOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              makeOrder <- blockchainTransactionMakeOrders.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nftID, sellerId = nftOwner.ownerId, buyerId = buyerId, denom = constants.Blockchain.StakingToken, makerOwnableSplit = AttoNumber(quantity * constants.Blockchain.SmallestDec), expiresIn = expiresIn, takerOwnableSplit = AttoNumber((price * quantity).toMicroBigDecimal), secondaryMarketId = secondaryMarketId)
            } yield makeOrder
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          makeOrder <- checkAndAdd(unconfirmedTxHashes)
        } yield makeOrder
      }

      def broadcastTxAndUpdate(makerOrder: MakeOrder) = {
        val broadcastTx = broadcastTxSync.Service.get(makerOrder.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionMakeOrders.Service.markFailedWithLog(txHashes = Seq(makerOrder.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionMakeOrders.Service.markFailedWithLog(txHashes = Seq(makerOrder.txHash), log = successResponse.get.result.log)
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
        makeOrder <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase), nft, nftOwner)
        _ <- broadcastTxAndUpdate(makeOrder)
      } yield makeOrder
    }

    private def updateForExpiredOrders() = {
      val allOrderIds = masterSecondaryMarkets.Service.getAllOrderIDs

      def filterExistingOrderIds(allOrderIds: Seq[String]) = blockchainOrders.Service.filterExistingIds(allOrderIds)

      def markForDeletion(orderIds: Seq[String]) = masterSecondaryMarkets.Service.markForDeletionByOrderIds(orderIds)

      (for {
        allOrderIds <- allOrderIds
        existingOrderIds <- filterExistingOrderIds(allOrderIds)
        _ <- markForDeletion(allOrderIds.diff(existingOrderIds))
      } yield ()
        ).recover {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
      }
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = MakeOrderTransactions.module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {

        val markDeleteForExpiredOrders = updateForExpiredOrders()
        val makeOrderTxs = Service.getAllPendingStatus

        def getTxs(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def checkAndUpdate(makeOrderTxs: Seq[MakeOrderTransaction], txs: Seq[blockchain.Transaction]) = utilitiesOperations.traverse(makeOrderTxs) { makeOrderTx =>
          val transaction = blockchainTransactionMakeOrders.Service.tryGet(makeOrderTx.txHash)

          def onTxComplete(transaction: MakeOrder) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val creationHeight = txs.find(_.hash == makeOrderTx.txHash).getOrElse(constants.Response.TRANSACTION_NOT_FOUND.throwBaseException()).height
              val markSuccess = Service.markSuccessAndCreationHeight(makeOrderTx.txHash, creationHeight)
              val nft = masterNFTs.Service.tryGet(makeOrderTx.nftId)

              def sendNotifications(nft: NFT) = utilitiesNotification.send(makeOrderTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_SUCCESSFUL, nft.name)(s"'${nft.id}'")

              def updateOrderId(nft: NFT) = {
                val orderID = makeOrderTx.getOrderID(creationHeight = creationHeight, nft.getAssetID)
                masterSecondaryMarkets.Service.updateOrderID(makeOrderTx.secondaryMarketId, orderID)
              }

              def updateAnalytics(collectionId: String) = collectionsAnalysis.Utility.onCreateSecondaryMarket(collectionId, totalListed = makeOrderTx.getQuantity, listingPrice = makeOrderTx.getPrice)

              (for {
                _ <- markSuccess
                nft <- nft
                _ <- updateOrderId(nft)
                _ <- updateAnalytics(nft.collectionId)
                _ <- sendNotifications(nft)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markFailed = Service.markFailed(makeOrderTx.txHash)
              val updateNFTOwner = masterNFTOwners.Service.markSecondaryMarketNull(secondaryMarketId = makeOrderTxs.filter(_.txHash == makeOrderTx.txHash).head.secondaryMarketId)
              val nft = masterNFTs.Service.tryGet(makeOrderTx.nftId)
              val markMasterForDelete = masterSecondaryMarkets.Service.markForDeletion(makeOrderTxs.filter(_.txHash == makeOrderTx.txHash).map(_.secondaryMarketId))

              def sendNotifications(nft: NFT) = utilitiesNotification.send(makeOrderTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_FAILED, nft.name)(s"'${nft.id}'")

              (for {
                _ <- markFailed
                _ <- updateNFTOwner
                _ <- markMasterForDelete
                nft <- nft
                _ <- sendNotifications(nft)
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
          makeOrderTxs <- makeOrderTxs
          txs <- getTxs(makeOrderTxs.map(_.txHash).distinct)
          _ <- checkAndUpdate(makeOrderTxs, txs)
          _ <- markDeleteForExpiredOrders
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}