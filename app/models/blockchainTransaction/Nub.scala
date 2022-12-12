package models.blockchainTransaction

import exceptions.BaseException
import models.Trait._
import models.common.Coin
import models.{blockchain, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class Nub(fromAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): Nubs.NubSerialized = Nubs.NubSerialized(
    fromAccountId = this.fromAccountId,
    txHash = this.txHash,
    txRawBytes = this.txRawBytes,
    fromAddress = this.fromAddress,
    broadcasted = this.broadcasted,
    status = this.status,
    memo = this.memo,
    log = this.log,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object Nubs {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_NUB

  case class NubSerialized(fromAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity2[String, String] {
    def deserialize: Nub = Nub(fromAccountId = this.fromAccountId, txHash = txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = fromAccountId

    def id2: String = txHash
  }

  class NubTable(tag: Tag) extends Table[NubSerialized](tag, "Nub") with ModelTable2[String, String] {

    def * = (fromAccountId, txHash, txRawBytes, fromAddress, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NubSerialized.tupled, NubSerialized.unapply)

    def fromAccountId = column[String]("fromAccountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = fromAccountId

    def id2 = txHash

  }

  val TableQuery = new TableQuery(tag => new NubTable(tag))

}

@Singleton
class Nubs @Inject()(
                      protected val databaseConfigProvider: DatabaseConfigProvider,
                      blockchainAccounts: blockchain.Accounts,
                      blockchainTransactions: blockchain.Transactions,
                      masterNFTOwners: master.NFTOwners,
                      masterNFTs: master.NFTs,
                      broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                      utilitiesOperations: utilities.Operations,
                      getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                      getAccount: queries.blockchain.GetAccount,
                      utilitiesTransactionComplete: utilities.TransactionComplete,
                      transactionQuery: queries.blockchain.GetTransaction,
                    )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[Nubs.NubTable, Nubs.NubSerialized, String, String](
    databaseConfigProvider,
    Nubs.TableQuery,
    executionContext,
    Nubs.module,
    Nubs.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(fromAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, memo: Option[String]): Future[Nub] = {
      val nub = Nub(fromAccountId = fromAccountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, broadcasted = false, status = None, memo = memo, log = None)
      for {
        _ <- create(nub.serialize())
      } yield nub
    }

    def tryGet(fromAccountId: String, txHash: String): Future[Nub] = tryGetById1AndId2(id1 = fromAccountId, id2 = txHash).map(_.deserialize)

    def update(nub: Nub): Future[Nub] = {
      for {
        _ <- updateById1AndId2(nub.serialize())
      } yield nub
    }

    def getAllPendingStatus: Future[Seq[Nub]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

  }

  object Utility {

    def transaction(fromAccountId: String, fromAddress: String, gasPrice: Double, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val memo = s"NUB/${utilities.IdGenerator.getRandomHexadecimal}"


      def checkMempoolAndAddTx(bcAccount: blockchain.Account, unconfirmedTxHashes: Seq[String]): Future[Nub] = {
        val txRawBytes = utilities.BlockchainTransaction.getTxRawBytes(
          messages = Seq(
            utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = fromAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = MicroNumber.zero))),
            //            utilities.BlockchainTransaction.getNubMsgAsAny(fromAddress = fromAddress, fromId = fromId, toId = toId, classificationId = classificationId, nftProperties = "")),
          ),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          memo = memo)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        for {
          nub <- if (!unconfirmedTxHashes.contains(txHash)) Service.add(fromAccountId = fromAccountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, memo = Option(memo)) else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        } yield nub
      }

      def broadcastTxAndUpdate(nub: Nub) = {
        val broadcastTx = broadcastTxSync.Service.get(nub.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = {
          val updatedNub = if (errorResponse.isDefined || (successResponse.isDefined && successResponse.get.result.code != 0)) {
            nub.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.fold(successResponse.get.result.log)(_.error.data)))
          } else nub.copy(broadcasted = true)
          Service.update(updatedNub)
        }

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedNub <- update(successResponse, errorResponse)
        } yield updatedNub
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        nub <- checkMempoolAndAddTx(bcAccount, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedNub <- broadcastTxAndUpdate(nub)
      } yield updatedNub
    }

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val nubs = Service.getAllPendingStatus

        def checkAndUpdate(nubs: Seq[Nub]) = utilitiesOperations.traverse(nubs) { nub =>
          val transaction = blockchainTransactions.Service.get(nub.txHash)

          def update(transaction: Option[blockchain.Transaction]) = transaction.fold[Future[Option[Nub]]](Future(None))(tx => Service.update(nub.copy(status = Option(tx.status), log = if (tx.rawLog != "") Option(tx.rawLog) else None)).map(Option(_)))

          def onTxComplete(transaction: Option[blockchain.Transaction], nub: Option[Nub]) = if (transaction.isDefined) {
            utilitiesTransactionComplete.onNub(transaction = transaction.get, fromAccountId = nub.get.fromAccountId)
          } else Future()

          for {
            transaction <- transaction
            nub <- update(transaction)
            _ <- onTxComplete(transaction, nub)
          } yield ()

        }

        val forComplete = (for {
          nubs <- nubs
          _ <- checkAndUpdate(nubs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.Scheduler.InitialDelay, delay = constants.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}