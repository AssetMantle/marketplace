package utilities

import com.sksamuel.scrimage
import com.sksamuel.scrimage.nio.JpegWriter
import play.api.Logger

import java.io.File
import java.util.Base64
import javax.imageio.ImageIO
import scala.concurrent.ExecutionContext


object ImageProcess {

  implicit val module: String = constants.Module.UTILITIES_IMAGE_PROCESS

  implicit val logger: Logger = Logger(this.getClass)

  def convertToThumbnailWithWidth(filePath: String, canvasWidth: Int = 300)(implicit executionContext: ExecutionContext): Array[Byte] = {
    try {
      val file = new File(filePath: String)
      val imageRes = ImageIO.read(file)
      val writer: JpegWriter = JpegWriter.compression(100)
      val bytes = FileOperations.convertToByteArray(scrimage.ImmutableImage.loader().fromFile(file).fit(canvasWidth, (canvasWidth * imageRes.getHeight) / imageRes.getWidth).output(writer, file))
      Base64.getEncoder.encode(bytes)
    } catch {
      case noSuchElementException: NoSuchElementException => logger.info(constants.Response.NO_SUCH_ELEMENT_EXCEPTION.message, noSuchElementException)
        constants.Response.NO_SUCH_ELEMENT_EXCEPTION.throwBaseException(noSuchElementException)
      case nullPointerException: NullPointerException => logger.error(nullPointerException.getMessage)
        constants.Response.NULL_POINTER_EXCEPTION.throwBaseException(nullPointerException)
      case e: Exception => logger.error(constants.Response.GENERIC_EXCEPTION.message)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }
  }

  def convertToThumbnailWithHeight(filePath: String, canvasHeight: Int = 400)(implicit executionContext: ExecutionContext): Array[Byte] = {
    try {
      val file = new File(filePath: String)
      val imageRes = ImageIO.read(file)
      val writer: JpegWriter = JpegWriter.compression(100)
      val bytes = FileOperations.convertToByteArray(scrimage.ImmutableImage.loader().fromFile(file).fit((canvasHeight * imageRes.getWidth) / imageRes.getHeight, canvasHeight).output(writer, file))
      Base64.getEncoder.encode(bytes)
    } catch {
      case noSuchElementException: NoSuchElementException => logger.info(constants.Response.NO_SUCH_ELEMENT_EXCEPTION.message, noSuchElementException)
        constants.Response.NO_SUCH_ELEMENT_EXCEPTION.throwBaseException(noSuchElementException)
      case nullPointerException: NullPointerException => logger.error(nullPointerException.getMessage)
        constants.Response.NULL_POINTER_EXCEPTION.throwBaseException(nullPointerException)
      case e: Exception => logger.error(constants.Response.GENERIC_EXCEPTION.message, e)
        constants.Response.GENERIC_EXCEPTION.throwBaseException(e)
    }
  }

  def decodeImageThumbnailData(data: Array[Byte]): String = Base64.getEncoder.encodeToString(Base64.getDecoder.decode(data))

}