package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.blockchainTransaction.MintAsset
import models.master._
import models.traits._
import models.{blockchain, blockchainTransaction, campaign, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MintAssetTransaction(txHash: String, nftID: String, minterAccountID: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.nftID)
}

object MintAssetTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_MINT_ASSET_TRANSACTION

  class MintAssetTransactionTable(tag: Tag) extends Table[MintAssetTransaction](tag, "MintAssetTransaction") with ModelTable[String] {

    def * = (txHash, nftID, minterAccountID, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (MintAssetTransaction.tupled, MintAssetTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftID = column[String]("nftID", O.PrimaryKey)

    def minterAccountID = column[String]("minterAccountID")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new MintAssetTransactionTable(tag))

}

@Singleton
class MintAssetTransactions @Inject()(
                                       protected val databaseConfigProvider: DatabaseConfigProvider,
                                       blockchainAssets: blockchain.Assets,
                                       campaignMintNFTAirDrops: campaign.MintNFTAirDrops,
                                       collectionsAnalysis: CollectionsAnalysis,
                                       masterKeys: master.Keys,
                                       masterNFTs: master.NFTs,
                                       masterNFTOwners: master.NFTOwners,
                                       masterNFTProperties: master.NFTProperties,
                                       masterCollections: master.Collections,
                                       broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                       utilitiesOperations: utilities.Operations,
                                       getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                       getAccount: queries.blockchain.GetAccount,
                                       getAbciInfo: queries.blockchain.GetABCIInfo,
                                       utilitiesNotification: utilities.Notification,
                                       blockchainTransactionMintAssets: blockchainTransaction.MintAssets,
                                     )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[MintAssetTransactions.MintAssetTransactionTable, MintAssetTransaction, String](
    databaseConfigProvider,
    MintAssetTransactions.TableQuery,
    executionContext,
    MintAssetTransactions.module,
    MintAssetTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, nftIDs: Seq[String], minterAccountIDs: Map[String, String]): Future[Unit] = create(nftIDs.map(x => MintAssetTransaction(txHash = txHash, nftID = x, minterAccountID = minterAccountIDs.getOrElse(x, constants.Response.NFT_OWNER_NOT_FOUND.throwBaseException()), status = None)))

    def getByTxHash(txHash: String): Future[Seq[MintAssetTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(MintAssetTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(MintAssetTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[MintAssetTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)

    def getWithStatusTrueOrPending(ids: Seq[String]): Future[Seq[String]] = filter(x => x.nftID.inSet(ids) && (x.status || x.status.?.isEmpty)).map(_.map(_.nftID))
  }

  object Utility {

    private def transaction(nftIDs: Seq[String], gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(constants.Blockchain.MantlePlaceMaintainerAddress).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val nfts = masterNFTs.Service.getByIds(nftIDs)
      val nftOwners = masterNFTOwners.Service.getByIds(nftIDs)
      val nftProperties = masterNFTProperties.Service.get(nftIDs)

      def collections(collectionIDs: Seq[String]) = masterCollections.Service.getCollections(collectionIDs)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String], nfts: Seq[NFT], collections: Seq[Collection], nftOwners: Seq[NFTOwner], nftProperties: Seq[NFTProperty]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = nfts.map(nft => {
            val collection = collections.find(_.id == nft.collectionId).getOrElse(constants.Response.COLLECTION_NOT_FOUND.throwBaseException())
            utilities.BlockchainTransaction.getMintAssetMsg(fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, toID = nftOwners.find(_.nftId == nft.id).getOrElse(constants.Response.NFT_NOT_FOUND.throwBaseException()).getOwnerIdentityID, classificationID = collection.getClassificationID, fromID = constants.Blockchain.MantlePlaceFromID, immutableMetas = nft.getImmutableMetaProperties(nftProperties, collection), mutableMetas = nft.getMutableMetaProperties(nftProperties), immutables = nft.getImmutableProperties(nftProperties, collection), mutables = nft.getMutableProperties(nftProperties))
          }),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultMintAssetGasLimit * nftIDs.length),
          gasLimit = constants.Blockchain.DefaultMintAssetGasLimit * nftIDs.length,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              mintAsset <- blockchainTransactionMintAssets.Service.add(txHash = txHash, fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftIDs = nftIDs, minterAccountIDs = nftOwners.map(x => x.nftId -> x.ownerId).toMap)
            } yield mintAsset
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          mintAsset <- checkAndAdd(unconfirmedTxHashes)
        } yield (mintAsset, txRawBytes)
      }

      def broadcastTxAndUpdate(mintAsset: MintAsset, txRawBytes: Array[Byte]) = {

        val broadcastTx = broadcastTxSync.Service.get(mintAsset.getTxRawAsHexString(txRawBytes))

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionMintAssets.Service.markFailedWithLog(txHashes = Seq(mintAsset.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionMintAssets.Service.markFailedWithLog(txHashes = Seq(mintAsset.txHash), log = successResponse.get.result.log)
        else Future(0)

        for {
          (successResponse, errorResponse) <- broadcastTx
          _ <- update(successResponse, errorResponse)
        } yield ()
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        nfts <- nfts
        nftOwners <- nftOwners
        nftProperties <- nftProperties
        collections <- collections(nfts.map(_.collectionId).distinct)
        (mintAsset, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase), nfts, collections, nftOwners, nftProperties)
        _ <- broadcastTxAndUpdate(mintAsset, txRawBytes)
      } yield mintAsset
    }

    private def mintGenesisAssets(): Future[Unit] = {
      val anyPendingTx = Service.checkAnyPendingTx
      val nfts = masterNFTs.Service.getForMinting

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
        val tx = transaction(nftIDs = nfts.map(_.id), gasPrice = 0.0001, ecKey = constants.Blockchain.MantleNodeMaintainerWallet.getECKey)

        def updateMasterKeys() = masterNFTs.Service.markNFTsMintPending(nfts.map(_.id))

        for {
          tx <- tx
          _ <- updateMasterKeys()
        } yield tx.txHash
      } else Future("")

      for {
        nfts <- nfts
        mintAssets <- filterAlreadyMintedNFTs(nfts.filter(x => !x.isMinted.getOrElse(true)))
        anyPendingTx <- anyPendingTx
        txHash <- doTx(mintAssets, anyPendingTx)
      } yield if (txHash != "") logger.info("MINT_ASSET: " + txHash + " ( " + nfts.map(_.assetId).mkString(",") + " )")
    }

    private def checkTransactions() = {
      val mintAssetTxs = Service.getAllPendingStatus

      def checkAndUpdate(mintAssetTxs: Seq[MintAssetTransaction]) = utilitiesOperations.traverse(mintAssetTxs.map(_.txHash).distinct) { txHash =>
        val transaction = blockchainTransactionMintAssets.Service.tryGet(txHash)

        def onTxComplete(transaction: MintAsset) = if (transaction.status.isDefined) {
          if (transaction.status.get) {
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

            def checkAndForAirDrop = {
              val toBeDropped = campaignMintNFTAirDrops.Service.filterExisting(mintAssetTxs.filter(_.txHash == txHash).map(_.minterAccountID))

              def keys(toBeDropped: Seq[String]) = if (toBeDropped.nonEmpty) masterKeys.Service.getAllActiveKeys(toBeDropped) else Future(Seq())

              def add(keys: Seq[Key]) = if (keys.nonEmpty) campaignMintNFTAirDrops.Service.addForDropping(accountIdsAddressMap = keys.map(x => x.accountId -> x.address).toMap, amount = constants.Campaign.MintNFTAirDropAmount, eligibilityTxHash = txHash) else Future()

              for {
                toBeDropped <- toBeDropped
                keys <- keys(toBeDropped)
                _ <- add(keys)
              } yield ()
            }

            (for {
              _ <- markSuccess
              _ <- updateMaster
              nfts <- nfts
              _ <- updateAnalysis(nfts)
            } yield ()
              ).recover {
              case _: Exception => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markMasterFailed = Service.markFailed(txHash)
            val updateMaster = masterNFTs.Service.markNFTsMintFailed(mintAssetTxs.filter(_.txHash == txHash).map(_.nftID))

            (for {
              _ <- markMasterFailed
              _ <- updateMaster
            } yield ()
              ).recover {
              case _: Exception => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
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
      val name: String = MintAssetTransactions.module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {
        val doMintAssets = mintGenesisAssets()

        val forComplete = (for {
          _ <- doMintAssets
          _ <- checkTransactions()
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}