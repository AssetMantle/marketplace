package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchainTransaction.{AdminTransaction, AdminTransactions}
import models.master.Key
import models.masterTransaction.IssueIdentityTransactions.IssueIdentityTransactionTable
import models.traits._
import models.{blockchain, master}
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
                                           utilitiesTransaction: utilities.Transaction,
                                           utilitiesOperations: utilities.Operations,
                                           utilitiesNotification: utilities.Notification,
                                           adminTransactions: AdminTransactions,
                                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[IssueIdentityTransactions.IssueIdentityTransactionTable, IssueIdentityTransaction, String, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_ISSUE_IDENTITY_TRANSACTION

  val tableQuery = new TableQuery(tag => new IssueIdentityTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, accountIds: Seq[String]): Future[String] = create(accountIds.map(x => IssueIdentityTransaction(txHash = txHash, accountId = x, status = None))).map(_.toString)

    def getByTxHash(txHash: String): Future[Seq[IssueIdentityTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[IssueIdentityTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("ISSUE_IDENTITY", 100000)

    def transaction(accountIdAddress: Map[String, Seq[String]]): Future[BlockchainTransaction] = {
      val messages = accountIdAddress.keys.map(x => utilities.BlockchainTransaction.getIssueIdentityMsgWithAuthentication(
        fromAddress = constants.Secret.issueIdentityWallet.address,
        classificationID = constants.Transaction.IdentityClassificationID,
        fromID = constants.Transaction.MantlePlaceIdentityID,
        immutableMetas = utilities.Identity.getImmutableMetas(x),
        mutableMetas = utilities.Identity.getMutableMetas(accountIdAddress.getOrElse(x, Seq())),
        immutableMesas = utilities.Identity.getImmutableMesas,
        mutableMesas = utilities.Identity.getMutableMesas,
      )).toSeq

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, accountIds = accountIdAddress.keys.toSeq)

      val adminTx = utilitiesTransaction.doAdminTx(messages = messages, wallet = constants.Secret.issueIdentityWallet, masterTxFunction = masterTxFunc)

      for {
        (adminTx, _) <- adminTx
      } yield adminTx

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
        val tx = transaction(accountIdAddress = ids.map(x => x -> keys.filter(_.accountId == x).map(_.address)).toMap)

        def updateMasterKeys() = masterKeys.Service.markIdentityIssuePending(keys.map(_.accountId).distinct)

        for {
          tx <- tx
          _ <- updateMasterKeys()
        } yield tx.txHash
      } else Future("")

      for {
        accountIds <- accountIds
        issueIdentities <- filterAlreadyIssuedIdentities(accountIds)
        anyPendingTx <- anyPendingTx
        keys <- getKeys(anyPendingTx, issueIdentities)
        txHash <- doTx(issueIdentities, keys)
      } yield if (txHash != "") logger.info("ISSUE_IDENTITY: " + txHash + " ( " + keys.map(x => x.accountId -> x.address).toMap.toString() + " )") else ()
    }

    private def checkTransactions() = {
      val issueIdentityTxs = Service.getAllPendingStatus

      def checkAndUpdate(issueIdentityTxs: Seq[IssueIdentityTransaction]) = utilitiesOperations.traverse(issueIdentityTxs.map(_.txHash).distinct) { txHash =>
        val transaction = adminTransactions.Service.tryGet(txHash)

        def onTxComplete(adminTransaction: AdminTransaction) = if (adminTransaction.status.isDefined) {
          if (adminTransaction.status.get) {
            val markSuccess = Service.markSuccess(txHash)
            val updateMasterKeys = masterKeys.Service.markIdentityIssued(issueIdentityTxs.filter(_.txHash == txHash).map(_.accountId))

            for {
              _ <- markSuccess
              _ <- updateMasterKeys
            } yield ()
          } else {
            val markMasterFailed = Service.markFailed(txHash)
            val updateMasterKeys = masterKeys.Service.markIdentityFailed(issueIdentityTxs.filter(_.txHash == txHash).map(_.accountId))

            for {
              _ <- markMasterFailed
              _ <- updateMasterKeys
            } yield ()
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
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}