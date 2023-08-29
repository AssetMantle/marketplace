package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.{AdminTransaction, AdminTransactions}
import models.master.Key
import models.masterTransaction.IssueIdentityTransactions.IssueIdentityTransactionTable
import models.traits._
import models.{blockchain, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class IssueIdentityTransaction(txHash: String, accountId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = accountId

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.accountId)
}

private[masterTransaction] object IssueIdentityTransactions {

  class IssueIdentityTransactionTable(tag: Tag) extends Table[IssueIdentityTransaction](tag, "IssueIdentityTransaction") with ModelTable2[String, String] {

    def * = (txHash, accountId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (IssueIdentityTransaction.tupled, IssueIdentityTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = accountId

  }

}

@Singleton
class IssueIdentityTransactions @Inject()(
                                           protected val dbConfigProvider: DatabaseConfigProvider,
                                           blockchainIdentities: blockchain.Identities,
                                           masterAccounts: master.Accounts,
                                           masterKeys: master.Keys,
                                           utilitiesOperations: utilities.Operations,
                                           utilitiesNotification: utilities.Notification,
                                           adminTransactions: AdminTransactions,
                                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[IssueIdentityTransactions.IssueIdentityTransactionTable, IssueIdentityTransaction, String, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_ISSUE_IDENTITY_TRANSACTION

  val tableQuery = new TableQuery(tag => new IssueIdentityTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, accountIds: Seq[String]): Future[Int] = create(accountIds.map(x => IssueIdentityTransaction(txHash = txHash, accountId = x, status = None)))

    def getByTxHash(txHash: String): Future[Seq[IssueIdentityTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[IssueIdentityTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    def transaction(accountIdAddress: Map[String, Seq[String]], ecKey: ECKey): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = adminTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(constants.Secret.issueIdentityWallet.address)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val (txRawBytes, _) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = accountIdAddress.keys.map(x => utilities.BlockchainTransaction.getMantlePlaceIssueIdentityMsgWithAuthentication(
            id = x,
            fromAddress = constants.Secret.issueIdentityWallet.address,
            toAddress = accountIdAddress.getOrElse(x, Seq()).headOption.getOrElse(""),
            classificationID = constants.Transaction.IdentityClassificationID,
            fromID = constants.Transaction.FromID,
            addresses = accountIdAddress.getOrElse(x, Seq())
          )).toSeq,
          fee = utilities.BlockchainTransaction.getFee(gasPrice = constants.Transaction.AdminTxGasPrice, gasLimit = constants.Transaction.DefaultIssueIdentityGasLimit * accountIdAddress.size),
          gasLimit = constants.Transaction.DefaultIssueIdentityGasLimit * accountIdAddress.size,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              adminTransaction <- adminTransactions.Service.addWithNoneStatus(txHash = txHash, fromAddress = constants.Secret.issueIdentityWallet.address, timeoutHeight = timeoutHeight, txType = constants.Transaction.Admin.ISSUE_IDENTITY)
              _ <- Service.addWithNoneStatus(txHash = txHash, accountIds = accountIdAddress.keys.toSeq)
            } yield adminTransaction
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          adminTransaction <- checkAndAdd
        } yield (adminTransaction, txRawBytes)
      }

      def broadcastTxAndUpdate(adminTransaction: AdminTransaction, txRawBytes: Array[Byte]) = adminTransactions.Utility.broadcastTxAndUpdate(adminTransaction, txRawBytes)

      for {
        (latestHeight, bcAccount, unconfirmedTxs) <- latestHeightAccountUnconfirmedTxs
        (adminTransaction, txRawBytes) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedAdminTransaction <- broadcastTxAndUpdate(adminTransaction, txRawBytes)
      } yield updatedAdminTransaction
    }

    private def issueIdentities(): Future[Unit] = {
      val accountIds = masterKeys.Service.getNotIssuedIdentityAccountIDs
      val anyPendingTx = Service.checkAnyPendingTx

      def filterAlreadyIssuedIdentities(accountIDs: Seq[String]) = {
        val identityIDs = accountIDs.map(x => utilities.Identity.getMantlePlaceIdentityID(x))
        val existingIdentityIDsString = blockchainIdentities.Service.getIDsAlreadyExists(identityIDs.map(_.asString))

        def updateMasterKeys(issuedAccountIDs: Seq[String]) = if (issuedAccountIDs.nonEmpty) masterKeys.Service.markIdentityIssued(issuedAccountIDs) else Future(0)

        for {
          existingIdentityIDsString <- existingIdentityIDsString
          _ <- updateMasterKeys(accountIDs.filter(x => existingIdentityIDsString.contains(utilities.Identity.getMantlePlaceIdentityID(x).asString)))
        } yield accountIDs.filterNot(x => existingIdentityIDsString.contains(utilities.Identity.getMantlePlaceIdentityID(x).asString))
      }

      def getKeys(anyPendingTx: Boolean, ids: Seq[String]) = if (!anyPendingTx && ids.nonEmpty) masterKeys.Service.getAllKeys(ids) else Future(Seq())

      def doTx(ids: Seq[String], keys: Seq[Key]) = if (keys.nonEmpty) {
        val tx = transaction(accountIdAddress = ids.map(x => x -> keys.filter(_.accountId == x).map(_.address)).toMap, ecKey = constants.Secret.issueIdentityWallet.getECKey)

        def updateMasterKeys() = masterKeys.Service.markIdentityIssuePending(keys.map(_.accountId).distinct)

        for {
          tx <- tx
          _ <- updateMasterKeys()
        } yield tx.txHash
      } else Future("")

      (for {
        accountIds <- accountIds
        issueIdentities <- filterAlreadyIssuedIdentities(accountIds)
        anyPendingTx <- anyPendingTx
        keys <- getKeys(anyPendingTx, issueIdentities)
        txHash <- doTx(issueIdentities, keys)
      } yield if (txHash != "") logger.info("ISSUE_IDENTITY: " + txHash + " ( " + keys.map(x => x.accountId -> x.address).toMap.toString() + " )")
        ).recover {
        case _: BaseException => logger.error("UNABLE_TO_ISSUE_IDENTITIES")
      }
    }

    private def checkTransactions() = {
      val issueIdentityTxs = Service.getAllPendingStatus

      def checkAndUpdate(issueIdentityTxs: Seq[IssueIdentityTransaction]) = utilitiesOperations.traverse(issueIdentityTxs.map(_.txHash).distinct) { txHash =>
        val transaction = adminTransactions.Service.tryGet(txHash)

        def onTxComplete(adminTransaction: AdminTransaction) = if (adminTransaction.status.isDefined) {
          if (adminTransaction.status.get) {
            val markSuccess = Service.markSuccess(txHash)
            val updateMasterKeys = masterKeys.Service.markIdentityIssued(issueIdentityTxs.filter(_.txHash == txHash).map(_.accountId))

            (for {
              _ <- markSuccess
              _ <- updateMasterKeys
            } yield ()
              ).recover {
              case exception: Exception => logger.error(exception.getLocalizedMessage)
                logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markMasterFailed = Service.markFailed(txHash)
            val updateMasterKeys = masterKeys.Service.markIdentityFailed(issueIdentityTxs.filter(_.txHash == txHash).map(_.accountId))

            (for {
              _ <- markMasterFailed
              _ <- updateMasterKeys
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

      for {
        issueIdentityTxs <- issueIdentityTxs
        _ <- checkAndUpdate(issueIdentityTxs)
      } yield ()

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.FiveMinutes

      def runner(): Unit = {

        val forComplete = (for {
          _ <- issueIdentities()
          _ <- checkTransactions()
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}