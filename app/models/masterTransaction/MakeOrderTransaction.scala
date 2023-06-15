package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.master.{NFT, NFTOwner}
import models.masterTransaction.MakeOrderTransactions.MakeOrderTransactionTable
import models.traits._
import models.{analytics, blockchain, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.data.base.NumberData
import schema.id.base.{AssetID, OrderID}
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MakeOrderTransaction(txHash: String, nftId: String, sellerId: String, buyerId: Option[String], denom: String, makerOwnableSplit: BigInt, expiresIn: Long, takerOwnableSplit: BigInt, secondaryMarketId: String, status: Option[Boolean], creationHeight: Option[Int], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getExpiresIn(max: Int, endHours: Int): Option[Int] = this.creationHeight.map(x => x + (max * endHours) / constants.Blockchain.MaxOrderHours)

  def getQuantity: BigInt = this.makerOwnableSplit

  def getPrice: MicroNumber = MicroNumber(this.takerOwnableSplit / this.getQuantity)

  def getExchangeRate: BigDecimal = this.getPrice.toBigDecimal

  def getOrderID(creationHeight: Int, assetID: AssetID): OrderID = utilities.Order.getOrderID(
    classificationID = constants.Transaction.OrderClassificationID,
    properties = constants.Orders.ImmutableMetas,
    makerID = utilities.Identity.getMantlePlaceIdentityID(this.sellerId),
    makerOwnableID = assetID,
    makerOwnableSplit = this.makerOwnableSplit,
    creationHeight = creationHeight,
    takerID = if (buyerId.isEmpty) constants.Blockchain.EmptyIdentityID else utilities.Identity.getMantlePlaceIdentityID(buyerId.get),
    takerOwnableID = constants.Blockchain.StakingTokenCoinID,
    takerOwnableSplit = this.takerOwnableSplit
  )

  def serialize: MakeOrderTransactions.MakeOrderTransactionSerialized = MakeOrderTransactions.MakeOrderTransactionSerialized(
    txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, buyerId = this.buyerId, denom = this.denom, makerOwnableSplit = BigDecimal(this.makerOwnableSplit.toString()), expiresIn = this.expiresIn, takerOwnableSplit = BigDecimal(this.takerOwnableSplit.toString()), secondaryMarketId = this.secondaryMarketId, status = this.status, creationHeight = this.creationHeight, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )

  def getExpiryFromNow(latestBlock: Int): Int = ((latestBlock - this.creationHeight.getOrElse(latestBlock)) * constants.Blockchain.MaxOrderHours) / constants.Blockchain.MaxOrderExpiry
}

private[masterTransaction] object MakeOrderTransactions {

  case class MakeOrderTransactionSerialized(txHash: String, nftId: String, sellerId: String, buyerId: Option[String], denom: String, makerOwnableSplit: BigDecimal, expiresIn: Long, takerOwnableSplit: BigDecimal, secondaryMarketId: String, status: Option[Boolean], creationHeight: Option[Int], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def id: String = txHash

    def deserialize()(implicit module: String, logger: Logger): MakeOrderTransaction = MakeOrderTransaction(
      txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, buyerId = this.buyerId, denom = this.denom, makerOwnableSplit = this.makerOwnableSplit.toBigInt, expiresIn = this.expiresIn, takerOwnableSplit = this.takerOwnableSplit.toBigInt, secondaryMarketId = this.secondaryMarketId, status = this.status, creationHeight = this.creationHeight, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  class MakeOrderTransactionTable(tag: Tag) extends Table[MakeOrderTransactionSerialized](tag, "MakeOrderTransaction") with ModelTable[String] {

    def * = (txHash, nftId, sellerId, buyerId.?, denom, makerOwnableSplit, expiresIn, takerOwnableSplit, secondaryMarketId, status.?, creationHeight.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MakeOrderTransactionSerialized.tupled, MakeOrderTransactionSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def sellerId = column[String]("sellerId")

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

    def id = txHash

  }

}

@Singleton
class MakeOrderTransactions @Inject()(
                                       protected val dbConfigProvider: DatabaseConfigProvider,
                                       blockchainOrders: blockchain.Orders,
                                       utilitiesOperations: utilities.Operations,
                                       masterCollections: master.Collections,
                                       masterNFTs: master.NFTs,
                                       masterNFTProperties: master.NFTProperties,
                                       masterNFTOwners: master.NFTOwners,
                                       masterSecondaryMarkets: master.SecondaryMarkets,
                                       collectionsAnalysis: analytics.CollectionsAnalysis,
                                       utilitiesNotification: utilities.Notification,
                                       userTransactions: UserTransactions,
                                     )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[MakeOrderTransactions.MakeOrderTransactionTable, MakeOrderTransactions.MakeOrderTransactionSerialized, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_MAKE_ORDER_TRANSACTION

  val tableQuery = new TableQuery(tag => new MakeOrderTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, nftId: String, sellerId: String, buyerId: Option[String], denom: String, makerOwnableSplit: BigInt, expiresIn: Long, takerOwnableSplit: BigInt, secondaryMarketId: String): Future[String] = create(MakeOrderTransaction(txHash = txHash, nftId = nftId, sellerId = sellerId, buyerId = buyerId, denom = denom, makerOwnableSplit = makerOwnableSplit, expiresIn = expiresIn, takerOwnableSplit = takerOwnableSplit, secondaryMarketId = secondaryMarketId, status = None, creationHeight = None).serialize).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[MakeOrderTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def tryGetByNFTAndSecondaryMarket(nftId: String, secondaryMarketId: String): Future[MakeOrderTransaction] = filterHead(x => x.nftId === nftId && x.secondaryMarketId === secondaryMarketId).map(_.deserialize)

    def markSuccessAndCreationHeight(txHash: String, creationHeight: Int): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(x => (x.status, x.creationHeight)).update((true, creationHeight)))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[MakeOrderTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def checkAnyPendingTx(secondaryMarketIDs: Seq[String]): Future[Seq[String]] = customQuery(tableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIDs) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)
  }

  object Utility {
    def transaction(nftID: String, nftOwner: NFTOwner, quantity: Long, fromAddress: String, endHours: Int, price: MicroNumber, buyerId: Option[String], secondaryMarketId: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)
      val nft = masterNFTs.Service.tryGet(nftID)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String], nft: NFT, nftOwner: NFTOwner) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val expiresIn = ((constants.Blockchain.MaxOrderExpiry * endHours) / constants.Blockchain.MaxOrderHours).toLong
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getMakeOrderMsg(
            fromAddress = fromAddress,
            fromID = utilities.Identity.getMantlePlaceIdentityID(nftOwner.ownerId),
            classificationID = constants.Transaction.OrderClassificationID,
            takerID = if (buyerId.isEmpty) constants.Blockchain.EmptyIdentityID else utilities.Identity.getMantlePlaceIdentityID(buyerId.get),
            makerOwnableID = nft.getAssetID,
            makerOwnableSplit = NumberData(quantity),
            expiresIn = expiresIn,
            takerOwnableID = constants.Blockchain.StakingTokenCoinID,
            takerOwnableSplit = NumberData(price.value * quantity),
            immutableMetas = constants.Orders.ImmutableMetas,
            mutableMetas = Seq(),
            immutables = Seq(),
            mutables = Seq())),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Transaction.DefaultMakeOrderGasLimit),
          gasLimit = constants.Transaction.DefaultMakeOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              userTransaction <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = nftOwner.ownerId, fromAddress = fromAddress, memo = Option(memo), timeoutHeight = timeoutHeight, txType = constants.Transaction.User.MAKE_ORDER)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nftID, sellerId = nftOwner.ownerId, buyerId = buyerId, denom = constants.Blockchain.StakingToken, makerOwnableSplit = quantity, expiresIn = expiresIn, takerOwnableSplit = price.value * quantity, secondaryMarketId = secondaryMarketId)
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
        nft <- nft
        (userTransaction, txRawBytes) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase), nft, nftOwner)
        updatedUserTransaction <- broadcastTxAndUpdate(userTransaction, txRawBytes)
      } yield updatedUserTransaction
    }

    private def updateForExpiredOrders() = {
      val allOrderIds = masterSecondaryMarkets.Service.getAllOrderIDs

      def filterExistingOrderIds(allOrderIds: Seq[String]) = blockchainOrders.Service.filterExistingIds(allOrderIds)

      def markOnExpiry(orderIds: Seq[String]) = masterSecondaryMarkets.Service.markOnExpiry(orderIds)

      (for {
        allOrderIds <- allOrderIds
        existingOrderIds <- filterExistingOrderIds(allOrderIds)
        _ <- markOnExpiry(allOrderIds.diff(existingOrderIds))
      } yield ()
        ).recover {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
      }
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {

        val checkExpiredOrders = updateForExpiredOrders()
        val makeOrderTxs = Service.getAllPendingStatus

        def checkAndUpdate(makeOrderTxs: Seq[MakeOrderTransaction]) = utilitiesOperations.traverse(makeOrderTxs) { makeOrderTx =>
          val transaction = userTransactions.Service.tryGet(makeOrderTx.txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccessAndCreationHeight(makeOrderTx.txHash, userTransaction.txHeight.getOrElse(constants.Response.TRANSACTION_HEIGHT_NOT_FOUND.throwBaseException()))
              val nft = masterNFTs.Service.tryGet(makeOrderTx.nftId)
              val updateNFTOwner = masterNFTOwners.Service.onSecondaryMarket(nftId = makeOrderTx.nftId, ownerId = makeOrderTx.sellerId, sellQuantity = makeOrderTx.makerOwnableSplit)

              def sendNotifications(nft: NFT) = utilitiesNotification.send(makeOrderTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_SUCCESSFUL, nft.name)(s"'${nft.id}'")

              def updateOrderId(nft: NFT) = {
                val orderID = makeOrderTx.getOrderID(creationHeight = userTransaction.txHeight.getOrElse(constants.Response.TRANSACTION_HEIGHT_NOT_FOUND.throwBaseException()), nft.getAssetID)
                masterSecondaryMarkets.Service.updateOrderID(makeOrderTx.secondaryMarketId, orderID)
              }

              def markCollectionOnSecondaryMarket(collectionId: String) = masterCollections.Service.markListedOnSecondaryMarket(collectionId)

              def updateAnalytics(collectionId: String) = collectionsAnalysis.Utility.onCreateSecondaryMarket(collectionId, totalListed = makeOrderTx.getQuantity.toInt, listingPrice = makeOrderTx.getPrice)

              (for {
                _ <- markSuccess
                nft <- nft
                _ <- updateNFTOwner
                _ <- updateOrderId(nft)
                _ <- markCollectionOnSecondaryMarket(nft.collectionId)
                _ <- updateAnalytics(nft.collectionId)
                _ <- sendNotifications(nft)
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markFailed = Service.markFailed(makeOrderTx.txHash)
              val nft = masterNFTs.Service.tryGet(makeOrderTx.nftId)
              val markMaster = masterSecondaryMarkets.Service.markOnOrderCreationFailed(makeOrderTxs.filter(_.txHash == makeOrderTx.txHash).map(_.secondaryMarketId))

              def sendNotifications(nft: NFT) = utilitiesNotification.send(makeOrderTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_FAILED, nft.name)(s"'${nft.id}'")

              (for {
                _ <- markFailed
                _ <- markMaster
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
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()

        }

        val forComplete = (for {
          makeOrderTxs <- makeOrderTxs
          _ <- checkAndUpdate(makeOrderTxs)
          _ <- checkExpiredOrders
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}