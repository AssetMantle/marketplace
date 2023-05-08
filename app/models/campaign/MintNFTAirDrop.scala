package models.campaign

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction
import models.blockchainTransaction.CampaignSendCoin
import models.common.Coin
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MintNFTAirDrop(accountId: String, address: String, amount: Long, eligibilityTxHash: String, airdropTxHash: Option[String], status: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = accountId

}

object MintNFTAirDrops {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.CAMPAIGN_MINT_NFT_AIRDROP

  class MintNFTAirDropTable(tag: Tag) extends Table[MintNFTAirDrop](tag, "MintNFTAirDrop") with ModelTable[String] {

    def * = (accountId, address, amount, eligibilityTxHash, airdropTxHash.?, status, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MintNFTAirDrop.tupled, MintNFTAirDrop.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def address = column[String]("address")

    def amount = column[Long]("amount")

    def eligibilityTxHash = column[String]("eligibilityTxHash")

    def airdropTxHash = column[String]("airdropTxHash")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = accountId

  }

  val TableQuery = new TableQuery(tag => new MintNFTAirDropTable(tag))

}

@Singleton
class MintNFTAirDrops @Inject()(
                                 protected val databaseConfigProvider: DatabaseConfigProvider,
                                 broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                 utilitiesOperations: utilities.Operations,
                                 blockchainTransactionCampaignSendCoins: blockchainTransaction.CampaignSendCoins,
                                 getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                 getAccount: queries.blockchain.GetAccount,
                                 getAbciInfo: queries.blockchain.GetABCIInfo,
                                 utilitiesNotification: utilities.Notification,
                               )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[MintNFTAirDrops.MintNFTAirDropTable, MintNFTAirDrop, String](
    databaseConfigProvider,
    MintNFTAirDrops.TableQuery,
    executionContext,
    MintNFTAirDrops.module,
    MintNFTAirDrops.logger
  ) {

  object Service {

    def addForDropping(accountIdsAddressMap: Map[String, String], amount: Long, eligibilityTxHash: String): Future[Unit] = create(accountIdsAddressMap.map(x => MintNFTAirDrop(accountId = x._1, address = x._2, amount = amount, eligibilityTxHash = eligibilityTxHash, airdropTxHash = None, status = false)).toSeq)

    def updateDropTxHash(accountId: String, airDropTxHash: String): Future[Int] = customUpdate(MintNFTAirDrops.TableQuery.filter(_.accountId === accountId).map(_.airdropTxHash).update(airDropTxHash))

    def getByAirDropTxHash(txHash: String): Future[Seq[MintNFTAirDrop]] = filter(_.airdropTxHash === txHash)

    def markSuccess(accountId: String): Future[Int] = customUpdate(MintNFTAirDrops.TableQuery.filter(_.accountId === accountId).map(_.status).update(true))

    def markFailed(accountId: String): Future[Int] = customUpdate(MintNFTAirDrops.TableQuery.filter(_.accountId === accountId).map(_.status).update(false))

    def getAllForDropping: Future[Seq[MintNFTAirDrop]] = filter(x => x.airdropTxHash.?.isEmpty && !x.status)

    def getAllForUpdates: Future[Seq[MintNFTAirDrop]] = filter(x => x.airdropTxHash.?.isDefined && !x.status)

    def filterExisting(accountIds: Seq[String]): Future[Seq[String]] = {
      val existingAccountIds = filter(_.accountId.inSet(accountIds)).map(_.map(_.accountId))
      for {
        existingAccountIds <- existingAccountIds
      } yield accountIds.diff(existingAccountIds)
    }
  }

  object Utility {

    private def transaction(accountId: String, address: String, amount: MicroNumber, eligibilityTxHash: String): Future[Unit] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(constants.Campaign.AirDropWallet.address).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val txRawBytes = utilities.BlockchainTransaction.getTxRawBytes(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = constants.Campaign.AirDropWallet.address, toAddress = address, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)))),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = constants.Transaction.MediumGasPrice, gasLimit = constants.Blockchain.DefaultSendCoinGasAmount),
          gasLimit = constants.Blockchain.DefaultSendCoinGasAmount,
          account = bcAccount,
          ecKey = constants.Campaign.AirDropWallet.getECKey,
          timeoutHeight = timeoutHeight,
          memo = eligibilityTxHash)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              mintNFTAirDrop <- blockchainTransactionCampaignSendCoins.Service.add(txHash = txHash, fromAddress = constants.Campaign.AirDropWallet.address, status = None, memo = Option(eligibilityTxHash), timeoutHeight = timeoutHeight)
              _ <- Service.updateDropTxHash(accountId = accountId, airDropTxHash = txHash)
            } yield mintNFTAirDrop
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          _ <- checkAndAdd
        } yield (txHash, txRawBytes)
      }

      def broadcastTxAndUpdate(txHash: String, txRawBytes: Array[Byte]) = {

        val broadcastTx = broadcastTxSync.Service.get(txRawBytes.map("%02x".format(_)).mkString.toUpperCase)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionCampaignSendCoins.Service.markFailedWithLog(txHashes = Seq(txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionCampaignSendCoins.Service.markFailedWithLog(txHashes = Seq(txHash), log = successResponse.get.result.log)
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
        (txHash, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(txHash, txRawBytes)
      } yield ()
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = MintNFTAirDrops.module

      def runner(): Unit = {
        val toBeDropped = Service.getAllForDropping
        val checkDrops = Service.getAllForUpdates

        def dropTokens(drops: Seq[MintNFTAirDrop]) = utilitiesOperations.traverse(drops) { drop =>
          transaction(accountId = drop.accountId, address = drop.address, amount = MicroNumber(BigInt(drop.amount)), eligibilityTxHash = drop.eligibilityTxHash)
        }

        def checkAndUpdate(drops: Seq[MintNFTAirDrop]) = utilitiesOperations.traverse(drops) { drop =>

          val transaction = blockchainTransactionCampaignSendCoins.Service.tryGet(drop.airdropTxHash.get)

          def onTxComplete(transaction: CampaignSendCoin) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(drop.accountId)
              val sendNotifications = utilitiesNotification.send(drop.accountId, constants.Notification.MINT_NFT_AIR_DROP_SUCCESSFUL)("")

              (for {
                _ <- markSuccess
                _ <- sendNotifications
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(drop.accountId)

              (for {
                _ <- markMasterFailed
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
          toBeDropped <- toBeDropped
          checkDrops <- checkDrops
          _ <- checkAndUpdate(checkDrops)
          _ <- dropTokens(toBeDropped)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}