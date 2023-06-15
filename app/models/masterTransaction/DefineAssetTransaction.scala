package models.masterTransaction

import constants.Scheduler
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

case class DefineAssetTransaction(txHash: String, collectionId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

private[masterTransaction] object DefineAssetTransactions {

  class DefineAssetTransactionTable(tag: Tag) extends Table[DefineAssetTransaction](tag, "DefineAssetTransaction") with ModelTable[String] {

    def * = (txHash, collectionId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (DefineAssetTransaction.tupled, DefineAssetTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class DefineAssetTransactions @Inject()(
                                         protected val dbConfigProvider: DatabaseConfigProvider,
                                         blockchainClassifications: blockchain.Classifications,
                                         utilitiesOperations: utilities.Operations,
                                         utilitiesNotification: utilities.Notification,
                                         adminTransactions: AdminTransactions,
                                         masterCollections: master.Collections,
                                       )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[DefineAssetTransactions.DefineAssetTransactionTable, DefineAssetTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_DEFINE_ASSET_TRANSACTION

  val tableQuery = new TableQuery(tag => new DefineAssetTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, collectionIds: Seq[String]): Future[Int] = create(collectionIds.map(x => DefineAssetTransaction(txHash = txHash, collectionId = x, status = None)))

    def getByTxHash(txHash: String): Future[Seq[DefineAssetTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[DefineAssetTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    private def transaction(collections: Seq[Collection]): Future[AdminTransaction] = {
      val latestHeightAccountUnconfirmedTxs = adminTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(constants.Wallet.DefineAssetWallet.address)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = collections.map(x => utilities.BlockchainTransaction.getDefineAssetMsg(
            fromAddress = constants.Wallet.DefineAssetWallet.address,
            fromID = constants.Transaction.FromID,
            immutableMetas = x.getImmutableMetaProperties,
            immutables = x.getImmutableProperties,
            mutableMetas = x.getMutableMetaProperties,
            mutables = x.getMutableProperties)
          ),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = 0.0001, gasLimit = constants.Transaction.DefaultDefineAssetGasLimit * collections.size),
          gasLimit = constants.Transaction.DefaultDefineAssetGasLimit * collections.size,
          account = bcAccount,
          ecKey = constants.Wallet.DefineAssetWallet.getECKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              adminTransaction <- adminTransactions.Service.addWithNoneStatus(txHash = txHash, fromAddress = constants.Wallet.DefineAssetWallet.address, memo = Option(memo), timeoutHeight = timeoutHeight, txType = constants.Transaction.Admin.DEFINE_ASSET)
              _ <- Service.addWithNoneStatus(txHash = txHash, collectionIds = collections.map(_.id))
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

      (for {
        collections <- collections
        defineCollections <- filterAlreadyDefined(collections)
        anyPendingTx <- anyPendingTx
        txHash <- doTx(anyPendingTx, defineCollections)
      } yield if (txHash != "") logger.info("DEFINE_ASSET: " + txHash + " ( " + defineCollections.map(x => x.id -> x.getClassificationID.asString).toMap.toString() + " )")
        ).recover {
        case _: BaseException => logger.error("UNABLE_TO_DEFINE_ASSETS")
      }
    }

    private def checkTransactions(): Future[Unit] = {
      val defineAssetTxs = Service.getAllPendingStatus

      def checkAndUpdate(defineAssetTxs: Seq[DefineAssetTransaction]) = utilitiesOperations.traverse(defineAssetTxs.map(_.txHash).distinct) { txHash =>
        val transaction = adminTransactions.Service.tryGet(txHash)

        def onTxComplete(adminTransaction: AdminTransaction) = if (adminTransaction.status.isDefined) {
          if (adminTransaction.status.get) {
            val markSuccess = Service.markSuccess(txHash)
            val markDefined = masterCollections.Service.markAsDefined(defineAssetTxs.filter(_.txHash == adminTransaction.txHash).map(_.collectionId))

            (for {
              _ <- markSuccess
              _ <- markDefined
            } yield ()
              ).recover {
              case exception: Exception => logger.error(exception.getLocalizedMessage)
                logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markFailed = Service.markFailed(txHash)
            val markUndefined = masterCollections.Service.markAsNotDefined(defineAssetTxs.filter(_.txHash == adminTransaction.txHash).map(_.collectionId))

            (for {
              _ <- markFailed
              _ <- markUndefined
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

      (for {
        defineAssetTxs <- defineAssetTxs
        _ <- checkAndUpdate(defineAssetTxs)
      } yield ()).recover {
        case baseException: BaseException => logger.error(baseException.failure.logMessage)
      }
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {

        val forComplete = (for {
          _ <- defineCollections()
          _ <- checkTransactions()
        } yield ())

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}