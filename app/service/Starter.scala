package service

import models.common.NFT._
import models.master
import models.master.{Collection, CollectionFile}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsObject, JsPath, Json, Reads}
import play.api.{Configuration, Logger}

import java.io.File
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.{Source => ScalaSource}

@Singleton
class Starter @Inject()(
                         masterAccounts: master.Accounts,
                         masterCollections: master.Collections,
                         masterCollectionFiles: master.CollectionFiles,
                         masterNFTs: master.NFTs,
                         masterWishLists: master.WishLists,
                         utilitiesOperations: utilities.Operations,
                       )(implicit exec: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.STARTER_SERVICE

  private implicit val logger: Logger = Logger(this.getClass)

  private val uploadCollectionFilePath = constants.CommonConfig.Files.CollectionPath + "/upload.json"

  abstract class NftProperty {
    def toStringProperty: StringProperty
  }

  case class StringProperty(name: String, `type`: String, `value`: String) extends NftProperty {
    def toStringProperty: StringProperty = this

    def toProperty: Property = Property(name = name, `type` = `type`, `value` = `value`)
  }

  implicit val StringPropertyReads: Reads[StringProperty] = Json.reads[StringProperty]

  case class NumberProperty(name: String, `type`: String, `value`: Int) extends NftProperty {
    def toStringProperty: StringProperty = StringProperty(name = name, `type` = constants.NFT.Data.DECIMAL, `value` = `value`.toString)
  }

  implicit val NumberPropertyReads: Reads[NumberProperty] = Json.reads[NumberProperty]

  def msgApply(`type`: String, value: JsObject): NftProperty = try {
    `type`.toLowerCase match {
      case "string" => utilities.JSON.convertJsonStringToObject[StringProperty](value.toString).copy(`type` = constants.NFT.Data.STRING)
      case "number" => utilities.JSON.convertJsonStringToObject[NumberProperty](value.toString).toStringProperty
    }
  }

  implicit val msgReads: Reads[NftProperty] = (
    (JsPath \ "type").read[String] and
      JsPath.read[JsObject]
    ) (msgApply _)

  case class UploadCollection(name: String, accountId: String, description: String, jsonPath: String, imagePath: String, website: String, uploadToIPFS: Boolean, downloadFromIPFS: Boolean, profile: String, cover: String, twitter: String, instagram: String, updateDetails: Boolean, imageFormat: String)

  case class NFT(name: String, description: String, image: String, properties: Seq[NftProperty], edition: Option[Int] = None)

  implicit val UploadCollectionReads: Reads[UploadCollection] = Json.reads[UploadCollection]

  implicit val NFTReads: Reads[NFT] = Json.reads[NFT]

  case class UpdateAccountId(id: String, description: String, twitter: String, website: String, instagram: String, accountId: String)

  implicit val UpdateAccountIdReads: Reads[UpdateAccountId] = Json.reads[UpdateAccountId]

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

  private def updateCollectionAwsFiles() = {
    val collectionFiles = masterCollectionFiles.Service.fetchAll()
    val collections = masterCollections.Service.fetchAll()

    def update(collectionFiles: Seq[CollectionFile], collections: Seq[Collection]): Unit = collectionFiles.foreach { collectionFile =>
      try {
        val sourceKey = collections.find(_.id == collectionFile.id).fold("")(_.name) + "/others/" + collectionFile.fileName
        val destinationKey = collectionFile.id + "/others/" + collectionFile.fileName
        if (!utilities.AmazonS3.exists(destinationKey)) {
          utilities.AmazonS3.copyObject(sourceKey = sourceKey, destinationKey = destinationKey)
        }
      } catch {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
      }
    }

    for {
      collectionFiles <- collectionFiles
      collections <- collections
    } yield update(collectionFiles, collections)

  }

  private def updateCollectionNFTAwsFiles() = {
    val collections = masterCollections.Service.fetchAll()

    def update(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      val nfts = masterNFTs.Service.getAllForCollection(collection.id)

      for {
        nfts <- nfts
      } yield {
        nfts.foreach { nft =>
          try {
            val sourceKey = collections.find(_.id == nft.collectionId).fold("")(_.name) + "/nfts/" + nft.fileName
            val destinationKey = nft.collectionId + "/nfts/" + nft.fileName
            //            println(destinationKey)
            if (!utilities.AmazonS3.exists(destinationKey)) {
              utilities.AmazonS3.copyObject(sourceKey = sourceKey, destinationKey = destinationKey)
            }
          } catch {
            case exception: Exception => logger.error(exception.getLocalizedMessage)
          }
        }
      }
    }

    for {
      collections <- collections
    } yield update(collections)

  }

  private def updateNFTProperties() = {
    val collections = masterCollections.Service.fetchAll()

    def update(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      println(collection.name)
      println(collection.id)
      val nfts = masterNFTs.Service.getAllForCollection(collection.id)

      def updateNFTs(nfts: Seq[master.NFT]) = {
        println(nfts.length)
        utilitiesOperations.traverse(nfts)(nft => masterNFTs.Service.updateBaseNFTProperty(nft))
      }

      for {
        nfts <- nfts
        _ <- updateNFTs(nfts)
      } yield ()
    }


    for {
      collections <- collections
      _ <- update(collections)
    } yield ()
  }

  private def updateSocialURLs() = {
    val collections = masterCollections.Service.fetchAll()

    def update(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      masterCollections.Service.updateById(collection.copy(socialProfiles = collection.socialProfiles.map(x => x.copy(url = x.url.split("/").last))))
    }

    for {
      collections <- collections
      _ <- update(collections)
    } yield ()
  }

  def start(): Future[Unit] = {

    (for {
      _ <- updateNFTProperties()
      _ <- updateCollectionAwsFiles()
      _ <- updateCollectionNFTAwsFiles()
      _ <- updateSocialURLs()
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}