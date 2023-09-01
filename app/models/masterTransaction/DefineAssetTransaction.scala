package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchainTransaction.{AdminTransaction, AdminTransactions}
import models.master.Collection
import models.masterTransaction.DefineAssetTransactions.DefineAssetTransactionTable
import models.traits._
import models.{blockchain, master}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class DefineAssetTransaction(txHash: String, collectionId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = collectionId

}

private[masterTransaction] object DefineAssetTransactions {

  class DefineAssetTransactionTable(tag: Tag) extends Table[DefineAssetTransaction](tag, "DefineAssetTransaction") with ModelTable2[String, String] {

    def * = (txHash, collectionId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (DefineAssetTransaction.tupled, DefineAssetTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def collectionId = column[String]("collectionId", O.PrimaryKey)

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = collectionId

  }

}

@Singleton
class DefineAssetTransactions @Inject()(
                                         protected val dbConfigProvider: DatabaseConfigProvider,
                                         blockchainClassifications: blockchain.Classifications,
                                         utilitiesOperations: utilities.Operations,
                                         utilitiesNotification: utilities.Notification,
                                         utilitiesTransaction: utilities.Transaction,
                                         adminTransactions: AdminTransactions,
                                         masterCollections: master.Collections,
                                       )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[DefineAssetTransactions.DefineAssetTransactionTable, DefineAssetTransaction, String, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_DEFINE_ASSET_TRANSACTION

  val tableQuery = new TableQuery(tag => new DefineAssetTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, collectionIds: Seq[String]): Future[String] = create(collectionIds.map(x => DefineAssetTransaction(txHash = txHash, collectionId = x, status = None))).map(_.toString)

    def getByTxHash(txHash: String): Future[Seq[DefineAssetTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[DefineAssetTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {
    implicit val txUtil: TxUtil = TxUtil("DEFINE_ASSET", 150000)

    private def transaction(collections: Seq[Collection]): Future[AdminTransaction] = {
      val messages = collections.map(x => utilities.BlockchainTransaction.getDefineAssetMsg(
        fromAddress = constants.Secret.defineAssetWallet.address,
        fromID = constants.Transaction.MantlePlaceIdentityID,
        immutableMetas = x.getImmutableMetaProperties,
        immutables = x.getImmutableProperties,
        mutableMetas = x.getMutableMetaProperties,
        mutables = x.getMutableProperties)
      )

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, collectionIds = collections.map(_.id))

      val adminTx = utilitiesTransaction.doAdminTx(messages = messages, wallet = constants.Secret.defineAssetWallet, masterTxFunction = masterTxFunc)

      for {
        (adminTx, _) <- adminTx
      } yield adminTx
    }

    private def defineCollections(): Future[Unit] = {
      val collections = masterCollections.Service.fetchAllForDefiningAsset()
      val anyPendingTx = Service.checkAnyPendingTx

      def filterAlreadyDefined(collections: Seq[Collection]) = {
        val classificationIDs = collections.map(_.getClassificationID)
        val existingClassificationIDsString = blockchainClassifications.Service.getIDsAlreadyExists(classificationIDs.map(_.asString))

        def updateMasterKeys(collectionIds: Seq[String]) = if (collectionIds.nonEmpty) masterCollections.Service.markAsDefined(collectionIds) else Future(0)

        for {
          existingClassificationIDsString <- existingClassificationIDsString
          _ <- updateMasterKeys(collections.filter(x => existingClassificationIDsString.contains(x.getClassificationID.asString)).map(_.id))
        } yield collections.filterNot(x => existingClassificationIDsString.contains(x.getClassificationID.asString))
      }

      def doTx(anyPendingTx: Boolean, collections: Seq[Collection]) = if (!anyPendingTx && collections.nonEmpty) {
        val tx = transaction(collections)

        def updateMasterKeys() = masterCollections.Service.markAsDefinePending(collections.map(_.id))

        for {
          tx <- tx
          _ <- updateMasterKeys()
        } yield tx.txHash
      } else Future("")

      for {
        collections <- collections
        defineCollections <- filterAlreadyDefined(collections)
        anyPendingTx <- anyPendingTx
        txHash <- doTx(anyPendingTx, defineCollections)
      } yield if (txHash != "") logger.info("DEFINE_ASSET: " + txHash + " ( " + defineCollections.map(x => x.id -> x.getClassificationID.asString).toMap.toString() + " )") else ()

    }

    private def checkTransactions(): Future[Unit] = {
      val defineAssetTxs = Service.getAllPendingStatus

      def checkAndUpdate(defineAssetTxs: Seq[DefineAssetTransaction]) = utilitiesOperations.traverse(defineAssetTxs.map(_.txHash).distinct) { txHash =>
        val transaction = adminTransactions.Service.tryGet(txHash)

        def onTxComplete(adminTransaction: AdminTransaction) = if (adminTransaction.status.isDefined) {
          if (adminTransaction.status.get) {
            val markSuccess = Service.markSuccess(txHash)
            val markDefined = masterCollections.Service.markAsDefined(defineAssetTxs.filter(_.txHash == adminTransaction.txHash).map(_.collectionId))

            for {
              _ <- markSuccess
              _ <- markDefined
            } yield ()
          } else {
            val markFailed = Service.markFailed(txHash)
            val markUndefined = masterCollections.Service.markAsNotDefined(defineAssetTxs.filter(_.txHash == adminTransaction.txHash).map(_.collectionId))

            for {
              _ <- markFailed
              _ <- markUndefined
            } yield ()
          }
        } else Future()

        for {
          transaction <- transaction
          _ <- onTxComplete(transaction)
        } yield ()
      }

      for {
        defineAssetTxs <- defineAssetTxs
        _ <- checkAndUpdate(defineAssetTxs)
      } yield ()
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {

        val forComplete = (for {
          _ <- defineCollections()
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