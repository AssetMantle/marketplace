package constants

import models.common.Collection.Property

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

  def getNFTFilePath: String = constants.CommonConfig.Files.CollectionPath + "/nfts/"

  object Category {
    val ART = "ART"
    val PHOTOGRAPHY = "PHOTOGRAPHY"
    val MISCELLANEOUS = "MISCELLANEOUS"
  }

  object DefaultProperty {
    // Should be kept in lower case otherwise change in form constraints
    val NFT_NAME = "nftName"
    val NFT_DESCRIPTION = "nftDescription"
    val FILE_HASH = "fileHash"

    val IMMUTABLE = "IMMUTABLE"
    val MUTABLE = "MUTABLE"
    val REQUIRED = "REQUIRED"
    val NOT_REQUIRED = "NOT_REQUIRED"
    val NON_META = "NON_META"
    val META = "META"

    val list: Seq[String] = Seq(NFT_NAME, NFT_DESCRIPTION, FILE_HASH)

    val defaultProperties: Seq[Property] = list.map { propertyName => Property(name = propertyName, `type` = constants.NFT.Data.STRING, `value` = "", required = true, meta = true, mutable = false)
    }
  }
}
