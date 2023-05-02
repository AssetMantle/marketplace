package models.masterTransaction

import com.assetmantle.modules.assets.{transactions => assetTransactions}
import com.cosmos.bank.{v1beta1 => bankTx}
import com.google.protobuf.{Any => protobufAny}
import com.ibc.core.channel.{v1 => channelTx}
import com.assetmantle.modules.splits.{transactions => splitTransactions}
import constants.Scheduler
import exceptions.BaseException
import models.blockchain.Transaction
import models.traits._
import models.{blockchain, blockchainTransaction}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class LatestBlock(height: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[Long] {

  def id: Long = height

}

object LatestBlocks {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_LATEST_BLOCK

  implicit val logger: Logger = Logger(this.getClass)

  class LatestBlockTable(tag: Tag) extends Table[LatestBlock](tag, "LatestBlock") with ModelTable[Long] {

    def * = (height, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (LatestBlock.tupled, LatestBlock.unapply)

    def height = column[Long]("height", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = height

  }

  lazy val TableQuery = new TableQuery(tag => new LatestBlockTable(tag))
}

@Singleton
class LatestBlocks @Inject()(
                              blockchainAssets: blockchain.Assets,
                              blockchainBlocks: blockchain.Blocks,
                              blockchainSplits: blockchain.Splits,
                              blockchainTransactions: blockchain.Transactions,
                              blockchainTransactionProvisionAddresses: blockchainTransaction.ProvisionAddresses,
                              blockchainTransactionUnprovisionAddresses: blockchainTransaction.UnprovisionAddresses,
                              blockchainTransactionCancelOrders: blockchainTransaction.CancelOrders,
                              blockchainTransactionMakeOrders: blockchainTransaction.MakeOrders,
                              blockchainTransactionTakeOrders: blockchainTransaction.TakeOrders,
                              utilitiesOperations: utilities.Operations,
                              utilitiesExternalTransactions: utilities.ExternalTransactions,
                              protected val databaseConfigProvider: DatabaseConfigProvider
                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[LatestBlocks.LatestBlockTable, LatestBlock, Long](
    databaseConfigProvider,
    LatestBlocks.TableQuery,
    executionContext,
    LatestBlocks.module,
    LatestBlocks.logger
  ) {

  object Service {

    def getLastChecked: Future[Long] = customQuery(LatestBlocks.TableQuery.sortBy(_.height.desc).result.headOption).map(_.fold(0L)(_.height))

    def update(latestHeight: Long): Future[Unit] = {
      for {
        _ <- create(LatestBlock(latestHeight))
        _ <- filterAndDelete(_.height < latestHeight)
      } yield ()
    }

  }

  object Utility {

    private def checkAndProcess(mantlePlaceTx: Boolean, transaction: Transaction, stdMsg: protobufAny, f1: (String) => Future[Boolean], f2: (protobufAny, Transaction) => Future[Unit]) = if (!mantlePlaceTx) {
      val txExists = f1(transaction.hash)
      for {
        txExists <- txExists
        _ <- f2(stdMsg, transaction)
      } yield txExists
    } else Future(mantlePlaceTx)

    private def processTransaction(transaction: Transaction) = if (transaction.status) {
      var mantlePlaceTx = false
      val check = utilitiesOperations.traverse(transaction.getMessages) { stdMsg =>
        stdMsg.getTypeUrl match {
          case schema.constants.Messages.SEND_COIN => utilitiesExternalTransactions.onSendCoin(bankTx.MsgSend.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.RECV_PACKET => utilitiesExternalTransactions.onIBCReceive(channelTx.MsgRecvPacket.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.ASSET_BURN => utilitiesExternalTransactions.onBurnNFT(assetTransactions.burn.Message.parseFrom(stdMsg.getValue), transaction.hash)
          case schema.constants.Messages.ASSET_MUTATE => utilitiesExternalTransactions.onMutateNFT(assetTransactions.mutate.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.ASSET_RENUMERATE => utilitiesExternalTransactions.onRenumerateNFT(assetTransactions.renumerate.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.IDENTITY_PROVISION =>
            for {
              txExists <- checkAndProcess(mantlePlaceTx, transaction, stdMsg, blockchainTransactionProvisionAddresses.Service.checkExists, utilitiesExternalTransactions.onProvisionIdentity)
            } yield mantlePlaceTx = txExists
          case schema.constants.Messages.IDENTITY_UNPROVISION =>
            for {
              txExists <- checkAndProcess(mantlePlaceTx, transaction, stdMsg, blockchainTransactionUnprovisionAddresses.Service.checkExists, utilitiesExternalTransactions.onUnprovisionIdentity)
            } yield mantlePlaceTx = txExists
          case schema.constants.Messages.ORDER_CANCEL =>
            for {
              txExists <- checkAndProcess(mantlePlaceTx, transaction, stdMsg, blockchainTransactionCancelOrders.Service.checkExists, utilitiesExternalTransactions.onOrderCancel)
            } yield mantlePlaceTx = txExists
          case schema.constants.Messages.ORDER_MAKE =>
            for {
              txExists <- checkAndProcess(mantlePlaceTx, transaction, stdMsg, blockchainTransactionMakeOrders.Service.checkExists, utilitiesExternalTransactions.onOrderMake)
            } yield mantlePlaceTx = txExists
          case schema.constants.Messages.ORDER_TAKE =>
            for {
              txExists <- checkAndProcess(mantlePlaceTx, transaction, stdMsg, blockchainTransactionTakeOrders.Service.checkExists, utilitiesExternalTransactions.onOrderTake)
            } yield mantlePlaceTx = txExists
          case schema.constants.Messages.SPLIT_SEND => utilitiesExternalTransactions.onSplitSend(splitTransactions.send.Message.parseFrom(stdMsg.getValue))
          //          case schema.constants.Messages.SPLIT_WRAP =>
          //          case schema.constants.Messages.SPLIT_UNWRAP => utilitiesExternalTransactions.onSplitUnwrap(splitTransactions.unwrap.Message.parseFrom(stdMsg.getValue))
          case _ => Future()
        }

      }
      for {
        _ <- check
      } yield ()
    } else Future()

    private def processBlock(height: Int): Unit = {
      val transactions = blockchainTransactions.Service.getByHeight(height)

      def processAll(transactions: Seq[Transaction]) = utilitiesOperations.traverse(transactions)(x => processTransaction(x))

      val forComplete = for {
        transactions <- transactions
        _ <- processAll(transactions)
      } yield ()

      Await.result(forComplete, Duration.Inf)

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = LatestBlocks.module

      def runner(): Unit = {
        val latestBlockchainHeight = blockchainBlocks.Service.getLatestHeight
        val lastChecked = Service.getLastChecked

        def processBlocks(latestBlockchainHeight: Int, lastCheckedHeight: Long): Unit = if (latestBlockchainHeight > lastCheckedHeight) {
          (lastCheckedHeight + 1).to(latestBlockchainHeight).foreach(x => processBlock(x.toInt))
        }

        val forComplete = (for {
          lastChecked <- lastChecked
        } yield processBlocks(latestBlockchainHeight, lastChecked)).recover {
          case baseException: BaseException => logger.error(baseException.failure.message)
        }
        Await.result(forComplete, Duration.Inf)
      }
    }

  }
}