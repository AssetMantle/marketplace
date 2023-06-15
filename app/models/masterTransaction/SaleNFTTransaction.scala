package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.common.Coin
import models.master.{Collection, NFT}
import models.masterTransaction.SaleNFTTransactions.SaleNFTTransactionTable
import models.traits._
import models.{analytics, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class SaleNFTTransaction(txHash: String, nftId: String, buyerAccountId: String, sellerAccountId: String, mintOnSuccess: Boolean, saleId: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize: SaleNFTTransactions.SaleNFTTransactionSerialize = SaleNFTTransactions.SaleNFTTransactionSerialize(
    txHash = this.txHash,
    nftId = this.nftId,
    buyerAccountId = this.buyerAccountId,
    sellerAccountId = this.sellerAccountId,
    mintOnSuccess = this.mintOnSuccess,
    saleId = this.saleId,
    toAddress = this.toAddress,
    amount = Json.toJson(this.amount).toString,
    status = this.status,
    createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )
}

private[masterTransaction] object SaleNFTTransactions {

  case class SaleNFTTransactionSerialize(txHash: String, nftId: String, buyerAccountId: String, sellerAccountId: String, mintOnSuccess: Boolean, saleId: String, toAddress: String, amount: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] {
    def id1: String = txHash

    def id2: String = nftId

    def deserialize()(implicit module: String, logger: Logger): SaleNFTTransaction = SaleNFTTransaction(
      txHash = this.txHash,
      nftId = this.nftId,
      buyerAccountId = this.buyerAccountId,
      sellerAccountId = this.sellerAccountId,
      mintOnSuccess = this.mintOnSuccess,
      saleId = this.saleId,
      toAddress = this.toAddress,
      amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](this.amount),
      status = this.status,
      createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )
  }

  class SaleNFTTransactionTable(tag: Tag) extends Table[SaleNFTTransactionSerialize](tag, "SaleNFTTransaction") with ModelTable2[String, String] {

    def * = (txHash, nftId, buyerAccountId, sellerAccountId, mintOnSuccess, saleId, toAddress, amount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SaleNFTTransactionSerialize.tupled, SaleNFTTransactionSerialize.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def buyerAccountId = column[String]("buyerAccountId")

    def sellerAccountId = column[String]("sellerAccountId")

    def mintOnSuccess = column[Boolean]("mintOnSuccess")

    def saleId = column[String]("saleId")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

  }

}

@Singleton
class SaleNFTTransactions @Inject()(
                                     protected val dbConfigProvider: DatabaseConfigProvider,
                                     utilitiesOperations: utilities.Operations,
                                     masterNFTOwners: master.NFTOwners,
                                     collectionsAnalysis: analytics.CollectionsAnalysis,
                                     masterNFTs: master.NFTs,
                                     masterSales: master.Sales,
                                     userTransactions: UserTransactions,
                                     utilitiesNotification: utilities.Notification,
                                   )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SaleNFTTransactions.SaleNFTTransactionTable, SaleNFTTransactions.SaleNFTTransactionSerialize, String, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_SALE_NFT_TRANSACTION

  val tableQuery = new TableQuery(tag => new SaleNFTTransactionTable(tag))

  object Service {
    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftIds: Seq[String], saleId: String, toAddress: String, amount: Seq[Coin], mintOnSuccess: Boolean): Future[Int] = create(nftIds.map(x => SaleNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = x, saleId = saleId, toAddress = toAddress, amount = amount, status = None, mintOnSuccess = mintOnSuccess).serialize))

    def update(saleNFTTransaction: SaleNFTTransaction): Future[Int] = updateById1AndId2(saleNFTTransaction.serialize)

    def checkAlreadySold(nftId: String, saleId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filterAndExists(x => x.nftId === nftId && x.saleId === saleId && (x.status || x.status.? === nullStatus))
    }

    def checkAlreadySold(nftIds: Seq[String], saleId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId.inSet(nftIds) && x.saleId === saleId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def countBuyerNFTsFromSale(buyerAccountId: String, saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.saleId === saleId && (x.status.? === nullStatus || x.status))
    }

    def getByTxHash(txHash: String): Future[Seq[SaleNFTTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def getByTxHashes(txHashes: Seq[String]): Future[Seq[SaleNFTTransaction]] = filter(_.txHash.inSet(txHashes)).map(_.map(_.deserialize))

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[SaleNFTTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def getTotalWhitelistSaleSold(saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.saleId === saleId && (x.status.? === nullStatus || x.status))
    }

    def checkAnyPendingTx(saleIDs: Seq[String]): Future[Seq[String]] = customQuery(tableQuery.filter(x => x.saleId.inSet(saleIDs) && x.status.?.isEmpty).map(_.saleId).distinct.result)
  }

  object Utility {
    def transaction(buyerAccountId: String, sellerAccountId: String, nftIds: Seq[String], saleId: String, mintOnSuccess: Boolean, fromAddress: String, collection: Collection, toAddress: String, amount: MicroNumber, gasPrice: BigDecimal, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val messages = if (mintOnSuccess) Seq(
          utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount))),
          utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = constants.Wallet.FeeCollectorAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = nftIds.length * collection.getBondAmount)))
        ) else Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount))))
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = messages,
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              nftSale <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = buyerAccountId, fromAddress = fromAddress, memo = Option(memo), timeoutHeight = timeoutHeight, txType = constants.Transaction.User.WHITELIST_SALE)
              _ <- Service.addWithNoneStatus(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftIds = nftIds, saleId = saleId, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)), mintOnSuccess = mintOnSuccess)
            } yield nftSale
          }
          else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          nftSale <- checkAndAdd
        } yield (nftSale, txRawBytes)
      }

      def broadcastTxAndUpdate(userTransaction: UserTransaction, txRawBytes: Array[Byte]) = userTransactions.Utility.broadcastTxAndUpdate(userTransaction, txRawBytes)

      for {
        (latestHeight, bcAccount, unconfirmedTxs) <- latestHeightAccountUnconfirmedTxs
        (nftSale, txRawBytes) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedUserTransaction <- broadcastTxAndUpdate(nftSale, txRawBytes)
      } yield updatedUserTransaction
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val saleNFTTxS = Service.getAllPendingStatus

        def checkAndUpdate(saleNFTTxs: Seq[SaleNFTTransaction]) = utilitiesOperations.traverse(saleNFTTxs.map(_.txHash).distinct) { txHash =>
          val userTransaction = userTransactions.Service.tryGet(txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val boughtNFTs = saleNFTTxs.filter(_.txHash == userTransaction.txHash)
              val markMasterSuccess = Service.markSuccess(userTransaction.txHash)
              val markForMinting = masterNFTs.Service.markReadyForMint(boughtNFTs.filter(_.mintOnSuccess).map(_.nftId))
              val sale = masterSales.Service.tryGet(boughtNFTs.head.saleId)
              val nft = masterNFTs.Service.tryGet(boughtNFTs.head.nftId)

              def transferNFTOwnership(boughtNFTs: Seq[SaleNFTTransaction]) = utilitiesOperations.traverse(boughtNFTs) { boughtNFT =>
                masterNFTOwners.Service.markNFTSoldFromSale(nftId = boughtNFT.nftId, saleId = boughtNFT.saleId, sellerAccountId = boughtNFT.sellerAccountId, buyerAccountId = boughtNFT.buyerAccountId)
              }

              def analysisUpdate(nft: NFT, price: MicroNumber, quantity: Int) = collectionsAnalysis.Utility.onSuccessfulSell(collectionId = nft.collectionId, price = price, quantity = quantity)

              def sendNotifications(boughtNFT: SaleNFTTransaction, count: Int) = {
                utilitiesNotification.send(boughtNFT.sellerAccountId, constants.Notification.SELLER_BUY_NFT_SUCCESSFUL_FROM_SALE, count.toString)("")
                utilitiesNotification.send(boughtNFT.buyerAccountId, constants.Notification.BUYER_BUY_NFT_SUCCESSFUL_FROM_SALE, count.toString)(s"'${boughtNFT.buyerAccountId}', '${constants.View.COLLECTED}'")
              }

              (for {
                _ <- markMasterSuccess
                _ <- transferNFTOwnership(boughtNFTs)
                _ <- markForMinting
                sale <- sale
                nft <- nft
                _ <- analysisUpdate(nft, sale.price, boughtNFTs.length)
                _ <- sendNotifications(boughtNFTs.head, boughtNFTs.length)
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val boughtNFTs = Service.getByTxHash(userTransaction.txHash)
              val markMasterFailed = Service.markFailed(userTransaction.txHash)

              def sendNotifications(buyNFTTx: SaleNFTTransaction, count: Int) = utilitiesNotification.send(buyNFTTx.buyerAccountId, constants.Notification.BUYER_BUY_NFT_FAILED, count.toString)("")

              (for {
                boughtNFTs <- boughtNFTs
                _ <- markMasterFailed
                _ <- sendNotifications(boughtNFTs.head, boughtNFTs.length)
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

        val forComplete = (for {
          saleNFTTxS <- saleNFTTxS
          _ <- checkAndUpdate(saleNFTTxS)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}