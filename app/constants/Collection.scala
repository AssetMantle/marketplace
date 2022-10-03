package constants

import models.master.CollectionProperty
import play.api.Logger

import scala.concurrent.ExecutionContext

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

  def getFilePath(documentType: String)(implicit module: String, logger: Logger): String = {
    documentType match {
      case File.PROFILE => constants.CommonConfig.Files.CollectionPath + "/"
      case File.COVER => constants.CommonConfig.Files.CollectionPath + "/"
      case _ => constants.Response.NO_SUCH_DOCUMENT_TYPE_EXCEPTION.throwBaseException()
    }
  }

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

    val list: Seq[String] = Seq(NAME, DESCRIPTION, CATEGORY, NSFW)

    def getDefaultProperties(id: String): Seq[CollectionProperty] = list.map(x => CollectionProperty(id = id, propertyName = x, propertyType = PropertyData.Type.STRING, required = true, mutable = false, fixedValue = None, hideValue = false))
  }

  object NFT {
    object Data {
      val STRING = "STRING"
      val NUMBER = "NUMBER"
      val BOOLEAN = "BOOLEAN"
    }
  }

}
