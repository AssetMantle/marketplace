package constants

import controllers.routes
import play.api.routing.JavaScriptReverseRoute

object File {

  val KEY_FILE = "file"

  val IMAGE = "IMAGES"
  val DOCUMENT = "DOCUMENT"
  val FILE = "FILE"

  //File Extensions
  val PDF = "pdf"
  val TXT = "txt"
  val JPG = "JPG"
  val PNG = "PNG"
  val JPEG = "JPEG"
  val GIF = "GIF"
  val JPG_LOWER_CASE = "jpg"
  val PNG_LOWER_CASE = "png"
  val JPEG_LOWER_CASE = "jpeg"
  val GIF_LOWER_CASE = "gif"
  val DOC = "doc"
  val DOCX = "docx"

  val ALL_IMAGES: Seq[String] = Seq(JPG, JPEG, PNG, JPG_LOWER_CASE, JPEG_LOWER_CASE, PNG_LOWER_CASE)
  val ALL_IMAGES_WITH_GIF: Seq[String] = Seq(JPG, JPEG, PNG, GIF, JPG_LOWER_CASE, JPEG_LOWER_CASE, PNG_LOWER_CASE, GIF_LOWER_CASE)

  object Account {
    val PROFILE_PICTURE = "PROFILE_PICTURE"
  }

  case class FileUploadForm(name: String, get: JavaScriptReverseRoute, store: JavaScriptReverseRoute, upload: JavaScriptReverseRoute, fileTypes: Seq[String], maxFileSize: Long = 10485760)

  val COLLECTION_FILE_FORM: FileUploadForm = FileUploadForm("COLLECTION_FILE_FORM", get = routes.javascript.CollectionController.uploadCollectionFileForm, store = routes.javascript.CollectionController.storeCollectionFile, upload = routes.javascript.CollectionController.uploadCollectionFile, fileTypes = ALL_IMAGES_WITH_GIF)

}
