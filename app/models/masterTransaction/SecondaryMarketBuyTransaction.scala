package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchain.Split
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.common.Coin
import models.master.{NFT, SecondaryMarket}
import models.traits._
import models.{analytics, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class SecondaryMarketBuyTransaction(txHash: String, nftId: String, buyerId: String, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize: SecondaryMarketBuyTransactions.SecondaryMarketBuyTransactionSerialize = SecondaryMarketBuyTransactions.SecondaryMarketBuyTransactionSerialize(txHash = this.txHash, nftId = this.nftId, buyerId = this.buyerId, secondaryMarketId = this.secondaryMarketId, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

}

private[masterTransaction] object SecondaryMarketBuyTransactions {
  case class SecondaryMarketBuyTransactionSerialize(txHash: String, nftId: String, buyerId: String, secondaryMarketId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def id: String = txHash

    def deserialize()(implicit module: String, logger: Logger): SecondaryMarketBuyTransaction = SecondaryMarketBuyTransaction(txHash = this.txHash, nftId = this.nftId, buyerId = this.buyerId, secondaryMarketId = this.secondaryMarketId, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
  }

  class SecondaryMarketBuyTransactionTable(tag: Tag) extends Table[SecondaryMarketBuyTransactionSerialize](tag, "SecondaryMarketBuyTransaction") with ModelTable[String] {

    def * = (txHash, nftId, buyerId, secondaryMarketId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SecondaryMarketBuyTransactionSerialize.tupled, SecondaryMarketBuyTransactionSerialize.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def buyerId = column[String]("buyerId")

    def secondaryMarketId = column[String]("secondaryMarketId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class SecondaryMarketBuyTransactions @Inject()(
                                                protected val dbConfigProvider: DatabaseConfigProvider,
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
  extends GenericDaoImpl[SecondaryMarketBuyTransactions.SecondaryMarketBuyTransactionTable, SecondaryMarketBuyTransactions.SecondaryMarketBuyTransactionSerialize, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_SECONDARY_MARKET_BUY_TRANSACTION

  val tableQuery = new TableQuery(tag => new SecondaryMarketBuyTransactions.SecondaryMarketBuyTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, nftId: String, buyerId: String, secondaryMarketId: String): Future[String] = create(SecondaryMarketBuyTransaction(txHash = txHash, nftId = nftId, buyerId = buyerId, secondaryMarketId = secondaryMarketId, status = None).serialize).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[SecondaryMarketBuyTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[SecondaryMarketBuyTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def checkAnyPendingTx(secondaryMarketIDs: Seq[String]): Future[Seq[String]] = customQuery(tableQuery.filter(x => x.secondaryMarketId.inSet(secondaryMarketIDs) && x.status.?.isEmpty).map(_.secondaryMarketId).distinct.result)
  }

  object Utility {
    def transaction(nftID: String, buyerId: String, fromAddress: String, secondaryMarket: SecondaryMarket, gasPrice: BigDecimal, ecKey: ECKey, split: Option[Split], royaltyFees: MicroNumber, creatorAddress: String): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val secondaryMarketBuyMsg = utilities.BlockchainTransaction.getGetOrderMsg(
          fromAddress = fromAddress,
          fromID = utilities.Identity.getMantlePlaceIdentityID(buyerId),
          orderID = secondaryMarket.getOrderID)
        val wrapCoinMsg = utilities.BlockchainTransaction.getWrapTokenMsg(
          fromAddress = fromAddress,
          fromID = utilities.Identity.getMantlePlaceIdentityID(buyerId),
          coins = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = MicroNumber(secondaryMarket.price.value * secondaryMarket.quantity) - split.fold(MicroNumber.zero)(_.getBalanceAsMicroNumber)))
        )
        val sendCoinMsg = utilities.BlockchainTransaction.getSendCoinMsgAsAny(
          fromAddress = fromAddress,
          toAddress = creatorAddress,
          amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = royaltyFees))
        )
        val (txRawBytes, _) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(wrapCoinMsg, secondaryMarketBuyMsg, sendCoinMsg),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Transaction.DefaultGetOrderGasLimit),
          gasLimit = constants.Transaction.DefaultGetOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              userTransaction <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = buyerId, fromAddress = fromAddress, timeoutHeight = timeoutHeight, txType = constants.Transaction.User.SECONDARY_MARKET_BUY)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nftID, buyerId = buyerId, secondaryMarketId = secondaryMarket.id)
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

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {

        val secondaryMarketBuyTxs = Service.getAllPendingStatus

        def getTxs(hashes: Seq[String]) = userTransactions.Service.getByHashes(hashes)

        def checkAndUpdate(secondaryMarketBuyTxs: Seq[SecondaryMarketBuyTransaction], userTransactions: Seq[UserTransaction]) = utilitiesOperations.traverse(secondaryMarketBuyTxs) { secondaryMarketBuyTx =>
          val userTransaction = userTransactions.find(_.txHash == secondaryMarketBuyTx.txHash).get
          val onTxComplete = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccess(secondaryMarketBuyTx.txHash)
              val nft = masterNFTs.Service.tryGet(secondaryMarketBuyTx.nftId)
              val secondaryMarket = masterSecondaryMarkets.Service.tryGet(secondaryMarketBuyTx.secondaryMarketId)

              def updateSecondaryMarket() = masterSecondaryMarkets.Service.markOnCompletion(secondaryMarketBuyTx.secondaryMarketId)

              def collection(collectionId: String) = masterCollections.Service.tryGet(collectionId)

              def onSuccessfulBuy(collection: master.Collection, secondaryMarket: SecondaryMarket) = masterNFTOwners.Service.onSuccessfulBuyFromSecondaryMarket(nftId = secondaryMarketBuyTx.nftId, collection = collection, totalSold = secondaryMarket.quantity, buyerId = secondaryMarketBuyTx.buyerId)

              def sendNotifications(sellerId: String, nft: NFT) = {
                utilitiesNotification.send(sellerId, constants.Notification.SELLER_TAKE_ORDER_SUCCESSFUL, nft.name)("")
                utilitiesNotification.send(secondaryMarketBuyTx.buyerId, constants.Notification.BUYER_TAKE_ORDER_SUCCESSFUL, nft.name)(s"'${secondaryMarketBuyTx.buyerId}', '${constants.View.COLLECTED}'")
              }

              (for {
                _ <- markSuccess
                nft <- nft
                secondaryMarket <- secondaryMarket
                collection <- collection(nft.collectionId)
                _ <- onSuccessfulBuy(collection, secondaryMarket)
                _ <- updateSecondaryMarket()
                _ <- sendNotifications(secondaryMarket.sellerId, nft)
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markFailed = Service.markFailed(secondaryMarketBuyTx.txHash)
              val nft = masterNFTs.Service.tryGet(secondaryMarketBuyTx.nftId)

              def sendNotifications(nft: NFT) = utilitiesNotification.send(secondaryMarketBuyTx.buyerId, constants.Notification.BUYER_TAKE_ORDER_FAILED, nft.name)("")

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
          secondaryMarketBuyTxs <- secondaryMarketBuyTxs
          txs <- getTxs(secondaryMarketBuyTxs.map(_.txHash).distinct)
          _ <- checkAndUpdate(secondaryMarketBuyTxs, txs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    def checkAlreadySold(nftId: String, secondaryMarket: SecondaryMarket): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      val secondaryMarketBuyTxs = filterAndExists(x => x.nftId === nftId && x.secondaryMarketId === secondaryMarket.id && (x.status || x.status.? === nullStatus))
      for {
        exists <- secondaryMarketBuyTxs
      } yield exists
    }
  }

}