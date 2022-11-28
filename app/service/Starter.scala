package service

import models.analytics.{CollectionAnalysis, CollectionsAnalysis}
import models.master.{Collection, NFTOwner}
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

  private val uploadCollectionDraftFilePath = constants.CommonConfig.Files.CollectionPath + "/upload.json"

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

      def update(totalNFTs: Int) = collectionsAnalysis.Service.add(CollectionAnalysis(id = collection.id, totalNFTs = totalNFTs, totalSold = 0, totalTraded = 0, floorPrice = 0, totalVolumeTraded = 0, bestOffer = 0, listed = 0, owners = 0, uniqueOwners = 0))

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

  def start(): Unit = {
    try {
      Await.result(correctNotifications(), Duration.Inf)
      Await.result(updateAccountType(), Duration.Inf)
      Await.result(updateCollectionAnalysis(), Duration.Inf)
      Await.result(addNFTOwners(), Duration.Inf)
    } catch {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}