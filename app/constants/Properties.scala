package constants

object Properties {

  object DefaultProperty {
    // Should be kept in lower case otherwise change in form constraints
    val NFT_NAME = "name"
    val COLLECTION_NAME = "collectionName"
    val FILE_HASH = "fileHash"
    val CREATOR_ID = "creatorID"
    val BOND_AMOUNT = "bondAmount"

    val list: Seq[String] = Seq(NFT_NAME, COLLECTION_NAME, FILE_HASH, CREATOR_ID)

  }

  val RestrictedPropertyList: Seq[String] = Seq(
    DefaultProperty.NFT_NAME,
    DefaultProperty.COLLECTION_NAME,
    DefaultProperty.FILE_HASH,
    DefaultProperty.BOND_AMOUNT,
    schema.constants.Properties.SupplyProperty.id.keyID.value,
    schema.constants.Properties.BurnHeightProperty.id.keyID.value)

}
