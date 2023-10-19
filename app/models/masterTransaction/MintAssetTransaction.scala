package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.blockchainTransaction.{AdminTransaction, AdminTransactions}
import models.master._
import models.masterTransaction.MintAssetTransactions.MintAssetTransactionTable
import models.traits._
import models.{blockchain, campaign, master}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MintAssetTransaction(txHash: String, nftID: String, toAccountID: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = nftID

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.nftID)
}

private[masterTransaction] object MintAssetTransactions {

  class MintAssetTransactionTable(tag: Tag) extends Table[MintAssetTransaction](tag, "MintAssetTransaction") with ModelTable2[String, String] {

    def * = (txHash, nftID, toAccountID, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MintAssetTransaction.tupled, MintAssetTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftID = column[String]("nftID")

    def toAccountID = column[String]("toAccountID")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftID

  }

}

@Singleton
class MintAssetTransactions @Inject()(
                                       protected val dbConfigProvider: DatabaseConfigProvider,
                                       blockchainAssets: blockchain.Assets,
                                       blockchainIdentities: blockchain.Identities,
                                       campaignMintNFTAirDrops: campaign.MintNFTAirDrops,
                                       campaignIneligibleMintNFTAirDrops: campaign.IneligibleMintNFTAirDrops,
                                       collectionsAnalysis: CollectionsAnalysis,
                                       masterKeys: master.Keys,
                                       masterNFTs: master.NFTs,
                                       masterNFTOwners: master.NFTOwners,
                                       masterNFTProperties: master.NFTProperties,
                                       masterCollections: master.Collections,
                                       utilitiesOperations: utilities.Operations,
                                       utilitiesTransaction: utilities.Transaction,
                                       utilitiesNotification: utilities.Notification,
                                       adminTransactions: AdminTransactions,
                                     )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[MintAssetTransactions.MintAssetTransactionTable, MintAssetTransaction, String, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_MINT_ASSET_TRANSACTION

  val tableQuery = new TableQuery(tag => new MintAssetTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, nftIDs: Seq[String], toAccountIDs: Map[String, String]): Future[String] = create(nftIDs.map(x => MintAssetTransaction(txHash = txHash, nftID = x, toAccountID = toAccountIDs.getOrElse(x, constants.Response.NFT_OWNER_NOT_FOUND.throwBaseException()), status = None))).map(_.toString)

    def getByTxHash(txHash: String): Future[Seq[MintAssetTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[MintAssetTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("MINT_ASSET", 150000)

    private def transaction(nftIDs: Seq[String]): Future[AdminTransaction] = {
      val nfts = masterNFTs.Service.getByIds(nftIDs)
      val nftOwners = masterNFTOwners.Service.getByIds(nftIDs)
      val nftProperties = masterNFTProperties.Service.get(nftIDs)

      def collections(collectionIDs: Seq[String]) = masterCollections.Service.getCollections(collectionIDs)

      def identityIDExists(nftOwners: Seq[NFTOwner]) = blockchainIdentities.Service.getIDsAlreadyExists(nftOwners.map(_.getOwnerIdentityID.asString).distinct)

      def doTx(nfts: Seq[NFT], collections: Seq[Collection], nftOwners: Seq[NFTOwner], nftProperties: Seq[NFTProperty], identityIDExists: Seq[String]) = if (identityIDExists.distinct.length == nftOwners.map(_.ownerId).distinct.length) {
        val messages = nfts.map(nft => {
          val collection = collections.find(_.id == nft.collectionId).getOrElse(constants.Response.COLLECTION_NOT_FOUND.throwBaseException())
          utilities.BlockchainTransaction.getMintAssetMsg(
            fromAddress = constants.Secret.mintAssetWallet.address,
            toID = nftOwners.find(_.nftId == nft.id).getOrElse(constants.Response.NFT_NOT_FOUND.throwBaseException()).getOwnerIdentityID,
            classificationID = collection.getClassificationID,
            fromID = constants.Transaction.MantlePlaceIdentityID,
            immutableMetas = nft.getImmutableMetaProperties(nftProperties, collection),
            mutableMetas = nft.getMutableMetaProperties(nftProperties, collection),
            immutables = nft.getImmutableProperties(nftProperties),
            mutables = nft.getMutableProperties(nftProperties))
        })

        def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, nftIDs = nftIDs, toAccountIDs = nftOwners.map(x => x.nftId -> x.ownerId).toMap)

        val adminTx = utilitiesTransaction.doAdminTx(messages = messages, wallet = constants.Secret.mintAssetWallet, masterTxFunction = masterTxFunc)

        for {
          (adminTx, _) <- adminTx
        } yield adminTx

      } else {
        logger.error(s"${nftOwners.map(_.getOwnerIdentityID.asString).diff(identityIDExists).mkString(", ")} identities does not exist for minting asset")
        constants.Response.IDENTITY_DOES_NOT_EXIST_TO_MINT.throwBaseException()
      }


      for {
        nfts <- nfts
        collections <- collections(nfts.map(_.collectionId))
        nftOwners <- nftOwners
        identityIDExists <- identityIDExists(nftOwners)
        nftProperties <- nftProperties
        adminTx <- doTx(nfts = nfts, collections = collections, nftOwners = nftOwners, nftProperties = nftProperties, identityIDExists = identityIDExists)
      } yield adminTx

    }

    private def mintAssets(): Future[Unit] = {
      val anyPendingTx = Service.checkAnyPendingTx
      val definedAssets = masterCollections.Service.getDefined

      def nfts(definedAssets: Seq[String]) = masterNFTs.Service.getForMinting(definedAssets)

      def filterAlreadyMintedNFTs(nfts: Seq[NFT]) = {
        val assetIDs = nfts.map(_.getAssetID.asString)
        val existingAssetIDsString = blockchainAssets.Service.getIDsAlreadyExists(assetIDs)

        def updateMaster(nftIDs: Seq[String]) = if (nftIDs.nonEmpty) masterNFTs.Service.markNFTsMinted(nftIDs) else Future(0)

        def updateAnalysis(nftIDs: Seq[String]) = if (nftIDs.nonEmpty) {
          val mintedNFTs = nfts.filter(x => nftIDs.contains(x.id)).groupMap(_.collectionId)(_.id)
          utilitiesOperations.traverse(mintedNFTs.keys.toSeq) { collectionId =>
            val update = collectionsAnalysis.Utility.addMinted(collectionId, mintedNFTs.getOrElse(collectionId, Seq()).length)
            for {
              _ <- update
            } yield ()
          }
        } else Future(Seq())

        for {
          existingAssetIDsString <- existingAssetIDsString
          _ <- updateMaster(nfts.filter(x => existingAssetIDsString.contains(x.assetId.getOrElse(""))).map(_.id))
          _ <- updateAnalysis(nfts.filter(x => existingAssetIDsString.contains(x.assetId.getOrElse(""))).map(_.id))
        } yield nfts.filterNot(x => existingAssetIDsString.contains(x.assetId.getOrElse(""))).take(250)
      }

      def doTx(nfts: Seq[NFT], anyPendingTx: Boolean) = if (nfts.nonEmpty && !anyPendingTx) {
        val tx = transaction(nftIDs = nfts.map(_.id))

        def updateMasterKeys() = masterNFTs.Service.markNFTsMintPending(nfts.map(_.id))

        for {
          tx <- tx
          _ <- updateMasterKeys()
        } yield tx.txHash
      } else Future("")

      for {
        definedAssets <- definedAssets
        nfts <- nfts(definedAssets)
        unmintedAssets <- filterAlreadyMintedNFTs(nfts.filter(x => !x.isMinted.getOrElse(true)))
        anyPendingTx <- anyPendingTx
        txHash <- doTx(unmintedAssets, anyPendingTx)
      } yield if (txHash != "") logger.info("MINT_ASSET: " + txHash + " ( " + nfts.map(_.assetId).mkString(",") + " )")
    }

    private def checkTransactions() = {
      val mintAssetTxs = Service.getAllPendingStatus

      def checkAndUpdate(mintAssetTxs: Seq[MintAssetTransaction]) = utilitiesOperations.traverse(mintAssetTxs.map(_.txHash).distinct) { txHash =>
        val transaction = adminTransactions.Service.tryGet(txHash)

        def onTxComplete(adminTransaction: AdminTransaction) = if (adminTransaction.status.isDefined) {
          if (adminTransaction.status.get) {
            val markSuccess = Service.markSuccess(txHash)
            val updateMaster = masterNFTs.Service.markNFTsMinted(mintAssetTxs.filter(_.txHash == txHash).map(_.nftID))
            val nfts = masterNFTs.Service.getByIds(mintAssetTxs.filter(_.txHash == txHash).map(_.nftID))

            def updateAnalysis(nfts: Seq[NFT]) = if (nfts.nonEmpty) {
              val mintedNFTs = nfts.groupMap(_.collectionId)(_.id)
              utilitiesOperations.traverse(mintedNFTs.keys.toSeq) { collectionId =>
                val update = collectionsAnalysis.Utility.addMinted(collectionId, mintedNFTs.getOrElse(collectionId, Seq()).length)
                for {
                  _ <- update
                } yield ()
              }
            } else Future(Seq())

            def checkForAirDrop(nftIDs: Seq[String]) = {
              val ineligibles = campaignIneligibleMintNFTAirDrops.Service.getIneligibles(nftIDs)

              def toBeDropped(ineligibles: Seq[String]) = campaignMintNFTAirDrops.Service.filterExisting(mintAssetTxs.filterNot(x => ineligibles.contains(x.nftID)).filter(_.txHash == txHash).map(_.toAccountID))

              def keys(toBeDropped: Seq[String]) = if (toBeDropped.nonEmpty) masterKeys.Service.getAllActiveKeys(toBeDropped) else Future(Seq())

              def add(keys: Seq[Key]) = if (keys.nonEmpty) campaignMintNFTAirDrops.Service.addForDropping(accountIdsAddressMap = keys.map(x => x.accountId -> x.address).toMap, eligibilityTxHash = txHash) else Future()

              for {
                ineligibles <- ineligibles
                toBeDropped <- toBeDropped(ineligibles)
                keys <- keys(toBeDropped)
                _ <- add(keys)
              } yield ()
            }

            for {
              _ <- markSuccess
              _ <- updateMaster
              nfts <- nfts
              _ <- updateAnalysis(nfts)
              _ <- checkForAirDrop(nfts.map(_.id))
            } yield ()
          } else {
            val markMasterFailed = Service.markFailed(txHash)
            val updateMaster = masterNFTs.Service.markNFTsMintFailed(mintAssetTxs.filter(_.txHash == txHash).map(_.nftID))

            for {
              _ <- markMasterFailed
              _ <- updateMaster
            } yield ()
          }
        } else Future()

        for {
          transaction <- transaction
          _ <- onTxComplete(transaction)
        } yield ()
      }

      for {
        mintAssetTxs <- mintAssetTxs
        _ <- checkAndUpdate(mintAssetTxs)
      } yield ()

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {
        val doMintAssets = mintAssets()

        val forComplete = (for {
          _ <- doMintAssets
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