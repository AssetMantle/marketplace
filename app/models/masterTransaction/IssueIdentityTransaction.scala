package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.Trait._
import models.blockchainTransaction.IssueIdentity
import models.{blockchain, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class IssueIdentityTransaction(txHash: String, accountId: String, twitter: String, note1: String, note2: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.accountId)
}

object IssueIdentityTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_ISSUE_IDENTITY_TRANSACTION

  class IssueIdentityTransactionTable(tag: Tag) extends Table[IssueIdentityTransaction](tag, "IssueIdentityTransaction") with ModelTable[String] {

    def * = (txHash, accountId, twitter, note1, note2, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (IssueIdentityTransaction.tupled, IssueIdentityTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId")

    def twitter = column[String]("buyerAccountId")

    def note1 = column[String]("sellerAccountId")

    def note2 = column[String]("secondaryMarketId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new IssueIdentityTransactionTable(tag))

}

@Singleton
class IssueIdentityTransactions @Inject()(
                                           protected val databaseConfigProvider: DatabaseConfigProvider,
                                           blockchainAccounts: blockchain.Accounts,
                                           masterAccounts: master.Accounts,
                                           broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                           utilitiesOperations: utilities.Operations,
                                           getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                           getAccount: queries.blockchain.GetAccount,
                                           getAbciInfo: queries.blockchain.GetABCIInfo,
                                           utilitiesNotification: utilities.Notification,
                                           blockchainTransactionIssueIdentities: blockchainTransaction.IssueIdentities,
                                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[IssueIdentityTransactions.IssueIdentityTransactionTable, IssueIdentityTransaction, String](
    databaseConfigProvider,
    IssueIdentityTransactions.TableQuery,
    executionContext,
    IssueIdentityTransactions.module,
    IssueIdentityTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, accountIds: Seq[String]): Future[Unit] = create(accountIds.map(x => IssueIdentityTransaction(txHash = txHash, accountId = x, twitter = "", note1 = "", note2 = "", status = None)))

    def getByTxHash(txHash: String): Future[Seq[IssueIdentityTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(IssueIdentityTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(IssueIdentityTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[IssueIdentityTransaction]] = filter(_.status.?.isEmpty)
  }

  object Utility {

    def transaction(accountIds: Seq[String], toAddress: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(constants.Blockchain.MantlePlaceMaintainerAddress).map(_.account.toSerializableAccount(constants.Blockchain.MantlePlaceMaintainerAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = accountIds.map(x => utilities.BlockchainTransaction.getMantlePlaceIdentityMsg(id = x, fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, toAddress = toAddress, classificationID = constants.Blockchain.MantlePlaceIdentityClassificationID, fromID = constants.Blockchain.MantlePlaceFromID)),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultIssueIdentityGasLimit * accountIds.length),
          gasLimit = constants.Blockchain.DefaultIssueIdentityGasLimit * accountIds.length,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              issueIdentity <- blockchainTransactionIssueIdentities.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, toAddress = toAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, accountIds = accountIds)
            } yield issueIdentity
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          issueIdentity <- checkAndAdd(unconfirmedTxHashes)
        } yield issueIdentity
      }

      def broadcastTxAndUpdate(issueIdentity: IssueIdentity) = {

        val broadcastTx = broadcastTxSync.Service.get(issueIdentity.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionIssueIdentities.Service.markFailedWithLog(txHashes = Seq(issueIdentity.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionIssueIdentities.Service.markFailedWithLog(txHashes = Seq(issueIdentity.txHash), log = successResponse.get.result.log)
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
        issueIdentity <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(issueIdentity)
      } yield issueIdentity
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = IssueIdentityTransactions.module

      def runner(): Unit = {
        val issueIdentityTxs = Service.getAllPendingStatus

        def checkAndUpdate(issueIdentityTxs: Seq[IssueIdentityTransaction]) = utilitiesOperations.traverse(issueIdentityTxs.map(_.txHash).distinct) { txHash =>
          val transaction = blockchainTransactionIssueIdentities.Service.tryGet(txHash)

          def onTxComplete(transaction: IssueIdentity) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(txHash)
              val updateMasterAccount = utilitiesOperations.traverse(issueIdentityTxs.filter(_.txHash == txHash))(x => masterAccounts.Service.updateIdentityID(accountId = x.accountId, identityID = x.getIdentityID))

              def sendNotifications(accountIds: Seq[String]): Unit = accountIds.foreach(x => utilitiesNotification.send(x, constants.Notification.IDENTITY_ISSUED)(""))

              (for {
                _ <- markSuccess
                _ <- updateMasterAccount
              } yield sendNotifications(issueIdentityTxs.filter(_.txHash == txHash).map(_.accountId))
                ).recover {
                case _: Exception => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(txHash)

              (for {
                _ <- markMasterFailed
              } yield ()
                ).recover {
                case _: Exception => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()

        }

        val forComplete = (for {
          issueIdentityTxs <- issueIdentityTxs
          _ <- checkAndUpdate(issueIdentityTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}