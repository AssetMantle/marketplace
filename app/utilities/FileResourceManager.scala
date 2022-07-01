package utilities

import exceptions.BaseException
import models.Trait.Document
import play.api.i18n.{Lang, MessagesApi}
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileResourceManager @Inject()(messagesApi: MessagesApi)(implicit executionContext: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.UTILITIES_FILE_RESOURCE_MANAGER

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val lang: Lang = Lang(configuration.get[String]("play.log.lang"))

  private val rootFilePath = configuration.get[String]("upload.rootFilePath")

  def storeFile[T <: Document[T]](name: String, path: String, document: T, masterCreate: T => Future[String]): Future[Unit] = {
    val oldFilePath = path + "/" + name
    val newFileName = FileOperations.getFileHash(oldFilePath) + "." + FileOperations.fileExtensionFromName(name)
    val newFilePath = path + "/" + newFileName
    logger.info(messagesApi(constants.Log.Info.STORE_FILE_ENTRY, name, document.documentType, path))
    val encodedBase64: Future[Option[Array[Byte]]] = Future {
      utilities.FileOperations.fileExtensionFromName(name) match {
        case constants.File.JPEG | constants.File.JPG | constants.File.PNG | constants.File.JPEG_LOWER_CASE | constants.File.JPG_LOWER_CASE | constants.File.PNG_LOWER_CASE => Option(utilities.ImageProcess.convertToThumbnailWithHeight(oldFilePath))
        case _ => None
      }
    }

    def updateAndCreateFile(encodedBase64: Option[Array[Byte]]): Future[String] = masterCreate(document.updateFileName(newFileName).updateFile(encodedBase64))

    (for {
      encodedBase64 <- encodedBase64
      _ <- updateAndCreateFile(encodedBase64)
    } yield {
      utilities.FileOperations.renameFile(oldFilePath, newFilePath)
      logger.info(messagesApi(constants.Log.Info.STORE_FILE_EXIT, name, document.documentType, path))
    }
      ).recover {
      case baseException: BaseException => logger.error(baseException.failure.message)
        utilities.FileOperations.deleteFile(oldFilePath)
        throw new BaseException(constants.Response.FILE_UPLOAD_ERROR)
      case e: Exception => logger.error(e.getMessage)
        utilities.FileOperations.deleteFile(oldFilePath)
        throw new BaseException(constants.Response.GENERIC_EXCEPTION)
    }
  }

  def updateFile[T <: Document[T]](name: String, path: String, oldDocument: T, updateOldDocument: T => Future[Int]): Future[Boolean] = {
    val oldFilePath = path + "/" + oldDocument.fileName
    val newUpdateFileName = FileOperations.getFileHash(oldFilePath) + "." + FileOperations.fileExtensionFromName(name)
    val newUpdateFilePath = path + "/" + newUpdateFileName
    val encodedBase64: Future[Option[Array[Byte]]] = Future {
      utilities.FileOperations.fileExtensionFromName(name) match {
        case constants.File.JPEG | constants.File.JPG | constants.File.PNG | constants.File.JPEG_LOWER_CASE | constants.File.JPG_LOWER_CASE | constants.File.PNG_LOWER_CASE =>
          Option(utilities.ImageProcess.convertToThumbnailWithHeight(newUpdateFilePath))
        case _ => None
      }
    }

    def update(encodedBase64: Option[Array[Byte]]): Future[Int] = updateOldDocument(oldDocument.updateFileName(newUpdateFileName).updateFile(encodedBase64))

    (for {
      encodedBase64 <- encodedBase64
      _ <- update(encodedBase64)
    } yield {
      utilities.FileOperations.deleteFile(oldFilePath)
      utilities.FileOperations.renameFile(oldFilePath, newUpdateFilePath)
    }).recover {
      case baseException: BaseException => logger.error(baseException.failure.message)
        utilities.FileOperations.deleteFile(newUpdateFilePath)
        throw new BaseException(constants.Response.FILE_UPLOAD_ERROR)
      case e: Exception => logger.error(e.getMessage)
        utilities.FileOperations.deleteFile(newUpdateFilePath)
        throw new BaseException(constants.Response.GENERIC_EXCEPTION)
    }
  }
}