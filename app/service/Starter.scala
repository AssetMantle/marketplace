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
      } else if (propertyType == constants.NFT.Data.DECIMAL) {
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
        case constants.NFT.Data.DECIMAL => Try(BigDecimal(this.value)).isSuccess
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
          if (y.`type` == constants.NFT.Data.DECIMAL) Try(BigDecimal(x.value)).isSuccess
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
          Await.result(masterNFTs.Service.add(master.NFT(id = fileHash, collectionId = uploadCollection.id, name = nftDetails.name, description = nftDetails.description, totalSupply = 1, isMinted = false, fileExtension = uploadCollection.nftFormat, ipfsLink = "", edition = None)), Duration.Inf)
          Await.result(masterNFTOwners.Service.add(master.NFTOwner(nftId = fileHash, ownerId = uploadCollection.creatorId, creatorId = uploadCollection.creatorId, collectionId = uploadCollection.id, quantity = 1, saleId = None, publicListingId = None)), Duration.Inf)
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
          if (uploadCollection.website != "") Option(SocialProfile(name = constants.Collection.SocialProfile.WEBSITE, url = uploadCollection.instagram)) else None,
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
        }
      }
    }

    for {
      collections <- collections
      _ <- verify(collections)
    } yield ()
  }

  def addValentineNFTs(): Future[Unit] = {
    case class ValentineNFT(fileName: String, name: String, description: String, format: String)
    val nfts = Seq(
      ValentineNFT(fileName = "1.gif", name = "name1", description = "", format = "gif"),
      ValentineNFT(fileName = "2.gif", name = "name2", description = "", format = "gif"),
      ValentineNFT(fileName = "3.gif", name = "name3", description = "", format = "gif"),
      ValentineNFT(fileName = "4.gif", name = "name4", description = "", format = "gif"),
      ValentineNFT(fileName = "5.gif", name = "name5", description = "", format = "gif"),
      ValentineNFT(fileName = "6.gif", name = "name6", description = "", format = "gif"),
      ValentineNFT(fileName = "7.gif", name = "name7", description = "", format = "gif"),
      ValentineNFT(fileName = "8.gif", name = "name8", description = "", format = "gif"),
      ValentineNFT(fileName = "9.gif", name = "name9", description = "", format = "gif"),
      ValentineNFT(fileName = "10.gif", name = "name9", description = "", format = "gif"),
      ValentineNFT(fileName = "11.gif", name = "name9", description = "", format = "gif"),
    )
    val allUsernames = masterAccounts.Service.getAllUsernames
    var allocatedUsernames: Seq[String] = Seq()
    var nftsDistributed = 0

    def getCollection = masterCollections.Service.tryGet("D4C3FD5554AEDB64")

    def uploadNFTs(allUsernames: Seq[String], collection: Collection): Unit = nfts.foreach { valentineNFT =>
      println(valentineNFT.name)
      println(allUsernames.length)
      val nftImageFile = constants.CommonConfig.Files.CollectionPath + "/" + valentineNFT.fileName
      val fileHash = utilities.FileOperations.getFileHash(nftImageFile)
      val newFileName = fileHash + "." + valentineNFT.format
      val allocateTo = if (nftsDistributed == nfts.length - 1) {
        allUsernames.diff(allocatedUsernames)
      } else {
        util.Random.shuffle(allUsernames.diff(allocatedUsernames)).take(allUsernames.length / nfts.length)
      }
      println(allocateTo.length)
      println(allocatedUsernames.intersect(allocateTo).length)
      allocatedUsernames = allocatedUsernames ++ allocateTo
      println(allocatedUsernames.length)
      val exists = Await.result(masterNFTs.Service.checkExists(fileHash), Duration.Inf)
      if (!exists) {
        try {
          val awsKey = utilities.Collection.getNFTFileAwsKey(collectionId = collection.id, fileName = newFileName)
          utilities.AmazonS3.uploadFile(awsKey, nftImageFile)
          Await.result(masterNFTs.Service.add(master.NFT(id = fileHash, collectionId = collection.id, name = valentineNFT.name, description = valentineNFT.description, totalSupply = allocateTo.length, isMinted = false, fileExtension = valentineNFT.format, ipfsLink = "", edition = None)), Duration.Inf)
          Await.result(masterNFTOwners.Service.add(allocateTo.map(x => master.NFTOwner(nftId = fileHash, ownerId = x, creatorId = collection.creatorId, collectionId = collection.id, quantity = 1, saleId = None, publicListingId = None))), Duration.Inf)
          Await.result(collectionsAnalysis.Utility.onNewNFT(collection.id), Duration.Inf)
          nftsDistributed = nftsDistributed + 1
          println(valentineNFT.name + " done")
        } catch {
          case exception: Exception => logger.error(exception.getLocalizedMessage)
        }
      }
    }

    for {
      allUsernames <- allUsernames
      collection <- getCollection
    } yield uploadNFTs(allUsernames, collection)
  }

  // Delete redundant nft tags
  def start(): Future[Unit] = {
    (for {
      _ <- addValentineNFTs()
      _ <- uploadCollections()
      _ <- validateAll()
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}