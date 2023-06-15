package service

import models.analytics.CollectionsAnalysis
import models.common.{Collection => commonCollection}
import models.master.{Collection, NFT}
import models.masterTransaction.NFTDraft
import models.{master, masterTransaction}
import play.api.libs.json.{Json, Reads}
import play.api.{Configuration, Logger}
import queries.blockchain.{GetABCIInfo, GetAccount}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class Starter @Inject()(
                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
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
                       )(implicit exec: ExecutionContext, configuration: Configuration) {

  implicit val module: String = constants.Module.STARTER_SERVICE

  implicit val logger: Logger = Logger(this.getClass)

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

  def correctCollectionProperties(): Future[Unit] = {
    val collections = masterCollections.Service.fetchAll()

    def updateCollections(collections: Seq[Collection]) = utilitiesOperations.traverse(collections.filter(_.properties.get.nonEmpty)) { collection =>
      val properties = collection.properties.get.filter(_.`type` == "DECIMAL").map(x => {
        val updatedDefaultValue = if (x.defaultValue == "") "0.0" else x.defaultValue
        x.copy(`type` = constants.NFT.Data.DECIMAL, defaultValue = updatedDefaultValue)
      }) ++ collection.properties.get.filter(_.`type` == "Decimal").map(x => {
        val updatedDefaultValue = if (x.defaultValue == "") "0.0" else x.defaultValue
        x.copy(`type` = constants.NFT.Data.DECIMAL, defaultValue = updatedDefaultValue)
      }) ++ collection.properties.get.filter(_.`type` == "String").map(x => {
        x.copy(`type` = constants.NFT.Data.STRING, defaultValue = x.defaultValue)
      }) ++ collection.properties.get.filter(_.`type` == "Boolean").map(x => {
        val updatedDefaultValue = if (x.defaultValue == "") "false" else x.defaultValue
        x.copy(`type` = constants.NFT.Data.BOOLEAN, defaultValue = updatedDefaultValue)
      }) ++ collection.properties.get.filterNot(x => x.`type` == "DECIMAL" || x.`type` == "Decimal" || x.`type` == "String" || x.`type` == "Boolean")
        .map(x => x.copy(name = x.name.trim))
      masterCollections.Service.update(collection.copy(properties = Option(properties)))
    }

    for {
      collections <- collections
      _ <- updateCollections(collections)
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

  def changeAwsKey(): Future[Unit] = {
    val nfts = masterNFTs.Service.getAllNFTs
    val nftDrafts = masterTransactionNFTDrafts.Service.getAllNFTs
    val collections = masterCollections.Service.fetchAll()

    def updateKey(nfts: Seq[NFT], nftDrafts: Seq[NFTDraft], collections: Seq[Collection]): Unit = {
      val allNFTs = nfts ++ nftDrafts.map(x => x.toNFT(collection = collections.find(_.id == x.collectionId).get))
      logger.info("Copying all NFTs: " + allNFTs.length)
      var migrated = 0
      allNFTs.foreach(nft => {
        val newKey = nft.getAwsKey
        val oldKey = utilities.Collection.getOldNFTFileAwsKey(collectionId = nft.collectionId, fileName = nft.getFileName)
        Thread.sleep(25)
        if (!utilities.AmazonS3.exists(newKey)) {
          if (utilities.AmazonS3.exists(oldKey)) {
            utilities.AmazonS3.copyObject(sourceKey = oldKey, destinationKey = newKey)
          } //else logger.error("NFT does not exists: " + nft.id + " collection: " + nft.collectionId)
        } else migrated = migrated + 1
      })
      logger.info("Copied all NFTs: " + migrated)
    }

    (for {
      nfts <- nfts
      nftDrafts <- nftDrafts
      collections <- collections
    } yield updateKey(nfts, nftDrafts, collections)
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

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
    //    (for {
    //      _ <- defineOrderAndAssets()
    //    } yield ()
    //      ).recover {
    //      case exception: Exception => logger.error(exception.getLocalizedMessage)
    //    }
    Future()
  }

}