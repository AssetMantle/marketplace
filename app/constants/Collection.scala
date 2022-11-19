package constants

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

  def getFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(File.AllCollectionsPath + collectionId + "/others/")

  def getNFTFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(File.AllCollectionsPath + collectionId + "/nfts/")

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

    val IMMUTABLE = "IMMUTABLE"
    val MUTABLE = "MUTABLE"
    val NOT_REQUIRED = "NOT_REQUIRED"
    val NON_META = "NON_META"
    val META = "META"

    val list: Seq[String] = Seq(NFT_NAME, NFT_DESCRIPTION, FILE_HASH, CLASSIFICATION_ID)

  }
}
