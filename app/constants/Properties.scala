package constants

object Properties {

  object DefaultProperty {
    // Should be kept in lower case otherwise change in form constraints
    val NFT_NAME = "name"
    val COLLECTION_NAME = "collectionName"
    val FILE_RESOURCE = "fileResource"
    val CREATOR_ID = "creatorID"
    val BOND_AMOUNT = "bondAmount"

    val list: Seq[String] = Seq(NFT_NAME, COLLECTION_NAME, FILE_RESOURCE, CREATOR_ID, BOND_AMOUNT)

  }

  val RestrictedPropertyList: Seq[String] = DefaultProperty.list ++ Seq(
    schema.constants.Properties.SupplyProperty.id.keyID.value,
    schema.constants.Properties.BurnHeightProperty.id.keyID.value,
    schema.constants.Properties.LockHeightProperty.id.keyID.value
  )

}
