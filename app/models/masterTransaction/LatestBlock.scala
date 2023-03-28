package models.masterTransaction

import com.assets.{transactions => assetTransactions}
import com.cosmos.bank.{v1beta1 => bankTx}
import com.ibc.core.channel.{v1 => channelTx}
import com.identities.{transactions => identityTransactions}
import com.orders.{transactions => orderTransactions}
import com.splits.{transactions => splitTransactions}
import constants.Scheduler
import exceptions.BaseException
import models.blockchain
import models.blockchain.Transaction
import models.traits._
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

    private def processTransaction(transaction: Transaction) = if (transaction.status) {
      utilitiesOperations.traverse(transaction.getMessages) { stdMsg =>
        stdMsg.getTypeUrl match {
          case constants.Blockchain.TransactionMessage.SEND_COIN => utilitiesExternalTransactions.onSendCoin(bankTx.MsgSend.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.RECV_PACKET => utilitiesExternalTransactions.onIBCReceive(channelTx.MsgRecvPacket.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.ASSET_BURN => utilitiesExternalTransactions.onBurnNFT(assetTransactions.burn.Message.parseFrom(stdMsg.getValue), transaction.hash)
          case constants.Blockchain.TransactionMessage.ASSET_MUTATE => utilitiesExternalTransactions.onMutateNFT(assetTransactions.mutate.Message.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.ASSET_RENUMERATE => utilitiesExternalTransactions.onRenumerateNFT(assetTransactions.renumerate.Message.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.IDENTITY_PROVISION => utilitiesExternalTransactions.onProvisionIdentity(identityTransactions.provision.Message.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.IDENTITY_UNPROVISION => utilitiesExternalTransactions.onUnprovisionIdentity(identityTransactions.unprovision.Message.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.ORDER_CANCEL => utilitiesExternalTransactions.onOrderCancel(orderTransactions.cancel.Message.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.ORDER_MAKE => utilitiesExternalTransactions.onOrderMake(orderTransactions.make.Message.parseFrom(stdMsg.getValue), transaction.height, transaction.hash)
          case constants.Blockchain.TransactionMessage.ORDER_TAKE => utilitiesExternalTransactions.onOrderTake(orderTransactions.take.Message.parseFrom(stdMsg.getValue))
          case constants.Blockchain.TransactionMessage.SPLIT_SEND => utilitiesExternalTransactions.onSplitSend(splitTransactions.send.Message.parseFrom(stdMsg.getValue))
          //          case constants.Blockchain.TransactionMessage.SPLIT_WRAP =>
          //          case constants.Blockchain.TransactionMessage.SPLIT_UNWRAP => utilitiesExternalTransactions.onSplitUnwrap(splitTransactions.unwrap.Message.parseFrom(stdMsg.getValue))
        }

      }
    }

    private def processBlock(height: Int): Unit = {
      val transactions = blockchainTransactions.Service.getByHeight(height)

      val forComplete = for {
        transactions <- transactions
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