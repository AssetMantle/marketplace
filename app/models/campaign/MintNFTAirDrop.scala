package models.campaign

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchainTransaction.{AdminTransaction, AdminTransactions}
import models.campaign.MintNFTAirDrops.MintNFTAirDropTable
import models.common.Coin
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MintNFTAirDrop(accountId: String, address: String, eligibilityTxHash: String, airdropTxHash: Option[String], status: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = accountId

}

object MintNFTAirDrops {
  class MintNFTAirDropTable(tag: Tag) extends Table[MintNFTAirDrop](tag, "MintNFTAirDrop") with ModelTable[String] {

    def * = (accountId, address, eligibilityTxHash, airdropTxHash.?, status, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MintNFTAirDrop.tupled, MintNFTAirDrop.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def address = column[String]("address")

    def eligibilityTxHash = column[String]("eligibilityTxHash")

    def airdropTxHash = column[String]("airdropTxHash")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = accountId

  }

}

@Singleton
class MintNFTAirDrops @Inject()(
                                 protected val dbConfigProvider: DatabaseConfigProvider,
                                 utilitiesOperations: utilities.Operations,
                                 adminTransactions: AdminTransactions,
                                 utilitiesTransaction: utilities.Transaction,
                                 utilitiesNotification: utilities.Notification,
                               )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[MintNFTAirDrops.MintNFTAirDropTable, MintNFTAirDrop, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.CAMPAIGN_MINT_NFT_AIRDROP

  val tableQuery = new TableQuery(tag => new MintNFTAirDropTable(tag))

  object Service {

    def addForDropping(accountIdsAddressMap: Map[String, String], eligibilityTxHash: String): Future[Int] = if (constants.CommonConfig.Campaign.nftMintAirDropEnabled) create(accountIdsAddressMap.map(x => MintNFTAirDrop(accountId = x._1, address = x._2, eligibilityTxHash = eligibilityTxHash, airdropTxHash = None, status = false)).toSeq) else Future(0)

    def updateDropTxHash(accountId: String, airDropTxHash: String): Future[String] = customUpdate(tableQuery.filter(_.accountId === accountId).map(_.airdropTxHash).update(airDropTxHash)).map(_.toString)

    def getByAirDropTxHash(txHash: String): Future[Seq[MintNFTAirDrop]] = filter(_.airdropTxHash === txHash)

    def markSuccess(accountId: String): Future[Int] = customUpdate(tableQuery.filter(_.accountId === accountId).map(_.status).update(true))

    def markFailed(accountId: String): Future[Int] = customUpdate(tableQuery.filter(_.accountId === accountId).map(x => (x.airdropTxHash.?, x.status)).update((None, false)))

    def getAllForDropping: Future[Seq[MintNFTAirDrop]] = filter(x => x.airdropTxHash.?.isEmpty && !x.status)

    def getAllForUpdates: Future[Seq[MintNFTAirDrop]] = filter(x => x.airdropTxHash.?.isDefined && !x.status)

    def filterExisting(accountIds: Seq[String]): Future[Seq[String]] = {
      val existingAccountIds = filter(_.accountId.inSet(accountIds)).map(_.map(_.accountId))
      for {
        existingAccountIds <- existingAccountIds
      } yield accountIds.diff(existingAccountIds)
    }
  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("MINT_NFT_AIRDROP", 150000)

    private def transaction(accountId: String, address: String, amount: MicroNumber, eligibilityTxHash: String): Future[AdminTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = constants.Secret.nftAirDropWallet.address, toAddress = address, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount))))

      def masterTxFunc(txHash: String) = Service.updateDropTxHash(accountId = accountId, airDropTxHash = txHash)

      val adminTx = utilitiesTransaction.doAdminTx(messages = messages, wallet = constants.Secret.nftAirDropWallet, masterTxFunction = masterTxFunc)

      for {
        (adminTx, _) <- adminTx
      } yield adminTx
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val toBeDropped = Service.getAllForDropping
        val checkDrops = Service.getAllForUpdates

        def dropTokens(drops: Seq[MintNFTAirDrop]) = utilitiesOperations.traverse(drops) { drop =>
          transaction(accountId = drop.accountId, address = drop.address, amount = constants.Campaign.MintNFTAirDropAmount, eligibilityTxHash = drop.eligibilityTxHash)
        }

        def checkAndUpdate(drops: Seq[MintNFTAirDrop]) = utilitiesOperations.traverse(drops) { drop =>

          val transaction = adminTransactions.Service.tryGet(drop.airdropTxHash.get)

          def onTxComplete(adminTransaction: AdminTransaction) = if (adminTransaction.status.isDefined) {
            if (adminTransaction.status.get) {
              val markSuccess = Service.markSuccess(drop.accountId)
              val sendNotifications = utilitiesNotification.send(drop.accountId, constants.Notification.MINT_NFT_AIR_DROP_SUCCESSFUL)("")

              for {
                _ <- markSuccess
                _ <- sendNotifications
              } yield ()
            } else {
              val markMasterFailed = Service.markFailed(drop.accountId)

              for {
                _ <- markMasterFailed
              } yield ()
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()

        }

        val forComplete = (for {
          toBeDropped <- toBeDropped
          checkDrops <- checkDrops
          _ <- checkAndUpdate(checkDrops)
          _ <- dropTokens(toBeDropped)
        } yield ()).recover {
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}