package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
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
import schema.id.base.{AssetID, OrderID}
import schema.types.Height
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class SecondaryMarketSellTransaction(txHash: String, nftId: String, sellerId: String, secondaryMarketId: String, quantity: BigInt, expiryHeight: Long, denom: String, receiveAmount: BigInt, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getMakerSplit: NumberData = NumberData(this.quantity)

  def getTakerSplit: NumberData = NumberData(this.receiveAmount)

  def getPrice: MicroNumber = MicroNumber(this.receiveAmount / this.quantity)

  def getExpiryHeightData: HeightData = HeightData(Height(this.expiryHeight))

  def getTakerAssetID: AssetID = schema.document.CoinAsset.getCoinAssetID(this.denom)

  def serialize: SecondaryMarketSellTransactions.SecondaryMarketSellTransactionSerialized = SecondaryMarketSellTransactions.SecondaryMarketSellTransactionSerialized(
    txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, secondaryMarketId = this.secondaryMarketId, quantity = BigDecimal(this.quantity.toString()), expiryHeight = this.expiryHeight, denom = this.denom, receiveAmount = BigDecimal(this.receiveAmount.toString()), status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )

  def getExpiryFromNow(latestBlock: Int): Long = ((this.expiryHeight - latestBlock) * constants.Blockchain.MaxOrderHours) / constants.Blockchain.MaxOrderExpiry
}

private[masterTransaction] object SecondaryMarketSellTransactions {

  case class SecondaryMarketSellTransactionSerialized(txHash: String, nftId: String, sellerId: String, secondaryMarketId: String, quantity: BigDecimal, expiryHeight: Long, denom: String, receiveAmount: BigDecimal, status: Option[Boolean], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def id: String = txHash

    def deserialize()(implicit module: String, logger: Logger): SecondaryMarketSellTransaction = SecondaryMarketSellTransaction(
      txHash = this.txHash, nftId = this.nftId, sellerId = this.sellerId, secondaryMarketId = this.secondaryMarketId, quantity = this.quantity.toBigInt, expiryHeight = this.expiryHeight, denom = this.denom, receiveAmount = this.receiveAmount.toBigInt, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  class SecondaryMarketSellTransactionTable(tag: Tag) extends Table[SecondaryMarketSellTransactionSerialized](tag, "SecondaryMarketSellTransaction") with ModelTable[String] {

    def * = (txHash, nftId, sellerId, secondaryMarketId, quantity, expiryHeight, denom, receiveAmount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SecondaryMarketSellTransactionSerialized.tupled, SecondaryMarketSellTransactionSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def sellerId = column[String]("sellerId")

    def secondaryMarketId = column[String]("secondaryMarketId", O.Unique)

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
                                                 utilitiesTransaction: utilities.Transaction,
                                                 collectionsAnalysis: analytics.CollectionsAnalysis,
                                                 utilitiesNotification: utilities.Notification,
                                                 userTransactions: UserTransactions,
                                               )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[SecondaryMarketSellTransactions.SecondaryMarketSellTransactionTable, SecondaryMarketSellTransactions.SecondaryMarketSellTransactionSerialized, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_SECONDARY_MARKET_SELL_TRANSACTION

  val tableQuery = new TableQuery(tag => new SecondaryMarketSellTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, nft: NFT, sellerId: String, quantity: BigInt, denom: String, receiveAmount: BigInt, expiryHeight: Long, secondaryMarketId: String): Future[String] = create(SecondaryMarketSellTransaction(txHash = txHash, nftId = nft.id, sellerId = sellerId, secondaryMarketId = secondaryMarketId, quantity = quantity, expiryHeight = expiryHeight, denom = denom, receiveAmount = receiveAmount, status = None).serialize).map(_.txHash)

    def getByTxHash(txHash: String): Future[Seq[SecondaryMarketSellTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize()))

    def tryGetByNFTAndSecondaryMarketId(nftId: String, secondaryMarketId: String): Future[SecondaryMarketSellTransaction] = filterHead(x => x.nftId === nftId && x.secondaryMarketId === secondaryMarketId).map(_.deserialize)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[SecondaryMarketSellTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize()))

    def checkAnyPendingTx(secondaryMarketIds: Seq[String]): Future[Seq[String]] = customQuery(tableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIds) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)
  }

  object Utility {
    implicit val txUtil: TxUtil = TxUtil("SECONDARY_MARKET_SELL", 300000)

    def transaction(secondaryMarketId: String, nft: NFT, nftOwner: NFTOwner, quantity: Long, fromAddress: String, endHours: Int, price: MicroNumber, gasPrice: BigDecimal, ecKey: ECKey, latestHeight: Long): Future[(UserTransaction, OrderID)] = {
      val expiryHeight = latestHeight + ((constants.Blockchain.MaxOrderExpiry * endHours) / constants.Blockchain.MaxOrderHours).toLong
      val makerID = utilities.Identity.getMantlePlaceIdentityID(nftOwner.ownerId)
      val takerAssetID = constants.Blockchain.StakingTokenAssetID
      val receiveAmount = price.value * quantity
      val messages = Seq(utilities.BlockchainTransaction.getPutOrderMsg(
        fromAddress = fromAddress,
        fromID = makerID,
        makerAssetID = nft.getAssetID,
        makerSplit = NumberData(quantity),
        expiryHeight = expiryHeight,
        takerAssetID = takerAssetID,
        takerSplit = NumberData(receiveAmount),
      ))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, nft = nft, sellerId = nftOwner.ownerId, quantity = quantity, denom = constants.Blockchain.StakingToken, expiryHeight = expiryHeight, receiveAmount = receiveAmount, secondaryMarketId = secondaryMarketId)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = nftOwner.ownerId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, txHash) <- userTx
      } yield {
        val orderId = utilities.Order.getOrderID(
          makerID = utilities.Identity.getMantlePlaceIdentityID(nftOwner.ownerId),
          makerAssetID = nft.getAssetID,
          makerSplit = NumberData(quantity),
          expiryHeight = HeightData(expiryHeight),
          takerAssetID = schema.document.CoinAsset.getCoinAssetID(constants.Blockchain.StakingToken),
          takerSplit = NumberData(receiveAmount)
        )
        (userTx, orderId)
      }
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {
        val secondaryMarketSellTxs = Service.getAllPendingStatus

        def checkAndUpdate(secondaryMarketSellTxs: Seq[SecondaryMarketSellTransaction]) = utilitiesOperations.traverse(secondaryMarketSellTxs) { secondaryMarketSellTx =>
          val transaction = userTransactions.Service.tryGet(secondaryMarketSellTx.txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccess(secondaryMarketSellTx.txHash)
              val nft = masterNFTs.Service.tryGet(secondaryMarketSellTx.nftId)
              val markSecondaryMarket = masterSecondaryMarkets.Service.markSecondaryMarketCreated(secondaryMarketSellTx.secondaryMarketId)
              val updateNFTOwner = masterNFTOwners.Service.onSecondaryMarket(nftId = secondaryMarketSellTx.nftId, ownerId = secondaryMarketSellTx.sellerId, sellQuantity = secondaryMarketSellTx.quantity)

              def sendNotifications(nft: NFT) = utilitiesNotification.send(secondaryMarketSellTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_SUCCESSFUL, nft.name)(s"'${nft.id}'")

              def markCollectionOnSecondaryMarket(collectionId: String) = masterCollections.Service.markListedOnSecondaryMarket(collectionId)

              def updateAnalytics(collectionId: String) = collectionsAnalysis.Utility.onCreateSecondaryMarket(collectionId, totalListed = secondaryMarketSellTx.quantity.toInt, listingPrice = secondaryMarketSellTx.getPrice)

              for {
                _ <- markSuccess
                nft <- nft
                _ <- updateNFTOwner
                _ <- markSecondaryMarket
                _ <- markCollectionOnSecondaryMarket(nft.collectionId)
                _ <- updateAnalytics(nft.collectionId)
                _ <- sendNotifications(nft)
              } yield ()
            } else {
              val markFailed = Service.markFailed(secondaryMarketSellTx.txHash)
              val nft = masterNFTs.Service.tryGet(secondaryMarketSellTx.nftId)
              val markMaster = masterSecondaryMarkets.Service.markOnOrderCreationFailed(secondaryMarketSellTxs.filter(_.txHash == secondaryMarketSellTx.txHash).map(_.secondaryMarketId))

              def sendNotifications(nft: NFT) = utilitiesNotification.send(secondaryMarketSellTx.sellerId, constants.Notification.SECONDARY_MARKET_CREATION_FAILED, nft.name)(s"'${nft.id}'")

              for {
                _ <- markFailed
                _ <- markMaster
                nft <- nft
                _ <- sendNotifications(nft)
              } yield ()
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
        } yield ()).recover {
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}