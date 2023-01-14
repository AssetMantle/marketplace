package service

import models.analytics.CollectionsAnalysis
import models.common.{Collection => commonCollection, NFT => commonNFT}
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

  case class NFTProperty(name: String, `type`: String, `value`: String) {
    def toProperty: commonNFT.StringProperty = commonNFT.StringProperty(name = name, `value` = `value`, meta = true, mutable = false)
  }

  implicit val NFTPropertyReads: Reads[NFTProperty] = Json.reads[NFTProperty]

  case class UploadCollection(id: String, name: String, jsonPath: String, downloadFromIPFS: Boolean, imagePath: String, description: String, website: String, profile: String, cover: String, twitter: String, instagram: String, accountId: String)

  case class NFT(name: String, description: String, image: String, properties: Seq[NFTProperty])

  implicit val UploadCollectionReads: Reads[UploadCollection] = Json.reads[UploadCollection]

  implicit val NFTReads: Reads[NFT] = Json.reads[NFT]

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

    def deleteCollections() = masterCollections.Service.delete(list)

    for {
      nftIDs <- nftIDs
      _ <- deleteWishlist
      _ <- deleteNFTProperties(nftIDs)
      _ <- deleteNFTTags(nftIDs)
      _ <- deleteNFTDraft
      _ <- deleteAnalytics
      _ <- deleteNftOwners
      _ <- deleteNfts()
      _ <- deleteCollections()
    } yield ()
  }



  // Set Mint.E properties to []

  // Delete redundant collections
  def start(): Unit = {
    try {
      Await.result(deleteCollections(), Duration.Inf)
    } catch {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}