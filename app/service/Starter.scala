package service

import models.analytics.CollectionsAnalysis
import models.common.Collection.SocialProfile
import models.common.{Collection => commonCollection}
import models.master.Collection
import models.{blockchainTransaction, master, masterTransaction}
import play.api.libs.json.{Json, Reads}
import play.api.{Configuration, Logger}

import java.io.File
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.{Source => ScalaSource}
import scala.util.Try

@Singleton
class Starter @Inject()(
                         collectionsAnalysis: CollectionsAnalysis,
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

  private val uploadCollectionFilePath = constants.CommonConfig.Files.CollectionPath + "/upload.json"

  def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir)
    file.listFiles.filter(_.isFile).map(_.getPath).toList
  }

  def readFile[T](path: String)(implicit reads: Reads[T]): Future[T] = Future {
    val source = ScalaSource.fromFile(path)
    val obj = utilities.JSON.convertJsonStringToObject[T](source.mkString)
    source.close()
    obj
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

    def toNFTProperty: NFTProperty = NFTProperty(name = this.name, value = this.value)
  }

  implicit val CollectionPropertyReads: Reads[CollectionProperty] = Json.reads[CollectionProperty]

  case class NFTProperty(name: String, value: String) {
    def toProperty(nftID: String, collection: Collection): master.NFTProperty = {
      val propertyType = collection.properties.getOrElse(constants.Response.INVALID_NFT_PROPERTY.throwBaseException()).find(_.name == this.name).getOrElse(constants.Response.INVALID_NFT_PROPERTY.throwBaseException()).`type`
      val conversionTry = propertyType match {
        case constants.NFT.Data.STRING => true
        case constants.NFT.Data.NUMBER => Try(BigDecimal(this.value)).isSuccess
        case constants.NFT.Data.BOOLEAN => Try(this.value.toBoolean).isSuccess || (this.value == constants.NFT.Data.TRUE || this.value == constants.NFT.Data.FALSE)
        case _ => false
      }
      if (conversionTry) master.NFTProperty(nftId = nftID, name = name, `type` = propertyType, value = value, meta = true, mutable = false)
      else constants.Response.INVALID_NFT_PROPERTY.throwBaseException()
    }
  }

  implicit val NFTPropertyReads: Reads[NFTProperty] = Json.reads[NFTProperty]

  case class UploadCollection(id: String, creatorId: String, name: String, description: String, jsonPath: String, downloadFromIPFS: Boolean, imagePath: String, nftFormat: String, twitter: String, instagram: String, website: String, profileImagePath: Option[String], coverImagePath: Option[String], addCollection: Boolean, updateNFTs: Boolean, classificationProperties: Seq[CollectionProperty])

  case class NFT(name: String, description: String, image: String, properties: Seq[NFTProperty])

  implicit val UploadCollectionReads: Reads[UploadCollection] = Json.reads[UploadCollection]

  implicit val NFTReads: Reads[NFT] = Json.reads[NFT]

  def verifyNFT(nft: NFT, collection: Collection): Boolean = {
    if (collection.properties.isEmpty && nft.properties.isEmpty) true
    else {
      val collectionPropertiesNames = collection.properties.get.map(_.name)
      val nftPropertiesNames = nft.properties.map(_.name)
      val a = nftPropertiesNames.map(x => collectionPropertiesNames.contains(x)).forall(identity)
      val b = nft.properties.length == collection.properties.get.length
      val c = collectionPropertiesNames.map(x => nftPropertiesNames.contains(x)).forall(identity)
      val d = nft.properties.map(x => {
        val e = collection.properties.get.find(_.name == x.name)
        e.fold(false)(y => {
          if (y.`type` == constants.NFT.Data.NUMBER) Try(BigDecimal(x.value)).isSuccess
          else if (y.`type` == constants.NFT.Data.BOOLEAN) (x.value == constants.NFT.Data.SMALL_TRUE || x.value == constants.NFT.Data.TRUE || x.value == constants.NFT.Data.SMALL_FALSE || x.value == constants.NFT.Data.FALSE)
          else if (y.`type` == constants.NFT.Data.STRING) true
          else false
        })
      }).forall(identity)
      a && b && c && d
    }
  }

  private def addNft(originalNFT: NFT, uploadCollection: UploadCollection, nftImageFileName: String, collection: Collection) = {
    val classificationPropertiesNames = uploadCollection.classificationProperties.map(_.name)
    val originalNFTPropertiesNames = originalNFT.properties.map(_.name)
    val nftDetails = if (uploadCollection.id == "C5FD79BDDDCB75C4") {
      originalNFT.copy(properties = originalNFT.properties.filter(x => classificationPropertiesNames.contains(x.name)) ++ uploadCollection.classificationProperties.filterNot(x => originalNFTPropertiesNames.contains(x.name)).map(_.toNFTProperty))
    } else originalNFT
    val valid = verifyNFT(nftDetails, collection)
    println(valid)
    if (valid) {
      val nftImageFile = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.imagePath + "/" + nftImageFileName
      println(nftImageFile)
      if (uploadCollection.downloadFromIPFS) {
        val fileUrl = nftDetails.image.split("\\/").drop(2).mkString("/")
        println(fileUrl)
        utilities.IPFS.downloadFile(fileUrl, nftImageFile)
      }
      val fileHash = utilities.FileOperations.getFileHash(nftImageFile)
      val newFileName = fileHash + "." + uploadCollection.nftFormat
      val exists = Await.result(masterNFTs.Service.checkExists(fileHash), Duration.Inf)
      if (!exists) {
        try {
          val awsKey = utilities.Collection.getNFTFileAwsKey(collectionId = uploadCollection.id, fileName = newFileName)
          utilities.AmazonS3.uploadFile(awsKey, nftImageFile)
          Await.result(masterNFTs.Service.add(master.NFT(id = fileHash, assetId = None, collectionId = uploadCollection.id, name = nftDetails.name, description = nftDetails.description, totalSupply = 1, isMinted = false, fileExtension = uploadCollection.nftFormat, ipfsLink = "", edition = None)), Duration.Inf)
          Await.result(masterNFTOwners.Service.add(master.NFTOwner(nftId = fileHash, ownerId = uploadCollection.creatorId, creatorId = uploadCollection.creatorId, collectionId = uploadCollection.id, quantity = 1, saleId = None, publicListingId = None, secondaryMarketId = None)), Duration.Inf)
          Await.result(masterNFTProperties.Service.addMultiple(nftDetails.properties.map(_.toProperty(fileHash, collection))), Duration.Inf)
          Await.result(collectionsAnalysis.Utility.onNewNFT(uploadCollection.id), Duration.Inf)
          ""
        } catch {
          case exception: Exception => logger.error(exception.getLocalizedMessage)
            ""
        }
      } else ""
      utilities.FileOperations.deleteFile(nftImageFile)
    } else {
      logger.error("incorrect nft: " + nftDetails.name + " (" + uploadCollection.name + ")")
      ""
    }
  }

  def uploadCollections(): Future[Unit] = {
    val uploadCollections = readFile[Seq[UploadCollection]](uploadCollectionFilePath)

    def addCollections(uploadCollections: Seq[UploadCollection]) = utilitiesOperations.traverse(uploadCollections) { uploadCollection =>
      if (uploadCollection.addCollection) {
        val coverFileName = if (uploadCollection.coverImagePath.isDefined) {
          val coverFileName = utilities.FileOperations.getFileHash(constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.coverImagePath.get) + "." + uploadCollection.nftFormat
          val coverAwsKey = utilities.Collection.getOthersFileAwsKey(collectionId = uploadCollection.id, fileName = coverFileName)
          utilities.AmazonS3.uploadFile(objectKey = coverAwsKey, filePath = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.coverImagePath.get)
          Option(coverFileName)
        } else None

        val profileFileName = if (uploadCollection.profileImagePath.isDefined) {
          val profileFileName = utilities.FileOperations.getFileHash(constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.profileImagePath.get) + "." + uploadCollection.nftFormat
          val profileAwsKey = utilities.Collection.getOthersFileAwsKey(collectionId = uploadCollection.id, fileName = profileFileName)
          utilities.AmazonS3.uploadFile(objectKey = profileAwsKey, filePath = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.profileImagePath.get)
          Option(profileFileName)
        } else None

        val socialProfiles = Seq(
          if (uploadCollection.twitter != "") Option(SocialProfile(name = constants.Collection.SocialProfile.TWITTER, url = uploadCollection.twitter)) else None,
          if (uploadCollection.instagram != "") Option(SocialProfile(name = constants.Collection.SocialProfile.INSTAGRAM, url = uploadCollection.instagram)) else None,
          if (uploadCollection.website != "") Option(SocialProfile(name = constants.Collection.SocialProfile.WEBSITE, url = uploadCollection.website)) else None,
        ).flatten
        val collection = Collection(id = uploadCollection.id, creatorId = uploadCollection.creatorId, classificationId = None, name = uploadCollection.name, description = uploadCollection.description, socialProfiles = socialProfiles, category = constants.Collection.Category.ART, nsfw = false, properties = Option(uploadCollection.classificationProperties.map(_.toProperty)), profileFileName = profileFileName, coverFileName = coverFileName, public = true)

        def add = masterCollections.Service.add(collection)

        def analysis = collectionsAnalysis.Utility.onNewCollection(uploadCollection.id)

        for {
          _ <- add
          _ <- analysis
        } yield ()
      } else Future()

    }

    def addCollectionNFTs(uploadCollections: Seq[UploadCollection]) = utilitiesOperations.traverse(uploadCollections) { uploadCollection =>
      if (uploadCollection.updateNFTs) {
        println("START:     " + uploadCollection.id)
        val jsonFiles = getListOfFiles(constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.jsonPath)
        val collection = masterCollections.Service.tryGet(uploadCollection.id)

        def addNFTs(collection: Collection) = utilitiesOperations.traverse(jsonFiles) { nftFile =>
          val nftDetails = readFile[NFT](nftFile)
          val nftImageFileName = nftFile.split("\\.").head.split("\\/").last + "." + uploadCollection.nftFormat
          println(nftImageFileName)
          (for {
            nftDetails <- nftDetails
          } yield {
            addNft(nftDetails, uploadCollection, nftImageFileName, collection)
            ""
          }
            ).recover {
            case exception: Exception => logger.error(exception.getLocalizedMessage)
              Future("")
          }
        }

        for {
          collection <- collection
          _ <- addNFTs(collection)
        } yield ()
      } else Future()
    }

    for {
      uploadCollections <- uploadCollections
      _ <- addCollections(uploadCollections)
      _ <- addCollectionNFTs(uploadCollections)
    } yield ()
  }

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

  def defineAssets() = {
    val collections = masterCollections.Service.getAllPublic

    def getMessages(collections: Seq[Collection]) = collections.map(collection => {
      if (collection.properties.isDefined) {
        val immutableMetas = collection.properties.get.filter(x => x.meta && !x.mutable).map(_.toMetaProperty) ++ constants.Collection.DefaultProperty.MetaProperties
        val immutables = collection.properties.get.filter(x => !x.meta && !x.mutable).map(_.toMesaProperty)
        val mutableMetas = collection.properties.get.filter(x => x.meta && x.mutable).map(_.toMetaProperty)
        val mutables = collection.properties.get.filter(x => !x.meta && x.mutable).map(_.toMesaProperty)
        utilities.BlockchainTransaction.getAssetDefineMsg(fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, fromID = constants.Blockchain.MantlePlaceFromID, immutableMetas = immutableMetas, immutables = immutables, mutableMetas = mutableMetas, mutables = mutables)
      } else {
        utilities.BlockchainTransaction.getAssetDefineMsg(fromAddress = constants.Blockchain.MantlePlaceMaintainerAddress, fromID = constants.Blockchain.MantlePlaceFromID, immutableMetas = constants.Collection.DefaultProperty.MetaProperties, immutables = Seq(), mutableMetas = Seq(), mutables = Seq())
      }
    })

    for {
      collections <- collections
    } yield ()
  }

  // Delete redundant nft tags

  def start(): Future[Unit] = {
    (for {
      _ <- fixMantleMonkeys()
      _ <- updateDecimalToNumberType()
      _ <- validateAll()
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}