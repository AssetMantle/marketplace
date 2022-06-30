package service

import exceptions.BaseException
import models.master
import models.master.{Collection, Property, SocialProfile}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsObject, JsPath, Json, Reads}
import play.api.{Configuration, Logger}

import java.io.File
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.{Source => ScalaSource}

@Singleton
class UploadCollections @Inject()(
                                   masterCollections: master.Collections,
                                   masterCollectionFiles: master.CollectionFiles,
                                   masterNFTs: master.NFTs,
                                   utilitiesOperations: utilities.Operations,
                                 )(implicit exec: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.UPLOAD_COLLECTION_SERVICE

  private implicit val logger: Logger = Logger(this.getClass)

  private val uploadCollectionFilePath = constants.CommonConfig.Files.ColectionPath + "/upload.json"

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

  case class UploadCollection(name: String, description: String, jsonPath: String, imagePath: String, website: String, uploadToIPFS: Boolean, downloadFromIPFS: Boolean, process: Boolean, profile: String, cover: String, twitter: String)

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

  def addNfts(collectionID: String, nftDetails: NFT, uploadCollection: UploadCollection): Future[Unit] = try {
    val ipfsDetails = nftDetails.image.split("/").takeRight(2)
    val ipfsHash = ipfsDetails(0)
    val fileName = ipfsDetails(1)
    val oldFilePath = constants.CommonConfig.Files.RootFilePath + uploadCollection.imagePath + "/" + fileName
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
    val newFilePath = constants.CommonConfig.Files.ColectionPath + "/" + awsKey
    println(awsKey)
    utilities.AmazonS3.uploadFile(awsKey, oldFilePath)
    utilities.FileOperations.renameFile(oldFilePath, newFilePath)
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
      throw new BaseException(constants.Response.FILE_UPLOAD_ERROR)
  }

  def addCollectionFiles(collectionID: String, uploadCollection: UploadCollection) = {
    val uploadProfile = if (uploadCollection.profile != "") {
      val oldFilePath = constants.CommonConfig.Files.ColectionPath + "/" + uploadCollection.profile
      val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
      val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(uploadCollection.profile)
      val awsKey = uploadCollection.name + "/others/" + newFileName
      val newFilePath = constants.CommonConfig.Files.ColectionPath + "/" + awsKey
      utilities.AmazonS3.uploadFile(awsKey, oldFilePath)
      utilities.FileOperations.renameFile(oldFilePath, newFilePath)
      masterCollectionFiles.Service.add(
        fileName = newFileName,
        file = utilities.ImageProcess.convertToThumbnailWithHeight(newFilePath),
        id = collectionID,
        documentType = constants.Collection.File.PROFILE,
      )
    } else Future()

    val uploadCover = if (uploadCollection.cover != "") {
      val oldFilePath = constants.CommonConfig.Files.ColectionPath + "/" + uploadCollection.cover
      val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
      val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(uploadCollection.cover)
      val awsKey = uploadCollection.name + "/others/" + newFileName
      val newFilePath = constants.CommonConfig.Files.ColectionPath + "/" + awsKey
      utilities.AmazonS3.uploadFile(awsKey, oldFilePath)
      utilities.FileOperations.renameFile(oldFilePath, newFilePath)
      masterCollectionFiles.Service.add(
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
  }

  def start(): Future[Unit] = {
    val uploads = readFile[Seq[UploadCollection]](uploadCollectionFilePath)

    def processDir(uploadCollections: Seq[UploadCollection]) = utilitiesOperations.traverse(uploadCollections) { uploadCollection =>
      val allCollections = masterCollections.Service.fetchAll()

      def addCollection(allCollections: Seq[Collection]): Future[Unit] = if (!allCollections.map(_.name).contains(uploadCollection.name)) {
        val collectionID = masterCollections.Service.add(name = uploadCollection.name, description = uploadCollection.description, website = uploadCollection.website, socialProfiles = if (uploadCollection.twitter != "") Seq(SocialProfile(name = constants.Collection.SocialProfile.TWITTER, url = uploadCollection.twitter)) else Seq())
        val collections = getListOfFiles(constants.CommonConfig.Files.RootFilePath + uploadCollection.jsonPath)

        def addCollectionNfts(collectionID: String) = utilitiesOperations.traverse(collections.take(6)) { filePath =>
          val nftDetails = readFile[NFT](filePath)

          (for {
            nftDetails <- nftDetails
            _ <- addNfts(collectionID, nftDetails, uploadCollection)
          } yield ()
            ).recover {
            case exception: Exception => logger.error(exception.getLocalizedMessage)
              throw new BaseException(constants.Response.COLLECTION_UPLOAD_ERROR)
          }
        }

        for {
          collectionID <- collectionID
          _ <- addCollectionFiles(collectionID, uploadCollection)
          _ <- addCollectionNfts(collectionID)
        } yield ()
      } else Future()

      for {
        allCollections <- allCollections
        collectionProcess <- addCollection(allCollections)
      } yield collectionProcess
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