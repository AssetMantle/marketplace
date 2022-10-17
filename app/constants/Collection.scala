package constants

import play.api.Logger

object Collection {

  object File {
    val PROFILE = "PROFILE"
    val COVER = "COVER"
  }

  object SocialProfile {
    val WEBSITE = "WEBSITE"
    val TWITTER = "TWITTER"
    val INSTAGRAM = "INSTAGRAM"
  }

  def getFilePath(id: String, documentType: String)(implicit module: String, logger: Logger): String = {
    documentType match {
      case File.PROFILE => constants.CommonConfig.Files.CollectionPath + "/" + id + "/" + File.PROFILE + "/"
      case File.COVER => constants.CommonConfig.Files.CollectionPath + "/" + id + "/" + File.COVER + "/"
      case _ => constants.Response.NO_SUCH_DOCUMENT_TYPE_EXCEPTION.throwBaseException()
    }
  }

  def getNFTFilePath(collectionId: String)(implicit module: String, logger: Logger): String = constants.CommonConfig.Files.CollectionPath + "/" + collectionId + "/nfts/"

  object Category {
    val ART = "ART"
    val PHOTOGRAPHY = "PHOTOGRAPHY"
    val MISCELLANEOUS = "MISCELLANEOUS"
  }

  object DefaultProperty {
    // Should be kept in lower case otherwise change in form constraints
    val NAME = "name"
    val DESCRIPTION = "description"
    val CATEGORY = "category"
    val NSFW = "nsfw"

    val IMMUTABLE = "IMMUTABLE"
    val MUTABLE = "MUTABLE"
    val REQUIRED = "REQUIRED"
    val NOT_REQUIRED = "NOT_REQUIRED"
    val NON_META = "NON_META"
    val META = "META"

    val list: Seq[String] = Seq(NAME, DESCRIPTION, CATEGORY, NSFW)
  }

  object NFT {
    object Data {
      val STRING = "STRING"
      val NUMBER = "NUMBER"
      val BOOLEAN = "BOOLEAN"
    }
  }

}
