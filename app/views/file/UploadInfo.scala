package views.file

import play.api.data.Forms._
import play.api.data._

object UploadInfo {
  def form = Form(
    mapping(
      "resumableChunkNumber" -> number,
      "resumableChunkSize" -> number,
      "resumableTotalSize" -> longNumber,
      "resumableIdentifier" -> nonEmptyText,
      "resumableFilename" -> nonEmptyText
    )(UploadInfo.apply)(UploadInfo.unapply))

  case class UploadInfo(resumableChunkNumber: Int, resumableChunkSize: Int, resumableTotalSize: Long, resumableIdentifier: String, resumableFilename: String) {
    def totalChunks: Double = Math.ceil(resumableTotalSize.toDouble / resumableChunkSize.toDouble)
  }

}