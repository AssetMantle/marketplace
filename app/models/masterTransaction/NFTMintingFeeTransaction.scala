package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.NFTMintingFee
import models.common.Coin
import models.master.NFT
import models.traits._
import models.{blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class NFTMintingFeeTransaction(txHash: String, nftId: String, collectionId: String, accountId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = nftId
}

object NFTMintingFeeTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_NFT_MINTING_FEE_TRANSACTION

  class NFTMintingFeeTransactionTable(tag: Tag) extends Table[NFTMintingFeeTransaction](tag, "NFTMintingFeeTransaction") with ModelTable2[String, String] {

    def * = (txHash, nftId, collectionId, accountId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTMintingFeeTransaction.tupled, NFTMintingFeeTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def accountId = column[String]("accountId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

  }

  val TableQuery = new TableQuery(tag => new NFTMintingFeeTransactionTable(tag))

}

@Singleton
class NFTMintingFeeTransactions @Inject()(
                                           protected val databaseConfigProvider: DatabaseConfigProvider,
                                           blockchainTransactionNFTMintingFees: blockchainTransaction.NFTMintingFees,
                                           broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                           utilitiesOperations: utilities.Operations,
                                           getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                           getAccount: queries.blockchain.GetAccount,
                                           getAbciInfo: queries.blockchain.GetABCIInfo,
                                           masterCollections: master.Collections,
                                           masterNFTs: master.NFTs,
                                           masterNFTOwners: master.NFTOwners,
                                           utilitiesNotification: utilities.Notification,
                                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[NFTMintingFeeTransactions.NFTMintingFeeTransactionTable, NFTMintingFeeTransaction, String, String](
    databaseConfigProvider,
    NFTMintingFeeTransactions.TableQuery,
    executionContext,
    NFTMintingFeeTransactions.module,
    NFTMintingFeeTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(accountId: String, collectionId: String, txHash: String, nftId: String): Future[Unit] = create(NFTMintingFeeTransaction(txHash = txHash, nftId = nftId, status = None, collectionId = collectionId, accountId = accountId))

    def getByTxHash(txHash: String): Future[Seq[NFTMintingFeeTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(NFTMintingFeeTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(NFTMintingFeeTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[NFTMintingFeeTransaction]] = filter(_.status.?.isEmpty)

  }

  object Utility {
    def transaction(accountId: String, nft: NFT, fromAddress: String, amount: MicroNumber, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = constants.Wallet.FeeCollectorAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)))),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultMintAssetGasLimit),
          gasLimit = constants.Blockchain.DefaultMintAssetGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              nftSale <- blockchainTransactionNFTMintingFees.Service.add(txHash = txHash, fromAddress = fromAddress, toAddress = constants.Wallet.MintAssetWallet.address, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)), status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(accountId = accountId, collectionId = nft.collectionId, txHash = txHash, nftId = nft.id)
            } yield nftSale
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          nftSale <- checkAndAdd(unconfirmedTxHashes)
        } yield (nftSale, txRawBytes)
      }

      def broadcastTxAndUpdate(nftMintingFee: NFTMintingFee, txRawBytes: Array[Byte]) = {

        val broadcastTx = broadcastTxSync.Service.get(nftMintingFee.getTxRawAsHexString(txRawBytes))

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionNFTMintingFees.Service.updateNFTMintingFee(nftMintingFee.copy(status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionNFTMintingFees.Service.updateNFTMintingFee(nftMintingFee.copy(status = Option(false), log = Option(successResponse.get.result.log)))
        else Future(nftMintingFee)

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedNFTSale <- update(successResponse, errorResponse)
        } yield updatedNFTSale
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        (nftSale, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedNFTSale <- broadcastTxAndUpdate(nftSale, txRawBytes)
      } yield updatedNFTSale
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = NFTMintingFeeTransactions.module

      def runner(): Unit = {
        val nftMintingFeeTxs = Service.getAllPendingStatus

        def checkAndUpdate(nftMintingFeeTxs: Seq[NFTMintingFeeTransaction]) = utilitiesOperations.traverse(nftMintingFeeTxs.map(_.txHash).distinct) { txHash =>
          val transaction = blockchainTransactionNFTMintingFees.Service.tryGet(txHash)

          def onTxComplete(transaction: NFTMintingFee) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val mintingNFTs = nftMintingFeeTxs.filter(_.txHash == txHash)
              val markSuccess = Service.markSuccess(txHash)
              val markMintReady = masterNFTs.Service.markReadyForMint(mintingNFTs.map(_.nftId))
              val nft = masterNFTs.Service.tryGet(mintingNFTs.head.nftId)

              def sendNotifications(nftMintingFeeTransaction: NFTMintingFeeTransaction, nft: NFT) = {
                val messageParameter = if (mintingNFTs.length == 1) nft.name else s"${nft.name} (+${mintingNFTs.length - 1})"
                utilitiesNotification.send(nftMintingFeeTransaction.accountId, constants.Notification.NFT_MINTING_FEE_TRANSACTION_SUCCESSFUL, messageParameter)(s"'${nft.id}'")
              }

              (for {
                _ <- markSuccess
                _ <- markMintReady
                nft <- nft
                _ <- sendNotifications(mintingNFTs.head, nft)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val mintingNFTs = Service.getByTxHash(txHash)
              val markMasterFailed = Service.markFailed(txHash)

              def sendNotifications(mintingNFT: NFTMintingFeeTransaction) = {
                utilitiesNotification.send(mintingNFT.accountId, constants.Notification.NFT_MINTING_FEE_TRANSACTION_SUCCESSFUL)("")
              }

              (for {
                mintingNFTs <- mintingNFTs
                _ <- markMasterFailed
                _ <- sendNotifications(mintingNFTs.head)
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
          nftMintingFeeTxs <- nftMintingFeeTxs
          _ <- checkAndUpdate(nftMintingFeeTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}