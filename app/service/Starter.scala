package service

import models.analytics.{CollectionAnalysis, CollectionsAnalysis}
import models.master
import models.master.Collection
import play.api.libs.json.Reads
import play.api.{Configuration, Logger}

import java.io.File
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.{Source => ScalaSource}

@Singleton
class Starter @Inject()(
                         collectionsAnalysis: CollectionsAnalysis,
                         masterAccounts: master.Accounts,
                         masterCollections: master.Collections,
                         masterCollectionFiles: master.CollectionFiles,
                         masterNFTs: master.NFTs,
                         masterNFTProperties: master.NFTProperties,
                         masterWishLists: master.WishLists,
                         utilitiesOperations: utilities.Operations,
                       )(implicit exec: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.STARTER_SERVICE

  private implicit val logger: Logger = Logger(this.getClass)

  private val uploadCollectionFilePath = constants.CommonConfig.Files.CollectionPath + "/upload.json"

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

  private def updateCollectionAnalysis() = {
    val collections = masterCollections.Service.fetchAll()

    def updateAnalytics(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      val totalNFTs = masterNFTs.Service.countNFTs(collection.id)

      def update(totalNFTs: Int) = collectionsAnalysis.Service.add(CollectionAnalysis(id = collection.id, totalNFTs = totalNFTs, totalSold = 0, totalTraded = 0, floorPrice = 0, totalVolume = 0, bestOffer = 0, listed = 0, owners = 0, uniqueOwners = 0))

      for {
        totalNFTs <- totalNFTs
        _ <- update(totalNFTs)
      } yield ()
    }

    for {
      collections <- collections
      _ <- updateAnalytics(collections)
    } yield ()
  }

  def start(): Future[Unit] = {

    (for {
      _ <- updateCollectionAnalysis()
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}