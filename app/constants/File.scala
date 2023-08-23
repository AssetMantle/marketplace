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
  val MP3 = "mp3"
  val WAV = "wav"
  val OGG = "ogg"

  val ALL_IMAGES: Seq[String] = Seq(JPG, JPEG, PNG, JPG_LOWER_CASE, JPEG_LOWER_CASE, PNG_LOWER_CASE)
  val ALL_IMAGES_WITH_GIF: Seq[String] = Seq(JPG, JPEG, PNG, GIF, JPG_LOWER_CASE, JPEG_LOWER_CASE, PNG_LOWER_CASE, GIF_LOWER_CASE)
  val ALL_AUDIO: Seq[String] = Seq(MP3, WAV, OGG)

  val NFT_TYPES: Seq[String] = ALL_IMAGES_WITH_GIF ++ ALL_AUDIO

  object Account {
    val PROFILE_PICTURE = "PROFILE_PICTURE"
  }

  val DefaultFileSize: Long = 10485760 // in Bytes
  val MaxNFTSize: Long = 104857600 // in Bytes

  case class FileUploadForm(name: String, get: JavaScriptReverseRoute, store: JavaScriptReverseRoute, upload: JavaScriptReverseRoute, fileTypes: Seq[String], maxFileSize: Long)

  val COLLECTION_DRAFT_FILE_FORM: FileUploadForm = FileUploadForm("COLLECTION_DRAFT_FILE_FORM", get = routes.javascript.CollectionController.uploadCollectionDraftFileForm, store = routes.javascript.CollectionController.storeCollectionDraftFile, upload = routes.javascript.CollectionController.uploadCollectionDraftFile, fileTypes = ALL_IMAGES_WITH_GIF, maxFileSize = DefaultFileSize)
  val COLLECTION_FILE_FORM: FileUploadForm = FileUploadForm("COLLECTION_FILE_FORM", get = routes.javascript.CollectionController.uploadCollectionFileForm, store = routes.javascript.CollectionController.storeCollectionFile, upload = routes.javascript.CollectionController.uploadCollectionFile, fileTypes = ALL_IMAGES_WITH_GIF, maxFileSize = DefaultFileSize)
  val NFT_FILE_FORM: FileUploadForm = FileUploadForm("NFT_FILE_FORM", get = routes.javascript.NFTController.uploadNFTFileForm, store = routes.javascript.NFTController.storeNFTFile, upload = routes.javascript.NFTController.uploadNFTFile, fileTypes = NFT_TYPES, maxFileSize = MaxNFTSize)

}
