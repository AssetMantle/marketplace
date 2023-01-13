package views.base.companion

import play.api.data.Forms._
import play.api.data._

object UploadFile {
  def form: Form[UploadFile] = Form(
    mapping(
      "resumableChunkNumber" -> number,
      "resumableChunkSize" -> number,
      "resumableTotalSize" -> longNumber,
      "resumableIdentifier" -> nonEmptyText,
      "resumableFilename" -> nonEmptyText
    )(UploadFile.apply)(UploadFile.unapply))

  case class UploadFile(resumableChunkNumber: Int, resumableChunkSize: Int, resumableTotalSize: Long, resumableIdentifier: String, resumableFilename: String)

}