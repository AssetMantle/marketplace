package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.DefineAsset
import models.master.Collection
import models.traits._
import models.{blockchain, blockchainTransaction, master}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class DefineAssetTransaction(txHash: String, collectionId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

object DefineAssetTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_DEFINE_ASSET_TRANSACTION

  class DefineAssetTransactionTable(tag: Tag) extends Table[DefineAssetTransaction](tag, "DefineAssetTransaction") with ModelTable[String] {

    def * = (txHash, collectionId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (DefineAssetTransaction.tupled, DefineAssetTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def collectionId = column[String]("collectionId", O.PrimaryKey)

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new DefineAssetTransactionTable(tag))

}

@Singleton
class DefineAssetTransactions @Inject()(
                                         protected val databaseConfigProvider: DatabaseConfigProvider,
                                         blockchainClassifications: blockchain.Classifications,
                                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                         utilitiesOperations: utilities.Operations,
                                         getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                         getAccount: queries.blockchain.GetAccount,
                                         getAbciInfo: queries.blockchain.GetABCIInfo,
                                         utilitiesNotification: utilities.Notification,
                                         blockchainTransactionDefineAssets: blockchainTransaction.DefineAssets,
                                         masterCollections: master.Collections,
                                       )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[DefineAssetTransactions.DefineAssetTransactionTable, DefineAssetTransaction, String](
    databaseConfigProvider,
    DefineAssetTransactions.TableQuery,
    executionContext,
    DefineAssetTransactions.module,
    DefineAssetTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, collectionIds: Seq[String]): Future[Unit] = create(collectionIds.map(x => DefineAssetTransaction(txHash = txHash, collectionId = x, status = None)))

    def getByTxHash(txHash: String): Future[Seq[DefineAssetTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(DefineAssetTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(DefineAssetTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[DefineAssetTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)

    def getWithStatusTrueOrPending(collectionIds: Seq[String]): Future[Seq[String]] = filter(x => x.collectionId.inSet(collectionIds) && (x.status || x.status.?.isEmpty)).map(_.map(_.collectionId))
  }

  object Utility {

    private def transaction(collections: Seq[Collection]): Future[DefineAsset] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(constants.Wallet.DefineAssetWallet.address).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = collections.map(x => utilities.BlockchainTransaction.getDefineAssetMsg(
            fromAddress = constants.Wallet.DefineAssetWallet.address,
            fromID = constants.Transaction.FromID,
            immutableMetas = x.getImmutableMetaProperties,
            immutables = x.getImmutableProperties,
            mutableMetas = x.getMutableMetaProperties,
            mutables = x.getMutableProperties)
          ),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = 0.0001, gasLimit = constants.Blockchain.DefaultDefineAssetGasLimit * collections.size),
          gasLimit = constants.Blockchain.DefaultDefineAssetGasLimit * collections.size,
          account = bcAccount,
          ecKey = constants.Wallet.DefineAssetWallet.getECKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              defineAsset <- blockchainTransactionDefineAssets.Service.add(txHash = txHash, fromAddress = constants.Wallet.DefineAssetWallet.address, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, collectionIds = collections.map(_.id))
            } yield defineAsset
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          defineAsset <- checkAndAdd(unconfirmedTxHashes)
        } yield (defineAsset, txRawBytes)
      }

      def broadcastTxAndUpdate(defineAsset: DefineAsset, txRawBytes: Array[Byte]) = {

        val broadcastTx = broadcastTxSync.Service.get(defineAsset.getTxRawAsHexString(txRawBytes))

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionDefineAssets.Service.markFailedWithLog(txHashes = Seq(defineAsset.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionDefineAssets.Service.markFailedWithLog(txHashes = Seq(defineAsset.txHash), log = successResponse.get.result.log)
        else Future(0)

        for {
          (successResponse, errorResponse) <- broadcastTx
          _ <- update(successResponse, errorResponse)
        } yield ()
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        abciInfo <- abciInfo
        (defineAsset, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(defineAsset, txRawBytes)
      } yield defineAsset

    }

    private def defineCollections(): Future[Unit] = {
      val collections = masterCollections.Service.fetchAllDefineReady()
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
        val transaction = blockchainTransactionDefineAssets.Service.tryGet(txHash)

        def onTxComplete(transaction: DefineAsset) = if (transaction.status.isDefined) {
          if (transaction.status.get) {
            val markSuccess = Service.markSuccess(txHash)
            val markDefined = masterCollections.Service.markAsDefined(defineAssetTxs.filter(_.txHash == transaction.txHash).map(_.collectionId))

            (for {
              _ <- markSuccess
              _ <- markDefined
            } yield ()
              ).recover {
              case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markFailed = Service.markFailed(txHash)
            val markUndefined = masterCollections.Service.markAsNotDefined(defineAssetTxs.filter(_.txHash == transaction.txHash).map(_.collectionId))

            (for {
              _ <- markFailed
              _ <- markUndefined
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

      (for {
        defineAssetTxs <- defineAssetTxs
        _ <- checkAndUpdate(defineAssetTxs)
      } yield ()).recover {
        case baseException: BaseException => logger.error(baseException.failure.logMessage)
      }
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = DefineAssetTransactions.module

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