package service

import models.analytics.{CollectionAnalysis, CollectionsAnalysis}
import models.common.Collection.SocialProfile
import models.master.{Collection, NFT, NFTOwner}
import models.{blockchainTransaction, master, masterTransaction}
import play.api.libs.json.Reads
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
                         masterCollectionFiles: master.CollectionFiles,
                         masterNFTs: master.NFTs,
                         masterNFTOwners: master.NFTOwners,
                         masterNFTProperties: master.NFTProperties,
                         masterWishLists: master.WishLists,
                         masterTransactionNotifications: masterTransaction.Notifications,
                         utilitiesOperations: utilities.Operations,
                         blockchainTransactionSendCoins: blockchainTransaction.SendCoins
                       )(implicit exec: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.STARTER_SERVICE

  private implicit val logger: Logger = Logger(this.getClass)

  def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir)
    file.listFiles.filter(_.isFile)
      .map(_.getPath).toList
  }

  def readFile[T](path: String)(implicit reads: Reads[T]): Future[T] = Future {
    val source = ScalaSource.fromFile(path)
    val obj = utilities.JSON.convertJsonStringToObject[T](source.mkString)
    source.close()
    obj
  }

  private def updateAccountType() = {
    val collections = masterCollections.Service.fetchAll()

    def update(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      masterAccounts.Service.updateAccountToCreator(collection.creatorId)
    }

    (for {
      collections <- collections
      _ <- update(collections)
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

  private def updateCollectionAnalysis() = {
    val collections = masterCollections.Service.fetchAll()

    def updateAnalytics(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      val totalNFTs = masterNFTs.Service.countNFTs(collection.id)

      def update(totalNFTs: Int) = collectionsAnalysis.Service.add(CollectionAnalysis(id = collection.id, totalNFTs = totalNFTs, totalSold = 0, totalTraded = 0, floorPrice = 0, totalVolumeTraded = 0, bestOffer = 0, listed = 0, owners = 0, uniqueOwners = 0, totalMinted = 0, salePrice = 0))

      for {
        totalNFTs <- totalNFTs
        _ <- update(totalNFTs)
      } yield ()
    }

    (for {
      collections <- collections
      _ <- updateAnalytics(collections)
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

  private def addNFTOwners() = {
    val collections = masterCollections.Service.fetchAll()

    def updateNFTs(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      val nftIds = masterNFTs.Service.getAllIdsForCollection(collection.id)

      def update(nftIds: Seq[String]) = masterNFTOwners.Service.add(nftIds.map(x => NFTOwner(nftId = x, ownerId = collection.creatorId, creatorId = collection.creatorId, collectionId = collection.id, quantity = 1, saleId = None)))

      (for {
        nftIds <- nftIds
        _ <- update(nftIds)
      } yield ()
        ).recover {
        case exception: Exception =>
      }
    }

    (for {
      collections <- collections
      _ <- updateNFTs(collections)
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

  private def correctNotifications() = {
    val incorrectNotifications = masterTransactionNotifications.Service.getClickableNotifications

    def update(incorrectNotifications: Seq[masterTransaction.Notification]) = utilitiesOperations.traverse(incorrectNotifications) { notification =>
      val notif =
        if (notification.jsRoute.getOrElse("").contains("CollectionController.viewCollection(") && !notification.jsRoute.getOrElse("").contains("CollectionController.viewCollection('")) {
          val updatedRoute1 = notification.jsRoute.getOrElse("").split("\\(")
          val updatedRoute2 = updatedRoute1.last.split("\\)")
          val route = s"${updatedRoute1.head}('${updatedRoute2.head}')"
          masterTransactionNotifications.Service.update(notification.copy(jsRoute = Option(route)))
        } else if (notification.jsRoute.getOrElse("").contains("NFTController.viewNFT(") && !notification.jsRoute.getOrElse("").contains("NFTController.viewNFT('")) {
          val updatedRoute1 = notification.jsRoute.getOrElse("").split("\\(")
          val updatedRoute2 = updatedRoute1.last.split("\\.")
          val route = s"${updatedRoute1.head}('${updatedRoute2.head}')"
          masterTransactionNotifications.Service.update(notification.copy(jsRoute = Option(route)))
        } else {
          Future()
        }
      (for {
        _ <- notif
      } yield ()
        ).recover {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
      }
    }

    (for {
      incorrectNotifications <- incorrectNotifications
      _ <- update(incorrectNotifications)
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

  private def addChristmasNFT() = {
    val collectionName = "MintE Memoirs"
    val collectionDescription = "A collection to acknowledge and show appreciation to the Mantlers for being a part of the Mantle Community. A series of periodic NFT drops to commemorate milestones and celebrations."
    val nftFilePath = constants.CommonConfig.Files.CollectionPath + "/christmas.gif"
    val thumbnailFilePath = constants.CommonConfig.Files.CollectionPath + "/cover.png"
    val allAccountIds = masterAccounts.Service.getAllIds

    val creatorId = "Mint.E"
    val nftName = "Season Greetings"
    val nftDescription = ""
    val socialProfiles = Seq(
      SocialProfile(name = constants.Collection.SocialProfile.WEBSITE, url = "https://assetmantle.one/"),
      SocialProfile(name = constants.Collection.SocialProfile.TWITTER, url = "AssetMantle/"),
      SocialProfile(name = constants.Collection.SocialProfile.INSTAGRAM, url = "assetmantle")
    )
    val newCoverFileName = utilities.FileOperations.getFileHash(thumbnailFilePath) + ".png"
    val collection = Collection(id = "D4C3FD5554AEDB64", creatorId = creatorId, classificationId = None, name = collectionName, description = collectionDescription, socialProfiles = socialProfiles, category = constants.Collection.Category.ART, nsfw = false, properties = None, profileFileName = None, coverFileName = Option(newCoverFileName), public = false)
    val nftId = utilities.FileOperations.getFileHash(nftFilePath)
    val fileExtension = "gif"
    val newNFTFileName = nftId + "." + fileExtension
    val nftAWSKey = utilities.Collection.getNFTFileAwsKey(collectionId = collection.id, fileName = newNFTFileName)
    val coverAWSKey = utilities.Collection.getOthersFileAwsKey(collectionId = collection.id, fileName = newCoverFileName)

    def addCollection() = masterCollections.Service.add(collection)

    def uploadNFTToAws = Future(utilities.AmazonS3.uploadFile(objectKey = nftAWSKey, filePath = nftFilePath))

    def uploadCoverToAws = Future(utilities.AmazonS3.uploadFile(objectKey = coverAWSKey, filePath = thumbnailFilePath))

    def addNFT(accountIds: Seq[String]) = masterNFTs.Service.add(NFT(id = nftId, collectionId = collection.id, name = nftName, description = nftDescription, totalSupply = accountIds.length, isMinted = false, fileExtension = fileExtension, ipfsLink = "", edition = None))

    def addNFTOwners(accountIds: Seq[String]) = masterNFTOwners.Service.add(accountIds.map(x => NFTOwner(nftId = nftId, ownerId = x, creatorId = creatorId, collectionId = collection.id, quantity = 1, saleId = None)))

    def addAnalytics(accountIds: Seq[String]) = collectionsAnalysis.Service.add(CollectionAnalysis(id = collection.id, totalNFTs = 1, totalMinted = 0, totalSold = 0, totalTraded = 0, floorPrice = 0, salePrice = 0, totalVolumeTraded = 0, bestOffer = 0, listed = 0, owners = accountIds.length, uniqueOwners = accountIds.length))

    def addNotification(allAccountIds: Seq[String]) = masterTransactionNotifications.Service.add(accountIDs = allAccountIds, notification = constants.Notification.NFT_GIFTED, nftName)(s"'${nftId}'")

    (for {
      allAccountIds <- allAccountIds
      _ <- addCollection()
      _ <- uploadNFTToAws
      _ <- uploadCoverToAws
      _ <- addNFT(allAccountIds)
      _ <- addNFTOwners(allAccountIds)
      _ <- addAnalytics(allAccountIds)
      _ <- addNotification(allAccountIds)
    } yield ()
      ).recover {
      case exception: Exception => throw exception
    }
  }

  def start(): Unit = {
    try {
      Await.result(addChristmasNFT(), Duration.Inf)
      Await.result(correctNotifications(), Duration.Inf)
      Await.result(updateAccountType(), Duration.Inf)
      Await.result(updateCollectionAnalysis(), Duration.Inf)
      Await.result(addNFTOwners(), Duration.Inf)
    } catch {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}