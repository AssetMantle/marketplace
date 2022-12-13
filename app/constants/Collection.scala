package constants

import models.master.NFTProperty

object Collection {

  object File {
    val PROFILE = "PROFILE"
    val COVER = "COVER"

    val AllCollectionsPath: String = constants.CommonConfig.Files.CollectionPath + "/"
  }

  object SocialProfile {
    val WEBSITE = "WEBSITE"
    val TWITTER = "TWITTER"
    val INSTAGRAM = "INSTAGRAM"
  }

  object Category {
    val ART = "ART"
    val PHOTOGRAPHY = "PHOTOGRAPHY"
    val MISCELLANEOUS = "MISCELLANEOUS"
  }

  object DefaultProperty {
    // Should be kept in lower case otherwise change in form constraints
    val NFT_NAME = "name"
    val NFT_DESCRIPTION = "description"
    val FILE_HASH = "fileHash"
    val CLASSIFICATION_ID = "classificationId"

    val list: Seq[String] = Seq(NFT_NAME, NFT_DESCRIPTION, FILE_HASH, CLASSIFICATION_ID)

  }
}
