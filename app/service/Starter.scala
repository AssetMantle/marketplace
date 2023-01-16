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

  case class CollectionProperty(name: String, `type`: String) {
    def toProperty: commonCollection.Property = commonCollection.Property(name = name, `type` = `type`, defaultValue = "")
  }

  implicit val CollectionPropertyReads: Reads[CollectionProperty] = Json.reads[CollectionProperty]

  case class NFTProperty(name: String, `value`: String) {
    def toProperty(nftID: String): master.NFTProperty = master.NFTProperty(nftId = nftID, name = name, `type` = constants.NFT.Data.STRING, `value` = `value`, meta = true, mutable = false)
  }

  implicit val NFTPropertyReads: Reads[NFTProperty] = Json.reads[NFTProperty]

  case class UploadCollection(id: String, name: String, jsonPath: String, downloadFromIPFS: Boolean, imagePath: String, description: String, website: String, profile: String, cover: String, twitter: String, instagram: String, accountId: String, classificationProperties: Seq[CollectionProperty], nftFormat: String)

  case class NFT(name: String, description: String, image: String, properties: Seq[NFTProperty])

  implicit val UploadCollectionReads: Reads[UploadCollection] = Json.reads[UploadCollection]

  implicit val NFTReads: Reads[NFT] = Json.reads[NFT]

  def verifyNFT(nft: NFT, uploadCollection: UploadCollection): Boolean = {
    val propertiesName = uploadCollection.classificationProperties.map(_.name)
    nft.properties.map(x => propertiesName.contains(x.name)).forall(identity) && nft.properties.length == uploadCollection.classificationProperties.length
  }

  private def addNft(nftDetails: NFT, uploadCollection: UploadCollection, nftImageFile: String) = if (verifyNFT(nftDetails, uploadCollection)) {
    val fileHash = utilities.FileOperations.getFileHash(nftImageFile)
    val extension = utilities.FileOperations.fileExtensionFromName(nftImageFile)
    val newFileName = fileHash + "." + extension
    val exists = Await.result(masterNFTs.Service.checkExists(fileHash), Duration.Inf)
    if (!exists) {
      try {
        val awsKey = utilities.Collection.getNFTFileAwsKey(collectionId = uploadCollection.id, fileName = newFileName)
        utilities.AmazonS3.uploadFile(awsKey, nftImageFile)
        Await.result(masterNFTs.Service.add(master.NFT(id = fileHash, collectionId = uploadCollection.id, name = nftDetails.name, description = nftDetails.description, totalSupply = 1, isMinted = false, fileExtension = extension, ipfsLink = "", edition = None)), Duration.Inf)
        Await.result(masterNFTOwners.Service.add(master.NFTOwner(nftId = fileHash, ownerId = uploadCollection.accountId, creatorId = uploadCollection.accountId, collectionId = uploadCollection.id, quantity = 1, saleId = None, publicListingId = None)), Duration.Inf)
        Await.result(masterNFTProperties.Service.addMultiple(nftDetails.properties.map(_.toProperty(fileHash))), Duration.Inf)
        Await.result(collectionsAnalysis.Utility.onNewNFT(uploadCollection.id), Duration.Inf)
        ""
      } catch {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
          ""
      }
    } else ""
  } else {
    logger.error(nftDetails.name + " has incorrect data properties of collection " + uploadCollection.name)
  }

  def uploadCollections(): Future[Unit] = {
    val uploadCollections = readFile[Seq[UploadCollection]](uploadCollectionFilePath)

    def addCollections(uploadCollections: Seq[UploadCollection]) = utilitiesOperations.traverse(uploadCollections) { uploadCollection =>

      val coverFileName = utilities.FileOperations.getFileHash(constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.cover) + "." + utilities.FileOperations.fileExtensionFromName(uploadCollection.cover)
      val coverAwsKey = utilities.Collection.getOthersFileAwsKey(collectionId = uploadCollection.id, fileName = coverFileName)
      utilities.AmazonS3.uploadFile(objectKey = coverAwsKey, filePath = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.cover)

      val profileFileName = utilities.FileOperations.getFileHash(constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.profile) + "." + utilities.FileOperations.fileExtensionFromName(uploadCollection.profile)
      val profileAwsKey = utilities.Collection.getOthersFileAwsKey(collectionId = uploadCollection.id, fileName = profileFileName)
      utilities.AmazonS3.uploadFile(objectKey = profileAwsKey, filePath = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.profile)

      val socialProfiles = Seq(SocialProfile(name = constants.Collection.SocialProfile.TWITTER, url = uploadCollection.twitter))
      val collection = Collection(id = uploadCollection.id, creatorId = uploadCollection.accountId, classificationId = None, name = uploadCollection.name, description = uploadCollection.description, socialProfiles = socialProfiles, category = constants.Collection.Category.ART, nsfw = false, properties = Option(uploadCollection.classificationProperties.map(_.toProperty)), profileFileName = Option(profileFileName), coverFileName = Option(coverFileName), public = true)

      val add = masterCollections.Service.add(collection)

      def analysis = collectionsAnalysis.Utility.onNewCollection(uploadCollection.id)

      for {
        _ <- add
        _ <- analysis
      } yield ()

    }

    def addCollectionNFTs(uploadCollections: Seq[UploadCollection]) = utilitiesOperations.traverse(uploadCollections) { uploadCollection =>
      println("START:     " + uploadCollection.name)
      val jsonFiles = getListOfFiles(constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.jsonPath)
      val addNFTs = utilitiesOperations.traverse(jsonFiles) { nftFile =>
        val nftDetails = readFile[NFT](nftFile)
        val nftImageFile: String = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.imagePath + "/" + nftFile.split("\\.").head.split("\\/").last + "." + uploadCollection.nftFormat
        println(nftImageFile)
        (for {
          nftDetails <- nftDetails
        } yield {
          addNft(nftDetails, uploadCollection, nftImageFile)
          ""
        }
          ).recover {
          case exception: Exception => logger.error(exception.getLocalizedMessage)
            Future("")
        }
      }

      for {
        _ <- addNFTs
      } yield ()
    }

    for {
      uploadCollections <- uploadCollections
      _ <- addCollections(uploadCollections)
      _ <- addCollectionNFTs(uploadCollections)
    } yield ()
  }

  def deleteCollections(): Future[Unit] = {
    val list = Seq("F15E9719C270B3F9", "CB9FFC37AB52AE1A", "C0D1BFC0E078E878", "B668A2AC9D071659", "A5645AA13D57E2B3")
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

  def fix(): Future[Unit] = {
    val updateMinte = masterCollections.Service.setPropertiesToEmpty()
    for {
      _ <- updateMinte
    } yield ()
  }



  // Set Mint.E properties to []

  // Delete redundant nft tags
  def start(): Unit = {
    (for {
      _ <- deleteCollections()
      _ <- uploadCollections()
      _ <- fix()
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}