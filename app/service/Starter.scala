package service

import exceptions.BaseException
import models.master
import models.master.{Collection, Property, SocialProfile}
import play.api.i18n.Lang
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsObject, JsPath, Json, Reads}
import play.api.{Configuration, Logger}

import java.io.File
import javax.inject.{Inject, Singleton}
import scala.concurrent
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.{Source => ScalaSource}

@Singleton
class Starter @Inject()(
                         masterAccounts: master.Accounts,
                         masterCollections: master.Collections,
                         masterCollectionFiles: master.CollectionFiles,
                         masterNFTs: master.NFTs,
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
    def toStringProperty: StringProperty = StringProperty(name = name, `type` = constants.Collection.NFT.Data.NUMBER, `value` = `value`.toString)
  }

  implicit val NumberPropertyReads: Reads[NumberProperty] = Json.reads[NumberProperty]

  def msgApply(`type`: String, value: JsObject): NftProperty = try {
    `type` match {
      case "String" => utilities.JSON.convertJsonStringToObject[StringProperty](value.toString).copy(`type` = constants.Collection.NFT.Data.STRING)
      case "number" => utilities.JSON.convertJsonStringToObject[NumberProperty](value.toString).toStringProperty
    }
  }

  implicit val msgReads: Reads[NftProperty] = (
    (JsPath \ "type").read[String] and
      JsPath.read[JsObject]
    ) (msgApply _)

  case class UploadCollection(name: String, description: String, jsonPath: String, imagePath: String, website: String, uploadToIPFS: Boolean, downloadFromIPFS: Boolean, process: Boolean, profile: String, cover: String, twitter: String, instagram: String, updateDetails: Boolean)

  case class NFT(name: String, description: String, image: String, properties: Seq[NftProperty], edition: Option[Int] = None)

  implicit val UploadCollectionReads: Reads[UploadCollection] = Json.reads[UploadCollection]

  implicit val NFTReads: Reads[NFT] = Json.reads[NFT]

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

  def addNfts(collectionID: String, nftDetails: NFT, uploadCollection: UploadCollection) = {
    println(nftDetails.name)
    println(uploadCollection.name)
    if (!Await.result(masterNFTs.Service.checkExistsByName(nftDetails.name), Duration.Inf)) {
      try {
        val ipfsDetails = nftDetails.image.split("/").takeRight(2)
        val ipfsHash = ipfsDetails(0)
        //        val fileName = ipfsDetails(1)
        val fileName = nftDetails.name + ".png"
        val oldFilePath = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.imagePath + "/" + fileName
        val ipfsPath = if (uploadCollection.uploadToIPFS) {
          val file = new File(oldFilePath)
          val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
          val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(fileName)
          utilities.IPFS.pinFile(file, newFileName).IpfsHash
        } else ipfsHash + "/" + fileName
        if (uploadCollection.downloadFromIPFS) {
          utilities.IPFS.downloadFile(ipfsHash + "/" + fileName, oldFilePath)
        }
        val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
        val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(fileName)
        val awsKey = uploadCollection.name + "/nfts/" + newFileName
        val newFilePath = constants.CommonConfig.Files.CollectionPath + "/" + awsKey
        utilities.AmazonS3.uploadFile(awsKey, oldFilePath)
        utilities.FileOperations.copyFile(oldFilePath, newFilePath)
        masterNFTs.Service.add(
          fileName = newFileName,
          file = utilities.ImageProcess.convertToThumbnailWithHeight(newFilePath, 400),
          collectionId = collectionID,
          name = nftDetails.name,
          description = nftDetails.description,
          properties = nftDetails.properties.map(_.toStringProperty.toProperty),
          ipfsLink = ipfsPath,
          edition = nftDetails.edition
        )
      } catch {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
          //          throw new BaseException(constants.Response.FILE_UPLOAD_ERROR)
          Future("")
      }
    } else Future("")
  }

  def addCollectionFiles(collectionID: String, uploadCollection: UploadCollection): Future[Unit] = if (uploadCollection.updateDetails) {
    val uploadProfile = if (uploadCollection.profile != "") {
      val oldFilePath = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.profile
      val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
      val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(uploadCollection.profile)
      val awsKey = uploadCollection.name + "/others/" + newFileName
      val newFilePath = constants.CommonConfig.Files.CollectionPath + "/" + awsKey
      utilities.AmazonS3.uploadFile(awsKey, oldFilePath)
      utilities.FileOperations.copyFile(oldFilePath, newFilePath)
      masterCollectionFiles.Service.insertOrUpdate(
        fileName = newFileName,
        file = utilities.ImageProcess.convertToThumbnailWithHeight(newFilePath, 200),
        id = collectionID,
        documentType = constants.Collection.File.PROFILE,
      )
    } else Future()

    val uploadCover = if (uploadCollection.cover != "") {
      val oldFilePath = constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.cover
      val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
      val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(uploadCollection.cover)
      val awsKey = uploadCollection.name + "/others/" + newFileName
      val newFilePath = constants.CommonConfig.Files.CollectionPath + "/" + awsKey
      utilities.AmazonS3.uploadFile(awsKey, oldFilePath)
      utilities.FileOperations.copyFile(oldFilePath, newFilePath)
      masterCollectionFiles.Service.insertOrUpdate(
        fileName = newFileName,
        file = utilities.ImageProcess.convertToThumbnailWithWidth(newFilePath, 400),
        id = collectionID,
        documentType = constants.Collection.File.COVER,
      )
    } else Future()
    for {
      _ <- uploadProfile
      _ <- uploadCover
    } yield ()
  } else Future()

  def start(): Future[Unit] = {
    val uploads = readFile[Seq[UploadCollection]](uploadCollectionFilePath)

    def processDir(uploadCollections: Seq[UploadCollection]) = utilitiesOperations.traverse(uploadCollections) { uploadCollection =>

      val collection = masterCollections.Service.getByName(name = uploadCollection.name)

      def addCollection(uploadCollection: UploadCollection, collection: Option[Collection]) = {
        val twitter = if (uploadCollection.twitter != "") Option(SocialProfile(name = constants.Collection.SocialProfile.TWITTER, url = uploadCollection.twitter)) else None
        val insta = if (uploadCollection.instagram != "") Option(SocialProfile(name = constants.Collection.SocialProfile.INSTAGRAM, url = uploadCollection.instagram)) else None
        val socialProfiles = Seq(twitter, insta).flatten
        if (collection.isEmpty) {
          masterCollections.Service.add(name = uploadCollection.name, description = uploadCollection.description, website = uploadCollection.website, socialProfiles = socialProfiles)
        } else if (uploadCollection.updateDetails) {
          masterCollections.Service.insertOrUpdate(id = collection.get.id, name = uploadCollection.name, description = uploadCollection.description, website = uploadCollection.website, socialProfiles = socialProfiles)
        } else Future(collection.get.id)
      }

      val collections = getListOfFiles(constants.CommonConfig.Files.CollectionPath + "/" + uploadCollection.jsonPath)

      def addCollectionNfts(collectionId: String) = utilitiesOperations.traverse(collections) { filePath =>
        val nftDetails = readFile[NFT](filePath)

        (for {
          nftDetails <- nftDetails
          _ <- addNfts(collectionId, nftDetails, uploadCollection)
        } yield ()
          ).recover {
          case exception: Exception => logger.error(exception.getLocalizedMessage)
            Future()
        }
      }

      for {
        collection <- collection
        collectionId <- addCollection(uploadCollection, collection)
        _ <- addCollectionFiles(collectionId, uploadCollection)
        _ <- addCollectionNfts(collectionId)
      } yield ()

    }

    (for {
      uploads <- uploads
      _ <- processDir(uploads)
    } yield ()
      ).recover {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
    }
  }

}