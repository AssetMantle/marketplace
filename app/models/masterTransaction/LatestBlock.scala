package models.masterTransaction

import com.assetmantle.modules.assets.{transactions => assetTransactions}
import com.assetmantle.modules.identities.{transactions => identityTransactions}
import com.assetmantle.modules.orders.{transactions => orderTransactions}
import com.assetmantle.modules.splits.{transactions => splitTransactions}
import com.cosmos.authz.v1beta1.MsgExec
import com.cosmos.bank.{v1beta1 => bankTx}
import com.google.protobuf.{Any => protobufAny}
import com.ibc.core.channel.{v1 => channelTx}
import constants.Scheduler
import exceptions.BaseException
import models.blockchain
import models.blockchain.Transaction
import models.blockchainTransaction.UserTransactions
import models.masterTransaction.LatestBlocks.LatestBlockTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala

case class LatestBlock(height: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[Long] {

  def id: Long = height

}

private[masterTransaction] object LatestBlocks {

  class LatestBlockTable(tag: Tag) extends Table[LatestBlock](tag, "LatestBlock") with ModelTable[Long] {

    def * = (height, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (LatestBlock.tupled, LatestBlock.unapply)

    def height = column[Long]("height", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = height

  }
}

@Singleton
class LatestBlocks @Inject()(
                              blockchainBlocks: blockchain.Blocks,
                              userTransactions: UserTransactions,
                              blockchainTransactions: blockchain.Transactions,
                              utilitiesOperations: utilities.Operations,
                              utilitiesExternalTransactions: utilities.ExternalTransactions,
                              protected val dbConfigProvider: DatabaseConfigProvider,
                            )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[LatestBlocks.LatestBlockTable, LatestBlock, Long]() {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_LATEST_BLOCK

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new LatestBlockTable(tag))

  object Service {

    def getLastChecked: Future[Long] = customQuery(tableQuery.sortBy(_.height.desc).result.headOption).map(_.fold(constants.Blockchain.UpgradeHeight.toLong - 1)(_.height))

    def update(latestHeight: Long): Future[Unit] = {
      for {
        _ <- create(LatestBlock(latestHeight))
        _ <- filterAndDelete(_.height < latestHeight)
      } yield ()
    }

  }

  object Utility {

    private def processMsgs(msgs: Seq[protobufAny], txHash: String, txHeight: Long): Future[Seq[Unit]] = {
      utilitiesOperations.traverse(msgs) { stdMsg =>
        stdMsg.getTypeUrl match {
          case schema.constants.Messages.SEND_COIN => utilitiesExternalTransactions.onSendCoin(bankTx.MsgSend.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.RECV_PACKET => utilitiesExternalTransactions.onIBCReceive(channelTx.MsgRecvPacket.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.ASSET_BURN => utilitiesExternalTransactions.onBurnNFT(assetTransactions.burn.Message.parseFrom(stdMsg.getValue), txHash)
          case schema.constants.Messages.ASSET_MUTATE => utilitiesExternalTransactions.onMutateNFT(assetTransactions.mutate.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.ASSET_RENUMERATE => utilitiesExternalTransactions.onRenumerateNFT(assetTransactions.renumerate.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.IDENTITY_PROVISION => utilitiesExternalTransactions.onProvisionIdentity(identityTransactions.provision.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.IDENTITY_UNPROVISION => utilitiesExternalTransactions.onUnprovisionIdentity(identityTransactions.unprovision.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.ORDER_CANCEL => utilitiesExternalTransactions.onOrderCancel(orderTransactions.cancel.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.ORDER_MAKE => utilitiesExternalTransactions.onOrderMake(orderTransactions.make.Message.parseFrom(stdMsg.getValue), txHeight)
          case schema.constants.Messages.ORDER_TAKE => utilitiesExternalTransactions.onOrderTake(orderTransactions.take.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.SPLIT_SEND => utilitiesExternalTransactions.onSplitSend(splitTransactions.send.Message.parseFrom(stdMsg.getValue))
          //          case schema.constants.Messages.SPLIT_WRAP =>
          //          case schema.constants.Messages.SPLIT_UNWRAP => utilitiesExternalTransactions.onSplitUnwrap(splitTransactions.unwrap.Message.parseFrom(stdMsg.getValue))
          case schema.constants.Messages.EXECUTE_AUTHORIZATION => {
            for {
              _ <- processMsgs(MsgExec.parseFrom(stdMsg.getValue).getMsgsList.asScala.toSeq, txHash, txHeight)
            } yield ()
          }
          case _ => Future()
        }

      }
    }

    private def processBlock(height: Int): Unit = {
      val transactions = blockchainTransactions.Utility.getByHeight(height)

      def userTxs(hashes: Seq[String]) = userTransactions.Service.getByHashes(hashes).map(_.map(_.txHash))

      def processAll(transactions: Seq[Transaction]) = {
        utilitiesOperations.traverse(transactions.filter(x => x.status))(x => processMsgs(x.getMessages, x.hash, x.height))
      }

      val forComplete = for {
        transactions <- transactions
        userTxHashes <- userTxs(transactions.map(_.hash))
        _ <- processAll(transactions.filterNot(x => userTxHashes.contains(x.hash)))
        _ <- Service.update(height)
      } yield ()

      Await.result(forComplete, Duration.Inf)

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val latestBlockchainHeight = blockchainBlocks.Service.getLatestHeight
        val lastChecked = Service.getLastChecked

        def processBlocks(latestBlockchainHeight: Int, lastCheckedHeight: Long): Unit = if (latestBlockchainHeight > lastCheckedHeight) {
          (lastCheckedHeight + 1).to(latestBlockchainHeight - 2).foreach(x => processBlock(x.toInt))
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