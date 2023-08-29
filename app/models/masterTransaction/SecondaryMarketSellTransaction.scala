package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.master.{NFT, NFTOwner}
import models.masterTransaction.SecondaryMarketSellTransactions.SecondaryMarketSellTransactionTable
import models.traits._
import models.{analytics, blockchain, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.data.base.{HeightData, NumberData}
import schema.id.base.{AssetID, HashID, OrderID}
import schema.types.Height
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class SecondaryMarketSellTransaction(txHash: String, nftId: String, sellerId: String, orderId: OrderID, quantity: BigInt, expiryHeight: Long, denom: String, receiveAmount: BigInt, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getMakerSplit: NumberData = NumberData(this.quantity)

  def getTakerSplit: NumberData = NumberData(this.receiveAmount)

  def getPrice: MicroNumber = MicroNumber(this.receiveAmount / this.quantity)

  def getExpiryHeightData: HeightData = HeightData(Height(this.expiryHeight))

  def getTakerAssetID: AssetID = schema.document.CoinAsset.getCoinAssetID(this.denom)

  def getOrderID(nftAssetID: AssetID): OrderID = utilities.Order.getOrderID(
    makerID = utilities.Identity.getMantlePlaceIdentityID(this.sellerId),
    makerAssetID = nftAssetID,
    makerSplit = this.getMakerSplit,
    expiryHeight = this.getExpiryHeightData,
    takerAssetID = this.getTakerAssetID,
    takerSplit = this.getTakerSplit
  )

  def serialize: SecondaryMarketSellTransactions.SecondaryMarketSellTransactionSerialized = SecondaryMarketSellTransactions.SecondaryMarketSellTransactionSerialized(
    txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, orderId = this.orderId.asString, quantity = BigDecimal(this.quantity.toString()), expiryHeight = this.expiryHeight, denom = this.denom, receiveAmount = BigDecimal(this.receiveAmount.toString()), status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )

  def getExpiryFromNow(latestBlock: Int): Long = ((this.expiryHeight - latestBlock) * constants.Blockchain.MaxOrderHours) / constants.Blockchain.MaxOrderExpiry
}

private[masterTransaction] object SecondaryMarketSellTransactions {

  case class SecondaryMarketSellTransactionSerialized(txHash: String, nftId: String, sellerId: String, orderId: String, quantity: BigDecimal, expiryHeight: Long, denom: String, receiveAmount: BigDecimal, status: Option[Boolean], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def id: String = txHash

    def deserialize()(implicit module: String, logger: Logger): SecondaryMarketSellTransaction = SecondaryMarketSellTransaction(
      txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, orderId = OrderID(HashID(utilities.Secrets.base64URLDecode(this.orderId))), quantity = this.quantity.toBigInt, expiryHeight = this.expiryHeight, denom = this.denom, receiveAmount = this.receiveAmount.toBigInt, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  class SecondaryMarketSellTransactionTable(tag: Tag) extends Table[SecondaryMarketSellTransactionSerialized](tag, "SecondaryMarketSellTransaction") with ModelTable[String] {

    def * = (txHash, nftId, sellerId, orderId, quantity, expiryHeight, denom, receiveAmount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SecondaryMarketSellTransactionSerialized.tupled, SecondaryMarketSellTransactionSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def sellerId = column[String]("sellerId")

    def orderId = column[String]("orderId", O.Unique)

    def quantity = column[BigDecimal]("quantity")

    def expiryHeight = column[Long]("expiryHeight")

    def denom = column[String]("denom")

    def receiveAmount = column[BigDecimal]("receiveAmount")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class SecondaryMarketSellTransactions @Inject()(
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
  extends GenericDaoImpl[SecondaryMarketSellTransactions.SecondaryMarketSellTransactionTable, SecondaryMarketSellTransactions.SecondaryMarketSellTransactionSerialized, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_SECONDARY_MARKET_SELL_TRANSACTION

  val tableQuery = new TableQuery(tag => new SecondaryMarketSellTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, nft: NFT, sellerId: String, quantity: BigInt, denom: String, receiveAmount: BigInt, expiryHeight: Long): Future[SecondaryMarketSellTransaction] = {
      val orderId = utilities.Order.getOrderID(
        makerID = utilities.Identity.getMantlePlaceIdentityID(sellerId),
        makerAssetID = nft.getAssetID,
        makerSplit = NumberData(quantity),
        expiryHeight = HeightData(expiryHeight),
        takerAssetID = schema.document.CoinAsset.getCoinAssetID(denom),
        takerSplit = NumberData(receiveAmount)
      )
      create(SecondaryMarketSellTransaction(txHash = txHash, nftId = nft.id, sellerId = sellerId, orderId = orderId, quantity = quantity, expiryHeight = expiryHeight, denom = denom, receiveAmount = receiveAmount, status = None).serialize).map(_.deserialize())
    }

    def getByTxHash(txHash: String): Future[Seq[SecondaryMarketSellTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def tryGetByNFTAndOrderId(nftId: String, orderId: String): Future[SecondaryMarketSellTransaction] = filterHead(x => x.nftId === nftId && x.orderId === orderId).map(_.deserialize)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[SecondaryMarketSellTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def checkAnyPendingTx(orderIds: Seq[String]): Future[Seq[String]] = customQuery(tableQuery.filter(x => x.orderId.inSet(orderIds) && x.status.?.isEmpty).map(_.orderId).distinct.result)
  }

  object Utility {
    def transaction(nftID: String, nftOwner: NFTOwner, quantity: Long, fromAddress: String, endHours: Int, price: MicroNumber, gasPrice: BigDecimal, ecKey: ECKey): Future[(BlockchainTransaction, SecondaryMarketSellTransaction)] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)
      val nft = masterNFTs.Service.tryGet(nftID)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String], nft: NFT, nftOwner: NFTOwner) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val expiryHeight = ((constants.Blockchain.MaxOrderExpiry * endHours) / constants.Blockchain.MaxOrderHours).toLong
        val makerID = utilities.Identity.getMantlePlaceIdentityID(nftOwner.ownerId)
        val takerAssetID = constants.Blockchain.StakingTokenAssetID
        val receiveAmount = price.value * quantity

        val (txRawBytes, _) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getPutOrderMsg(
            fromAddress = fromAddress,
            fromID = makerID,
            makerAssetID = nft.getAssetID,
            makerSplit = NumberData(quantity),
            expiryHeight = expiryHeight,
            takerAssetID = takerAssetID,
            takerSplit = NumberData(receiveAmount),
          )),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Transaction.DefaultPutOrderGasLimit),
          gasLimit = constants.Transaction.DefaultPutOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              userTransaction <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = nftOwner.ownerId, fromAddress = fromAddress, timeoutHeight = timeoutHeight, txType = constants.Transaction.User.SECONDARY_MARKET_SELL)
              secondaryMarketSellTx <- Service.addWithNoneStatus(txHash = txHash, nft = nft, sellerId = nftOwner.ownerId, quantity = quantity, denom = constants.Blockchain.StakingToken, expiryHeight = expiryHeight, receiveAmount = receiveAmount)
            } yield (userTransaction, secondaryMarketSellTx)
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          (userTransaction, secondaryMarketSellTx) <- checkAndAdd
        } yield (userTransaction, txRawBytes, secondaryMarketSellTx)
      }

      def broadcastTxAndUpdate(userTransaction: UserTransaction, txRawBytes: Array[Byte]) = userTransactions.Utility.broadcastTxAndUpdate(userTransaction, txRawBytes)

      for {
        (latestHeight, bcAccount, unconfirmedTxs) <- latestHeightAccountUnconfirmedTxs
        nft <- nft
        (userTransaction, txRawBytes, secondaryMarketSellTx) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase), nft, nftOwner)
        updatedUserTransaction <- broadcastTxAndUpdate(userTransaction, txRawBytes)
      } yield (updatedUserTransaction, secondaryMarketSellTx)
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
        val secondaryMarketSellTxs = Service.getAllPendingStatus

        def checkAndUpdate(secondaryMarketSellTxs: Seq[SecondaryMarketSellTransaction]) = utilitiesOperations.traverse(secondaryMarketSellTxs) { secondaryMarketSellTx =>
          val transaction = userTransactions.Service.tryGet(secondaryMarketSellTx.txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccess(secondaryMarketSellTx.txHash)
              val nft = masterNFTs.Service.tryGet(secondaryMarketSellTx.nftId)
              val updateNFTOwner = masterNFTOwners.Service.onSecondaryMarket(nftId = secondaryMarketSellTx.nftId, ownerId = secondaryMarketSellTx.sellerId, sellQuantity = secondaryMarketSellTx.quantity)

              def sendNotifications(nft: NFT) = utilitiesNotification.send(secondaryMarketSellTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_SUCCESSFUL, nft.name)(s"'${nft.id}'")

              def markCollectionOnSecondaryMarket(collectionId: String) = masterCollections.Service.markListedOnSecondaryMarket(collectionId)

              def updateAnalytics(collectionId: String) = collectionsAnalysis.Utility.onCreateSecondaryMarket(collectionId, totalListed = secondaryMarketSellTx.quantity.toInt, listingPrice = secondaryMarketSellTx.getPrice)

              (for {
                _ <- markSuccess
                nft <- nft
                _ <- updateNFTOwner
                _ <- markCollectionOnSecondaryMarket(nft.collectionId)
                _ <- updateAnalytics(nft.collectionId)
                _ <- sendNotifications(nft)
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markFailed = Service.markFailed(secondaryMarketSellTx.txHash)
              val nft = masterNFTs.Service.tryGet(secondaryMarketSellTx.nftId)
              val markMaster = masterSecondaryMarkets.Service.markOnOrderCreationFailed(secondaryMarketSellTxs.filter(_.txHash == secondaryMarketSellTx.txHash).map(_.orderId))

              def sendNotifications(nft: NFT) = utilitiesNotification.send(secondaryMarketSellTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_FAILED, nft.name)(s"'${nft.id}'")

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
          secondaryMarketSellTxs <- secondaryMarketSellTxs
          _ <- checkAndUpdate(secondaryMarketSellTxs)
          _ <- checkExpiredOrders
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}