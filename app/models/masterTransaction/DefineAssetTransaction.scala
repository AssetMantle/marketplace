package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.Trait._
import models.blockchainTransaction.DefineAsset
import models.{blockchain, blockchainTransaction}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

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
                                         blockchainAccounts: blockchain.Accounts,
                                         blockchainIdentities: blockchain.Identities,
                                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                         utilitiesOperations: utilities.Operations,
                                         getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                         getAccount: queries.blockchain.GetAccount,
                                         getAbciInfo: queries.blockchain.GetABCIInfo,
                                         utilitiesNotification: utilities.Notification,
                                         blockchainTransactionDefineAssets: blockchainTransaction.DefineAssets,
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
    val scheduler: Scheduler = new Scheduler {
      val name: String = DefineAssetTransactions.module

      def runner(): Unit = {
        val defineAssetTxs = Service.getAllPendingStatus

        def checkAndUpdate(defineAssetTxs: Seq[DefineAssetTransaction]) = utilitiesOperations.traverse(defineAssetTxs.map(_.txHash).distinct) { txHash =>
          val transaction = blockchainTransactionDefineAssets.Service.tryGet(txHash)

          def onTxComplete(transaction: DefineAsset) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(txHash)

              (for {
                _ <- markSuccess
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(txHash)

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
          defineAssetTxs <- defineAssetTxs
          _ <- checkAndUpdate(defineAssetTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}