package constants

object Properties {

  object DefaultProperty {
    // Should be kept in lower case otherwise change in form constraints
    val NFT_NAME = "name"
    val COLLECTION_NAME = "collectionName"
    val FILE_RESOURCE = "fileResource"
    val CREATOR_ID = "creatorID"

    val list: Seq[String] = Seq(NFT_NAME, COLLECTION_NAME, FILE_RESOURCE, CREATOR_ID)

  }

  val RestrictedPropertyList: Seq[String] = DefaultProperty.list

}
