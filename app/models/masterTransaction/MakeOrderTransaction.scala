package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.Trait._
import models.blockchainTransaction.MakeOrder
import models.master.{NFT, NFTOwner}
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

case class MakeOrderTransaction(txHash: String, nftId: String, sellerAccountId: String, buyerAccountId: Option[String], denom: String, makerOwnableSplit: AttoNumber, expiryHeight: Long, takerOwnableSplit: AttoNumber, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getOrderID(creationHeight: Int, assetID: AssetID): OrderID = utilities.Order.getOrderID(
    classificationID = constants.Blockchain.MantlePlaceOrderClassificationID,
    properties = Seq(constants.Orders.OriginMetaProperty),
    makerID = utilities.Identity.getMantlePlaceIdentityID(this.sellerAccountId),
    makerOwnableID = assetID,
    makerOwnableSplit = this.makerOwnableSplit.toBigDecimal,
    creationHeight = creationHeight,
    takerID = if (buyerAccountId.isEmpty) constants.Blockchain.EmptyIdentityID else utilities.Identity.getMantlePlaceIdentityID(buyerAccountId.get),
    takerOwnableID = constants.Blockchain.StakingTokenCoinID,
    takerOwnableSplit = this.takerOwnableSplit.toBigDecimal
  )

  def serialize: MakeOrderTransactions.MakeOrderTransactionSerialized = MakeOrderTransactions.MakeOrderTransactionSerialized(
    txHash = this.txHash, nftId = this.nftId, sellerAccountId = this.sellerAccountId, buyerAccountId = this.buyerAccountId, denom = this.denom, makerOwnableSplit = this.makerOwnableSplit.toBigDecimal, expiryHeight = this.expiryHeight, takerOwnableSplit = this.takerOwnableSplit.toBigDecimal, secondaryMarketId = this.secondaryMarketId, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )
}

object MakeOrderTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_MAKE_ORDER_TRANSACTION

  case class MakeOrderTransactionSerialized(txHash: String, nftId: String, sellerAccountId: String, buyerAccountId: Option[String], denom: String, makerOwnableSplit: BigDecimal, expiryHeight: Long, takerOwnableSplit: BigDecimal, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity3[String, String, String] {
    def id1: String = txHash

    def id2: String = nftId

    def id3: String = sellerAccountId

    def deserialize: MakeOrderTransaction = MakeOrderTransaction(
      txHash = this.txHash, nftId = this.nftId, sellerAccountId = this.sellerAccountId, buyerAccountId = this.buyerAccountId, denom = this.denom, makerOwnableSplit = AttoNumber(this.makerOwnableSplit), expiryHeight = this.expiryHeight, takerOwnableSplit = AttoNumber(this.takerOwnableSplit), secondaryMarketId = this.secondaryMarketId, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  class MakeOrderTransactionTable(tag: Tag) extends Table[MakeOrderTransactionSerialized](tag, "MakeOrderTransaction") with ModelTable3[String, String, String] {

    def * = (txHash, nftId, sellerAccountId, buyerAccountId.?, denom, makerOwnableSplit, expiryHeight, takerOwnableSplit, secondaryMarketId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MakeOrderTransactionSerialized.tupled, MakeOrderTransactionSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def sellerAccountId = column[String]("sellerAccountId", O.PrimaryKey)

    def buyerAccountId = column[String]("buyerAccountId")

    def denom = column[String]("denom")

    def makerOwnableSplit = column[BigDecimal]("makerOwnableSplit")

    def expiryHeight = column[Long]("expiryHeight")

    def takerOwnableSplit = column[BigDecimal]("takerOwnableSplit")

    def secondaryMarketId = column[String]("secondaryMarketId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

    def id3 = sellerAccountId

  }

  val TableQuery = new TableQuery(tag => new MakeOrderTransactionTable(tag))

}

@Singleton
class MakeOrderTransactions @Inject()(
                                       protected val databaseConfigProvider: DatabaseConfigProvider,
                                       blockchainAccounts: blockchain.Accounts,
                                       blockchainTransactions: blockchain.Transactions,
                                       blockchainTransactionMakeOrders: blockchainTransaction.MakeOrders,
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

    def addWithNoneStatus(txHash: String, nftId: String, sellerAccountId: String, buyerAccountId: Option[String], denom: String, makerOwnableSplit: AttoNumber, expiryHeight: Long, takerOwnableSplit: AttoNumber, secondaryMarketId: String): Future[Unit] = create(MakeOrderTransaction(txHash = txHash, nftId = nftId, sellerAccountId = sellerAccountId, buyerAccountId = buyerAccountId, denom = denom, makerOwnableSplit = makerOwnableSplit, expiryHeight = expiryHeight, takerOwnableSplit = takerOwnableSplit, secondaryMarketId = secondaryMarketId, status = None).serialize)

    def checkAlreadySold(nftIds: Seq[String], secondaryMarketId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId.inSet(nftIds) && x.secondaryMarketId === secondaryMarketId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def countBuyerNFTsFromMakeOrder(buyerAccountId: String, secondaryMarketId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.secondaryMarketId === secondaryMarketId && (x.status.? === nullStatus || x.status))
    }

    def getTotalMakeOrderSold(secondaryMarketId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.secondaryMarketId === secondaryMarketId && (x.status.? === nullStatus || x.status))
    }

    def getByTxHash(txHash: String): Future[Seq[MakeOrderTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def markSuccess(txHash: String): Future[Int] = customUpdate(MakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(MakeOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[MakeOrderTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def checkAnyPendingTx(secondaryMarketIDs: Seq[String]): Future[Seq[String]] = customQuery(MakeOrderTransactions.TableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIDs) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)
  }

  object Utility {
    def transaction(nftID: String, nftOwner: NFTOwner, endTimeEpoch: Long, price: MicroNumber, buyerAccountId: Option[String], secondaryMarketId: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(constants.Blockchain.MantlePlaceMaintainerAddress).map(_.account.toSerializableAccount(constants.Blockchain.MantlePlaceMaintainerAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val nft = masterNFTs.Service.tryGet(nftID)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String], nft: NFT, nftOwner: NFTOwner) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val expiryHeight = latestBlockHeight + (endTimeEpoch - utilities.Date.currentEpoch) / 6
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getMakeOrderMsg(
            fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress,
            fromID = constants.Blockchain.MantlePlaceFromID,
            classificationID = constants.Blockchain.MantlePlaceOrderClassificationID,
            takerID = if (buyerAccountId.isEmpty) constants.Blockchain.EmptyIdentityID else utilities.Identity.getMantlePlaceIdentityID(buyerAccountId.get),
            makerOwnableID = nft.getAssetID,
            makerOwnableSplit = nftOwner.getSplitValue.toBigDecimal,
            expiryHeight = expiryHeight,
            takerOwnableID = constants.Blockchain.StakingTokenCoinID,
            takerOwnableSplit = price.toMicroBigDecimal,
            immutableMetas = Seq(constants.Orders.OriginMetaProperty),
            mutableMetas = Seq(),
            immutables = Seq(),
            mutables = Seq())),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultMintAssetGasLimit),
          gasLimit = constants.Blockchain.DefaultMintAssetGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              mintAsset <- blockchainTransactionMakeOrders.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nftID, sellerAccountId = nftOwner.ownerId, buyerAccountId = buyerAccountId, denom = constants.Blockchain.StakingToken, makerOwnableSplit = nftOwner.getSplitValue, expiryHeight = expiryHeight, takerOwnableSplit = AttoNumber(price.toMicroBigDecimal), secondaryMarketId = secondaryMarketId)
            } yield mintAsset
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          mintAsset <- checkAndAdd(unconfirmedTxHashes)
        } yield mintAsset
      }

      def broadcastTxAndUpdate(makerOrder: MakeOrder) = {

        println(makerOrder.getTxRawAsHexString)

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
        mintAsset <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase), nft, nftOwner)
        _ <- broadcastTxAndUpdate(mintAsset)
      } yield mintAsset
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = MakeOrderTransactions.module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {

        val makeOrderTxs = Service.getAllPendingStatus

        def getTxs(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def checkAndUpdate(makeOrderTxs: Seq[MakeOrderTransaction], txs: Seq[blockchain.Transaction]) = utilitiesOperations.traverse(makeOrderTxs) { makeOrderTx =>
          val transaction = blockchainTransactionMakeOrders.Service.tryGet(makeOrderTx.txHash)

          def onTxComplete(transaction: MakeOrder) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(makeOrderTx.txHash)
              val nft = masterNFTs.Service.tryGet(makeOrderTx.nftId)

              def updateOrderId(nft: NFT) = {
                val orderID = makeOrderTx.getOrderID(creationHeight = txs.find(_.hash == makeOrderTx.txHash).getOrElse(constants.Response.TRANSACTION_NOT_FOUND.throwBaseException()).height, nft.getAssetID)
                masterSecondaryMarkets.Service.updateOrderID(makeOrderTx.secondaryMarketId, orderID)
              }

              (for {
                _ <- markSuccess
                nft <- nft
                _ <- updateOrderId(nft)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markFailed = Service.markFailed(makeOrderTx.txHash)
              val updateNFTOwner = masterNFTOwners.Service.markSecondaryMarketNull(secondaryMarketId = makeOrderTxs.filter(_.txHash == makeOrderTx.txHash).head.secondaryMarketId)

              (for {
                _ <- markFailed
                _ <- updateNFTOwner
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
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}