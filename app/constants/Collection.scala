package constants

import models.common.Collection.Property
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

  def getFilePath: String = constants.CommonConfig.Files.CollectionPath + "/"

  def getNFTFilePath(collectionId: String)(implicit module: String, logger: Logger): String = constants.CommonConfig.Files.CollectionPath + "/" + collectionId + "/nfts/"

  object Category {
    val ART = "ART"
    val PHOTOGRAPHY = "PHOTOGRAPHY"
    val MISCELLANEOUS = "MISCELLANEOUS"
  }

  object DefaultProperty {
    // Should be kept in lower case otherwise change in form constraints
    val NAME = "collectionName"
    val DESCRIPTION = "collectionDescription"
    val CATEGORY = "category"
    val NSFW = "nsfw"
    val NFT_NAME = "nftName"
    val NFT_DESCRIPTION = "nftDescription"

    val IMMUTABLE = "IMMUTABLE"
    val MUTABLE = "MUTABLE"
    val REQUIRED = "REQUIRED"
    val NOT_REQUIRED = "NOT_REQUIRED"
    val NON_META = "NON_META"
    val META = "META"

    val list: Seq[String] = Seq(NAME, DESCRIPTION, CATEGORY, NSFW, NFT_NAME, NFT_DESCRIPTION)

    val defaultProperties: Seq[Property] = list.map { propertyName =>
      if (propertyName != NSFW) Property(name = propertyName, `type` = constants.NFT.Data.STRING, `value` = "", required = true, meta = true, mutable = false)
      else Property(name = propertyName, `type` = constants.NFT.Data.BOOLEAN, `value` = "", required = true, meta = true, mutable = false)
    }
  }
}
