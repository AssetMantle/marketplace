package service

import models.analytics.CollectionsAnalysis
import models.blockchainTransaction.DefineAsset
import models.common.{Collection => commonCollection}
import models.master.Collection
import models.{blockchainTransaction, master, masterTransaction}
import play.api.libs.json.{Json, Reads}
import play.api.{Configuration, Logger}
import queries.blockchain.{GetABCIInfo, GetAccount}
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class Starter @Inject()(
                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                         blockchainTransactionDefineAssets: blockchainTransaction.DefineAssets,
                         masterTransactionDefineAssetTxs: masterTransaction.DefineAssetTransactions,
                         collectionsAnalysis: CollectionsAnalysis,
                         getAbciInfo: GetABCIInfo,
                         getAccount: GetAccount,
                         masterAccounts: master.Accounts,
                         masterKeys: master.Keys,
                         masterCollections: master.Collections,
                         masterNFTs: master.NFTs,
                         masterNFTTags: master.NFTTags,
                         masterNFTOwners: master.NFTOwners,
                         masterNFTProperties: master.NFTProperties,
                         masterWishLists: master.WishLists,
                         masterTransactionNotifications: masterTransaction.Notifications,
                         masterTransactionNFTDrafts: masterTransaction.NFTDrafts,
                         utilitiesOperations: utilities.Operations,
                         blockchainTransactionSendCoins: blockchainTransaction.SendCoins
                       )(implicit exec: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.STARTER_SERVICE

  private implicit val logger: Logger = Logger(this.getClass)

  def validateAll(): Future[Unit] = {
    println("validating nfts")
    val collections = masterCollections.Service.getAllPublic

    def verify(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      val allNFTIds = masterNFTs.Service.getAllIdsForCollection(collection.id)
      var count = 0

      def verifyNFT(allNFTIds: Seq[String]) = utilitiesOperations.traverse(allNFTIds) { nftId =>
        val nftProperties = masterNFTProperties.Service.getForNFT(nftId)
        for {
          nftProperties <- nftProperties
        } yield {
          var cause = ""
          val valid = if (collection.properties.isEmpty && nftProperties.isEmpty) true
          else {
            val collectionPropertiesName = collection.properties.get.map(_.name)
            val nftPropertiesName = nftProperties.map(_.name)
            val a = nftPropertiesName.map(x => collectionPropertiesName.contains(x)).forall(identity)
            if (!a) cause = "collectionPropertiesName not contain nftPropertiesName, "
            val b = nftProperties.length == collectionPropertiesName.length
            if (!b) cause = cause + "length does not match, "
            val c = collectionPropertiesName.map(x => nftPropertiesName.contains(x)).forall(identity)
            if (!c) cause = cause + "nftPropertiesName not contain collectionPropertiesName "
            a && b && c
          }
          if (!valid) {
            println("### invalid nft: " + nftId + " , collection: " + collection.id)
            println(nftProperties.map(_.name).mkString(","))
            count = count + 1
          }
        }
      }

      for {
        allNFTIds <- allNFTIds
        _ <- verifyNFT(allNFTIds)
      } yield {
        if (count > 0) {
          println("***@@@ " + collection.id + ", count: " + count.toString)
          println(collection.properties.get.map(_.name))
        } else {
          println("done validating nfts")
        }
      }
    }

    for {
      collections <- collections
      _ <- verify(collections)
    } yield ()
  }

  private def deleteCollection(collectionId: String): Future[Unit] = {
    println("deleting: " + collectionId)
    val list = Seq(collectionId)
    val deleteWishlist = masterWishLists.Service.deleteCollections(list)
    val nftIDs = masterNFTs.Service.getAllIdsForCollections(list)

    val deleteAnalytics = collectionsAnalysis.Service.delete(list)
    val deleteNftOwners = masterNFTOwners.Service.deleteCollections(list)

    val deleteNFTDraft = masterTransactionNFTDrafts.Service.deleteByCollectionIds(list)

    def deleteNFTProperties(nftIDs: Seq[String]) = masterNFTProperties.Service.deleteByNFTIds(nftIDs)

    def deleteNFTTags(nftIDs: Seq[String]) = masterNFTTags.Service.deleteByNFTIds(nftIDs)

    def deleteNfts() = masterNFTs.Service.deleteCollections(list)

    def deleteAllCollections() = masterCollections.Service.delete(list)

    for {
      nftIDs <- nftIDs
      _ <- deleteWishlist
      _ <- deleteNFTProperties(nftIDs)
      _ <- deleteNFTTags(nftIDs)
      _ <- deleteNFTDraft
      _ <- deleteAnalytics
      _ <- deleteNftOwners
      _ <- deleteNfts()
      _ <- deleteAllCollections()
    } yield ()
  }

  case class CollectionProperty(name: String, `type`: String, value: String) {
    def toProperty: commonCollection.Property = {
      val propertyType = this.`type`.toUpperCase
      if (propertyType == constants.NFT.Data.STRING) {
        commonCollection.Property(name = name, `type` = propertyType, defaultValue = value)
      } else if (propertyType == constants.NFT.Data.BOOLEAN) {
        commonCollection.Property(name = name, `type` = propertyType, defaultValue = value.toBoolean.toString)
      } else if (propertyType == constants.NFT.Data.NUMBER) {
        commonCollection.Property(name = name, `type` = propertyType, defaultValue = BigDecimal(value).toString)
      } else constants.Response.INVALID_NFT_PROPERTY.throwBaseException()
    }
  }

  implicit val CollectionPropertyReads: Reads[CollectionProperty] = Json.reads[CollectionProperty]


  def fixMantleMonkeys(): Future[Unit] = {
    val classificationProperties = Seq(
      CollectionProperty(name = "Background", `type` = constants.NFT.Data.STRING, value = ""),
      CollectionProperty(name = "MonkeyBase", `type` = constants.NFT.Data.STRING, value = ""),
      CollectionProperty(name = "Skin", `type` = constants.NFT.Data.STRING, value = ""),
      CollectionProperty(name = "Eye", `type` = constants.NFT.Data.STRING, value = ""),
      CollectionProperty(name = "Body", `type` = constants.NFT.Data.STRING, value = ""),
      CollectionProperty(name = "Face", `type` = constants.NFT.Data.STRING, value = ""),
      CollectionProperty(name = "Hat", `type` = constants.NFT.Data.STRING, value = ""),
      CollectionProperty(name = "Special", `type` = constants.NFT.Data.STRING, value = ""),
    )
    val collection = masterCollections.Service.tryGet("90059167EFA307A5")
    val allNFTIDs = masterNFTs.Service.getAllIdsForCollection("90059167EFA307A5")

    def updateCollection(collection: Collection) = if (collection.properties.get.length != classificationProperties.length) masterCollections.Service.update(collection.copy(properties = Option(classificationProperties.map(_.toProperty)))) else Future()

    def fixAllProperties(collection: Collection, allNFTIDs: Seq[String]) = if (collection.properties.get.length != classificationProperties.length) {
      utilitiesOperations.traverse(allNFTIDs) { nftID =>
        val properties = masterNFTProperties.Service.getForNFT(nftID)

        def add(properties: Seq[models.master.NFTProperty]) = {
          if (properties.map(_.name).contains("Special")) {
            masterNFTProperties.Service.addMultiple(Seq(
              properties.head.copy(name = "Body", `value` = ""),
              properties.head.copy(name = "Face", `value` = ""),
              properties.head.copy(name = "Hat", `value` = "")
            ))
          } else {
            masterNFTProperties.Service.add(properties.head.copy(name = "Special", `value` = ""))
          }
        }

        for {
          properties <- properties
          _ <- add(properties)
        } yield ()
      }
    } else Future(Seq())

    for {
      collection <- collection
      allNFTIDs <- allNFTIDs
      _ <- updateCollection(collection)
      _ <- fixAllProperties(collection, allNFTIDs)
    } yield ()
  }

  def updateDecimalToNumberType(): Future[Unit] = {
    val nftIds = masterNFTProperties.Service.getOnType("DECIMAL").map(_.map(_.nftId))

    def collectionIds(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds).map(_.map(_.collectionId).distinct)

    def collections(collectionIds: Seq[String]) = masterCollections.Service.getCollections(collectionIds)

    def updateCollections(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      val properties = collection.properties.get.filterNot(_.`type` == "DECIMAL") ++ collection.properties.get.filter(_.`type` == "DECIMAL").map(x => {
        val updatedDefaultValue = if (x.defaultValue == "") 0.toString else x.defaultValue
        x.copy(`type` = constants.NFT.Data.NUMBER, defaultValue = updatedDefaultValue)
      })
      masterCollections.Service.update(collection.copy(properties = Option(properties)))
    }

    def update = masterNFTProperties.Service.changeDecimalTypeToNumber

    for {
      nftIds <- nftIds
      collectionIds <- collectionIds(nftIds)
      collections <- collections(collectionIds)
      _ <- updateCollections(collections)
      _ <- update
    } yield ()
  }

  def defineOrderAndAssets(): Future[Unit] = {
    val collections = masterCollections.Service.fetchAll()
    val abciInfo = getAbciInfo.Service.get
    val bcAccount = getAccount.Service.get(constants.Blockchain.MantlePlaceMaintainerAddress).map(_.account.toSerializableAccount)

    def getMessages(collections: Seq[Collection]) = collections.map(collection => {
      utilities.BlockchainTransaction.getDefineAssetMsg(fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, fromID = constants.Blockchain.MantlePlaceFromID, immutableMetas = collection.getImmutableMetaProperties, immutables = collection.getImmutableProperties, mutableMetas = collection.getMutableMetaProperties, mutables = collection.getMutableProperties)
    }) ++ Seq(utilities.BlockchainTransaction.getDefineOrderMsg(fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, fromID = constants.Blockchain.MantlePlaceFromID, immutableMetas = Seq(constants.Orders.OriginMetaProperty), mutableMetas = Seq(), immutables = Seq(), mutables = Seq()))

    def checkMempoolAndAddTx(collections: Seq[Collection], bcAccount: models.blockchain.Account, latestBlockHeight: Int) = {
      val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
      val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
        messages = getMessages(collections),
        fee = utilities.BlockchainTransaction.getFee(gasPrice = 0.001, gasLimit = constants.Blockchain.DefaultDefineAssetGasLimit * collections.length),
        gasLimit = constants.Blockchain.DefaultDefineAssetGasLimit * collections.size,
        account = bcAccount,
        ecKey = constants.Blockchain.MantleNodeMaintainerWallet.getECKey,
        timeoutHeight = timeoutHeight)
      val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

      def checkAndAdd() = {
        for {
          issueIdentity <- blockchainTransactionDefineAssets.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
          _ <- masterTransactionDefineAssetTxs.Service.addWithNoneStatus(txHash = txHash, collectionIds = collections.map(_.id))
        } yield issueIdentity
      }

      for {
        issueIdentity <- checkAndAdd()
      } yield issueIdentity
    }

    def broadcastTxAndUpdate(defineAsset: DefineAsset) = {

      println(defineAsset.getTxRawAsHexString)
      val broadcastTx = broadcastTxSync.Service.get(defineAsset.getTxRawAsHexString)

      def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionDefineAssets.Service.markFailedWithLog(txHashes = Seq(defineAsset.txHash), log = errorResponse.get.error.data)
      else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionDefineAssets.Service.markFailedWithLog(txHashes = Seq(defineAsset.txHash), log = successResponse.get.result.log)
      else Future(0)

      for {
        (successResponse, errorResponse) <- broadcastTx
        _ <- update(successResponse, errorResponse)
      } yield ()
    }

    for {
      collections <- collections
      abciInfo <- abciInfo
      bcAccount <- bcAccount
      defineAsset <- checkMempoolAndAddTx(collections, bcAccount, abciInfo.result.response.last_block_height.toInt)
      _ <- broadcastTxAndUpdate(defineAsset)
    } yield ()
  }

  def updateIdentityIDs(): Future[Unit] = {
    println("Updating identity id")

    val accountIds = masterAccounts.Service.getEmptyIdentityID

    def process(accountIds: Seq[String]) = utilitiesOperations.traverse(accountIds) { accountId =>
      masterAccounts.Service.updateIdentityId(accountId)
    }

    for {
      accountIds <- accountIds
      _ <- process(accountIds)
    } yield {
      println("Update identity id DONE")
    }
  }

//  def changeAwsKey(): Future[Unit]

  def updateAssetIDs(): Future[Unit] = {
    println("Updating asset id")
    val collections = masterCollections.Service.fetchAll()
    val nfts = masterNFTs.Service.fetchAllWithNullAssetID()

    def process(collections: Seq[Collection], nfts: Seq[master.NFT]) = utilitiesOperations.traverse(collections) { collection =>
      val collectionNFTs = nfts.filter(_.collectionId == collection.id)
      val nftProperties = masterNFTProperties.Service.get(collectionNFTs.map(_.id))

      def update(nftProperties: Seq[master.NFTProperty]) = utilitiesOperations.traverse(collectionNFTs) { nft =>
        masterNFTs.Service.updateAssetID(nft.id, nft.getAssetID(nftProperties.filter(_.nftId == nft.id), collection))
      }

      (for {
        nftProperties <- nftProperties
        _ <- update(nftProperties)
      } yield ()
        ).recover {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
      }
    }

    for {
      collections <- collections
      nfts <- nfts
      _ <- process(collections, nfts)
    } yield {
      println("Update asset id DONE")
    }
  }

  def fixAllMultipleActiveKeys(): Unit = {
    val allActiveKeys = Await.result(masterKeys.Service.fetchAllActive, Duration.Inf)
    val allAccountIds = allActiveKeys.map(_.accountId).distinct
    if (allAccountIds.length != allActiveKeys.length) {
      println("correcting active")
      val wrongAccountIds = allAccountIds.flatMap(x => if (allActiveKeys.count(_.accountId == x) > 1) Option(x) else None)
      println(wrongAccountIds)
      println(wrongAccountIds.length)
      Await.result(masterKeys.Service.insertOrUpdateMultiple(allActiveKeys.filter(x => wrongAccountIds.contains(x.accountId) && x.encryptedPrivateKey.length == 0).map(_.copy(active = false))), Duration.Inf)
      val updatedAllActiveKeys = Await.result(masterKeys.Service.fetchAllActive, Duration.Inf)
      val updatedAllAccountIds = updatedAllActiveKeys.map(_.accountId).distinct
      val wrongManagedAccountIds = updatedAllAccountIds.flatMap(x => if (updatedAllActiveKeys.count(_.accountId == x) > 1) Option(x) else None)
      println(wrongManagedAccountIds)
      println(wrongManagedAccountIds.length)
      val wrongManagedKeys = updatedAllActiveKeys.filter(x => wrongManagedAccountIds.contains(x.accountId) && x.encryptedPrivateKey.length > 0)
      wrongManagedAccountIds.foreach(x => {
        val updateKeys = wrongManagedKeys.filter(_.accountId == x).sortBy(_.createdOnMillisEpoch.getOrElse(0L)).reverse.drop(1)
        Await.result(masterKeys.Service.insertOrUpdateMultiple(updateKeys.map(_.copy(active = false))), Duration.Inf)
      })
      val finalAllActiveKeys = Await.result(masterKeys.Service.fetchAllActive, Duration.Inf)
      val finalAllAccountIds = finalAllActiveKeys.map(_.accountId).distinct
      println(finalAllAccountIds.flatMap(x => if (finalAllActiveKeys.count(_.accountId == x) > 1) Option(x) else None))
      println(finalAllAccountIds.flatMap(x => if (finalAllActiveKeys.count(_.accountId == x) > 1) Option(x) else None).length)
    } else {
      println("all correct")
    }
  }

  def markMintReady(): Future[Unit] = {
    val nftIds = masterNFTOwners.Service.getSoldNFTs(constants.Collection.GenesisCollectionIDs)

    def update(ids: Seq[String]) = masterNFTs.Service.markReadyForMint(ids)

    for {
      nftIds <- nftIds
      _ <- update(nftIds)
    } yield ()
  }

  // Delete redundant nft tags

  def start(): Future[Unit] = {
    (for {
      _ <- defineOrderAndAssets()
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}