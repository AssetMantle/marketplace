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

  case class UploadCollection(id: String, jsonPath: String, downloadFromIPFS: Boolean, imagePath: String, nftFormat: String, accountId: String)

  case class NFT(name: String, description: String, image: String, properties: Seq[NFTProperty])

  implicit val UploadCollectionReads: Reads[UploadCollection] = Json.reads[UploadCollection]

  implicit val NFTReads: Reads[NFT] = Json.reads[NFT]

  def verifyNFT(nft: NFT, collection: Collection): Boolean = {
    if (collection.properties.isEmpty && nft.properties.isEmpty) true
    else {
      val propertiesName = collection.properties.get.map(_.name)
      nft.properties.map(x => propertiesName.contains(x.name)).forall(identity) && nft.properties.length == collection.properties.get.length
    }
  }

  private def addNft(nftDetails: NFT, uploadCollection: UploadCollection, nftImageFileName: String, collection: Collection) = if (verifyNFT(nftDetails, collection)) {
    val nftImageFile = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.imagePath + "/" + nftImageFileName
    println(nftImageFile)
    if (uploadCollection.downloadFromIPFS) {
      val fileUrl = nftDetails.image.split("\\/").drop(4).mkString("/")
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
    logger.error("incorrect nft: " + nftDetails.name)
    ""
  }

  def uploadCollections(): Future[Unit] = {
    val uploadCollections = readFile[Seq[UploadCollection]](uploadCollectionFilePath)

    def addCollectionNFTs(uploadCollections: Seq[UploadCollection]) = utilitiesOperations.traverse(uploadCollections) { uploadCollection =>
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
    }

    for {
      uploadCollections <- uploadCollections
      _ <- addCollectionNFTs(uploadCollections)
    } yield ()
  }

  def addValentineNFTs(): Future[Unit] = {
    val socialProfiles = Seq(
      SocialProfile(name = constants.Collection.SocialProfile.WEBSITE, url = "https://assetmantle.one/"),
      SocialProfile(name = constants.Collection.SocialProfile.INSTAGRAM, url = "assetmantle"),
      SocialProfile(name = constants.Collection.SocialProfile.TWITTER, url = "AssetMantle")
    )
    val creatorID = "Mint.E"
    case class ValentineNFT(fileName: String, name: String, description: String, format: String)
    val nfts = Seq(
      ValentineNFT(fileName = "1.jpeg", name = "name1", description = "description1", format = "jpeg"),
      ValentineNFT(fileName = "2.jpeg", name = "name2", description = "description2", format = "jpeg"),
      ValentineNFT(fileName = "3.jpeg", name = "name3", description = "description3", format = "jpeg"),
      ValentineNFT(fileName = "4.jpeg", name = "name4", description = "description4", format = "jpeg"),
      ValentineNFT(fileName = "5.jpeg", name = "name5", description = "description5", format = "jpeg"),
      ValentineNFT(fileName = "6.jpeg", name = "name6", description = "description6", format = "jpeg"),
      ValentineNFT(fileName = "7.jpeg", name = "name7", description = "description7", format = "jpeg"),
      ValentineNFT(fileName = "8.jpeg", name = "name8", description = "description8", format = "jpeg"),
      ValentineNFT(fileName = "9.jpeg", name = "name9", description = "description9", format = "jpeg"),
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
      val allocateTo = if (nftsDistributed == 8) {
        allUsernames.diff(allocatedUsernames)
      } else {
        util.Random.shuffle(allUsernames.diff(allocatedUsernames)).take(allUsernames.length / nfts.length)
      }
      println(allocateTo.length)
      println(allocatedUsernames.intersect(allocateTo).length)
      allocatedUsernames = allocatedUsernames ++ allocateTo
      println(allocatedUsernames.length)
      try {
        val awsKey = utilities.Collection.getNFTFileAwsKey(collectionId = collection.id, fileName = newFileName)
        utilities.AmazonS3.uploadFile(awsKey, nftImageFile)
        Await.result(masterNFTs.Service.add(master.NFT(id = fileHash, collectionId = collection.id, name = valentineNFT.name, description = valentineNFT.description, totalSupply = allocateTo.length, isMinted = false, fileExtension = valentineNFT.format, ipfsLink = "", edition = None)), Duration.Inf)
        Await.result(masterNFTOwners.Service.add(allocateTo.map(x => master.NFTOwner(nftId = fileHash, ownerId = x, creatorId = creatorID, collectionId = collection.id, quantity = 1, saleId = None, publicListingId = None))), Duration.Inf)
        Await.result(collectionsAnalysis.Utility.onNewNFT(collection.id), Duration.Inf)
        nftsDistributed = nftsDistributed + 1
        println(valentineNFT.name + " done")
      } catch {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
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
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}