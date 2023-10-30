package utilities

import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import org.apache.commons.io.FileUtils
import play.api.Logger
import views.base.companion.UploadFile.UploadFile

import java.io._
import java.nio.file.InvalidPathException
import java.security.{DigestInputStream, MessageDigest, Permission}
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object FileOperations {

  implicit val module: String = constants.Module.UTILITIES_FILE_OPERATIONS

  private val uploadedParts: ConcurrentMap[String, Set[UploadFile]] = new ConcurrentHashMap(8, 0.9f, 1)

  implicit val logger: Logger = Logger(this.getClass)

  System.setSecurityManager(new SecurityManager() {

    override def checkPermission(perm: Permission): Unit = {
    }

    override def checkPermission(perm: Permission, context: Object): Unit = {
    }
  })

  def savePartialFile(filePart: Array[Byte], fileInfo: UploadFile, uploadPath: String): Set[UploadFile] = {
    try {
      val fullFileName = uploadPath + fileInfo.resumableFilename
      val partialFile = new RandomAccessFile(new File(fullFileName), "rw")
      try {
        partialFile.seek((fileInfo.resumableChunkNumber - 1) * fileInfo.resumableChunkSize.toLong)
        partialFile.write(filePart, 0, filePart.length)
      } catch {
        case ioException: IOException => logger.error(ioException.getMessage)
          constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
        case e: Exception => logger.error(e.getMessage)
          constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
      } finally {
        partialFile.close()
      }
      if (uploadedParts.containsKey(fullFileName)) {
        val partsUploaded = uploadedParts.get(fullFileName)
        uploadedParts.put(fullFileName, partsUploaded + fileInfo)
      } else {
        uploadedParts.put(fullFileName, Set(fileInfo))
      }
    } catch {
      case illegalArgumentException: IllegalArgumentException => logger.error(illegalArgumentException.getMessage)
        constants.Response.FILE_ILLEGAL_ARGUMENT_EXCEPTION.throwBaseException(illegalArgumentException)
      case fileNotFoundException: FileNotFoundException => logger.error(fileNotFoundException.getMessage)
        constants.Response.FILE_NOT_FOUND_EXCEPTION.throwBaseException(fileNotFoundException)
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case ioException: IOException => logger.error(ioException.getMessage)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case nullPointerException: NullPointerException => logger.error(nullPointerException.getMessage)
        constants.Response.NULL_POINTER_EXCEPTION.throwBaseException(nullPointerException)
      case classCastException: ClassCastException => logger.error(classCastException.getMessage)
        constants.Response.CLASS_CAST_EXCEPTION.throwBaseException(classCastException)
      case unsupportedOperationException: UnsupportedOperationException => logger.error(unsupportedOperationException.getMessage)
        constants.Response.FILE_UNSUPPORTED_OPERATION_EXCEPTION.throwBaseException(unsupportedOperationException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }
  }

  def fileExtensionFromName(fileName: String): String = {
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) fileName.substring(fileName.lastIndexOf(".") + 1)
    else ""
  }

  def renameFile(oldFilePath: String, newFilePath: String)(implicit executionContext: ExecutionContext): Boolean =
    try {
      new File(oldFilePath).renameTo(new File(newFilePath))
    } catch {
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case nullPointerException: NullPointerException => logger.error(nullPointerException.getMessage)
        constants.Response.NULL_POINTER_EXCEPTION.throwBaseException(nullPointerException)
      case ioException: IOException => logger.error(ioException.getMessage)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }

  def copyFile(oldFilePath: String, newFilePath: String)(implicit executionContext: ExecutionContext): Unit =
    try {
      FileUtils.copyFile(new File(oldFilePath), new File(newFilePath))
    } catch {
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case nullPointerException: NullPointerException => logger.error(nullPointerException.getMessage)
        constants.Response.NULL_POINTER_EXCEPTION.throwBaseException(nullPointerException)
      case ioException: IOException => logger.error(ioException.getMessage)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }

  def fileStreamer(file: File, filePath: String)(implicit executionContext: ExecutionContext): Source[ByteString, _] =
    try {
      val source: Source[ByteString, _] = FileIO.fromPath(file.toPath)
        .watchTermination()((_, downloadDone) => downloadDone.onComplete {
          case Success(_) => deleteFile(filePath)
          case Failure(t) => logger.error(t.getMessage, t)
            deleteFile(filePath)
        })
      source
    } catch {
      case invalidPathException: InvalidPathException => logger.error(invalidPathException.getMessage)
        constants.Response.INVALID_FILE_PATH_EXCEPTION.throwBaseException(invalidPathException)
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case nullPointerException: NullPointerException => logger.error(nullPointerException.getMessage)
        constants.Response.NULL_POINTER_EXCEPTION.throwBaseException(nullPointerException)
      case ioException: IOException => logger.error(ioException.getMessage)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }

  def deleteFile(filePath: String)(implicit executionContext: ExecutionContext): Boolean = {
    try {
      new File(filePath).delete()
    } catch {
      case ioException: IOException => logger.error(ioException.getMessage)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }
  }

  def moveFile(oldFilePath: String, newFilePath: String)(implicit executionContext: ExecutionContext): Boolean =
    try {
      new File(oldFilePath).renameTo(new File(newFilePath))
    } catch {
      case ioException: IOException => logger.error(ioException.getMessage)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }

  // Warning: Will fail for file size greater than 2147483647 Bytes
  def convertToByteArray(file: File)(implicit executionContext: ExecutionContext): Array[Byte] = {
    try {
      val fileInputStreamReader = new FileInputStream(file)
      val bytes = new Array[Byte](file.length.asInstanceOf[Int])
      fileInputStreamReader.read(bytes)
      fileInputStreamReader.close()
      bytes
    } catch {
      case fileNotFoundException: FileNotFoundException => logger.error(fileNotFoundException.getMessage)
        constants.Response.FILE_NOT_FOUND_EXCEPTION.throwBaseException(fileNotFoundException)
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case ioException: IOException => logger.info(constants.Response.I_O_EXCEPTION.message, ioException)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }
  }

  def fetchFile(path: String, fileName: String): File = {
    val file = new java.io.File(path + fileName)
    if (!file.exists) constants.Response.NO_SUCH_FILE_EXCEPTION.throwBaseException() else file
  }

  def getFileNameWithoutExtension(fileName: String): String = fileName.split("""\.""")(0)

  def getFileHash(filePath: String): String = {
    val buffer = new Array[Byte](10 * 1024 * 1024)
    val sha256 = MessageDigest.getInstance("SHA-256")

    try {
      val dis = new DigestInputStream(new FileInputStream(new File(filePath)), sha256)
      try {
        while (dis.read(buffer) != -1) {}
      } finally {
        dis.close()
      }
      sha256.digest.map("%02x".format(_)).mkString
    } catch {
      case nullPointerException: NullPointerException => logger.error(nullPointerException.getMessage)
        constants.Response.FILE_NOT_FOUND_EXCEPTION.throwBaseException(nullPointerException)
      case fileNotFoundException: FileNotFoundException => logger.error(fileNotFoundException.getMessage)
        constants.Response.FILE_NOT_FOUND_EXCEPTION.throwBaseException(fileNotFoundException)
      case securityException: SecurityException => logger.error(securityException.getMessage)
        constants.Response.FILE_SECURITY_EXCEPTION.throwBaseException(securityException)
      case ioException: IOException => logger.info(constants.Response.I_O_EXCEPTION.message, ioException)
        constants.Response.I_O_EXCEPTION.throwBaseException(ioException)
      case e: Exception => logger.error(e.getMessage)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }
  }

  def checkAndCreateDirectory(path: String): String = try {
    val directory = new File(path)
    if (directory.exists()) {
      path
    } else {
      if (directory.mkdirs()) path
      else constants.Response.DIRECTORY_CREATION_FAILED.throwBaseException()
    }
  } catch {
    case exception: Exception => constants.Response.DIRECTORY_CREATION_FAILED.throwBaseException(exception)
  }

}