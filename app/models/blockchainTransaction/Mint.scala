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

case class Mint(fromAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, fromId: String, toId: String, classificationId: String, assetId: String, nftId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): Mints.MintSerialized = Mints.MintSerialized(
    fromAccountId = this.fromAccountId,
    txHash = this.txHash,
    txRawBytes = this.txRawBytes,
    fromAddress = this.fromAddress,
    fromId = this.fromId,
    toId = this.toId,
    classificationId = this.classificationId,
    assetId = this.assetId,
    nftId = this.nftId,
    broadcasted = this.broadcasted,
    status = this.status,
    memo = this.memo,
    log = this.log,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object Mints {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_MINT

  case class MintSerialized(fromAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, fromId: String, toId: String, classificationId: String, assetId: String, nftId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity2[String, String] {
    def deserialize: Mint = Mint(fromAccountId = this.fromAccountId, txHash = txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, fromId = this.fromId, toId = this.toId, classificationId = this.classificationId, assetId = this.assetId, nftId = this.nftId, broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = fromAccountId

    def id2: String = txHash
  }

  class MintTable(tag: Tag) extends Table[MintSerialized](tag, "Mint") with ModelTable2[String, String] {

    def * = (fromAccountId, txHash, txRawBytes, fromAddress, fromId, toId, classificationId, assetId, nftId, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MintSerialized.tupled, MintSerialized.unapply)

    def fromAccountId = column[String]("fromAccountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def fromId = column[String]("fromId")

    def toId = column[String]("toId")

    def classificationId = column[String]("classificationId")

    def assetId = column[String]("assetId")

    def nftId = column[String]("nftId")

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

  val TableQuery = new TableQuery(tag => new MintTable(tag))

}

@Singleton
class Mints @Inject()(
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
  extends GenericDaoImpl2[Mints.MintTable, Mints.MintSerialized, String, String](
    databaseConfigProvider,
    Mints.TableQuery,
    executionContext,
    Mints.module,
    Mints.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(fromAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, fromId: String, toId: String, classificationId: String, assetId: String, nftId: String, memo: Option[String]): Future[Mint] = {
      val mint = Mint(fromAccountId = fromAccountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, fromId = fromId, toId = toId, classificationId = classificationId, assetId = assetId, nftId = nftId, broadcasted = false, status = None, memo = memo, log = None)
      for {
        _ <- create(mint.serialize())
      } yield mint
    }

    def tryGet(fromAccountId: String, txHash: String): Future[Mint] = tryGetById1AndId2(id1 = fromAccountId, id2 = txHash).map(_.deserialize)

    def update(mint: Mint): Future[Mint] = {
      for {
        _ <- updateById1AndId2(mint.serialize())
      } yield mint
    }

    def getAllPendingStatus: Future[Seq[Mint]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

  }

  object Utility {

    def transaction(fromAccountId: String, fromAddress: String, fromId: String, toId: String, classificationId: String, assetId: String, nftId: String, gasPrice: Double, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val memo = s"MINT/$nftId/${utilities.IdGenerator.getRandomHexadecimal}"


      def checkMempoolAndAddTx(bcAccount: blockchain.Account, unconfirmedTxHashes: Seq[String]): Future[Mint] = {
        val txRawBytes = utilities.BlockchainTransaction.getTxRawBytes(
          messages = Seq(
            utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = fromAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = MicroNumber.zero))),
            //            utilities.BlockchainTransaction.getMintMsgAsAny(fromAddress = fromAddress, fromId = fromId, toId = toId, classificationId = classificationId, nftProperties = "")),
          ),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          memo = memo)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        for {
          mint <- if (!unconfirmedTxHashes.contains(txHash)) Service.add(fromAccountId = fromAccountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, fromId = fromId, toId = toId, classificationId = classificationId, assetId = assetId, nftId = nftId, memo = Option(memo)) else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        } yield mint
      }

      def broadcastTxAndUpdate(mint: Mint) = {
        val broadcastTx = broadcastTxSync.Service.get(mint.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = {
          val updatedMint = if (errorResponse.isDefined || (successResponse.isDefined && successResponse.get.result.code != 0)) {
            mint.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.fold(successResponse.get.result.log)(_.error.data)))
          } else mint.copy(broadcasted = true)
          Service.update(updatedMint)
        }

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedMint <- update(successResponse, errorResponse)
        } yield updatedMint
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        mint <- checkMempoolAndAddTx(bcAccount, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedMint <- broadcastTxAndUpdate(mint)
      } yield updatedMint
    }

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val mints = Service.getAllPendingStatus

        def checkAndUpdate(mints: Seq[Mint]) = utilitiesOperations.traverse(mints) { mint =>
          val transaction = blockchainTransactions.Service.get(mint.txHash)

          def update(transaction: Option[blockchain.Transaction]) = transaction.fold[Future[Option[Mint]]](Future(None))(tx => Service.update(mint.copy(status = Option(tx.status), log = if (tx.rawLog != "") Option(tx.rawLog) else None)).map(Option(_)))

          def onTxComplete(transaction: Option[blockchain.Transaction], mint: Option[Mint]) = if (transaction.isDefined) {
            utilitiesTransactionComplete.onMint(transaction = transaction.get, nftId = mint.get.nftId, fromAccountId = mint.get.fromAccountId)
          } else Future()

          for {
            transaction <- transaction
            mint <- update(transaction)
            _ <- onTxComplete(transaction, mint)
          } yield ()

        }

        val forComplete = (for {
          mints <- mints
          _ <- checkAndUpdate(mints)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.CommonConfig.Scheduler.InitialDelay, delay = constants.CommonConfig.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}