package constants

import schema.data.base.{IDData, StringData}
import schema.id.base.{IdentityID, PropertyID, StringID}
import schema.property.base.{MesaProperty, MetaProperty}

object Collection {

  val GenesisCollectionIDs = Seq(
    "AF3759D04DD81F27", "A07FAEF74B59B7F7", "C395A2ECE13B3B03", "C7E72E66C70DE134", "F6453EBFBEB4CE6C",
    "C5FD79BDDDCB75C4", "90059167EFA307A5", "BD3B7129B39506F0", "B86375904A635FB3", "C984F63CDBB8BB0C",
    "8034E1D7A0B83DAB", "FA643042634AD34B", "D73691D95C7250CD", "E691345678D3D476", "883C86FC2E5D21D1",
    "803C8C529D4C79B6", "AE86B3EE463AA709"
  )
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
    val CREATOR_ID = "creatorID"
    val BOND_AMOUNT = "bondAmount"

    val list: Seq[String] = Seq(NFT_NAME, NFT_DESCRIPTION, FILE_HASH, CREATOR_ID)

    val metaList: Seq[String] = Seq(NFT_NAME, FILE_HASH)

    val allImmutableMetaProperties: Seq[MetaProperty] = metaList.map(x => MetaProperty(id = PropertyID(keyID = StringID(x), typeID = constants.Data.StringDataTypeID), data = StringData("").toAnyData))
    def allImmutableMesaProperties(creatorID: IdentityID): Seq[MesaProperty] = Seq(MesaProperty(id = PropertyID(keyID = StringID(CREATOR_ID), typeID = constants.Data.IDDataTypeID), dataID = IDData(creatorID.toAnyID).getDataID))

  }

  case class CollectionStatus(name: String, id: Int)

  object Status {
    val NO_STATUS: CollectionStatus = CollectionStatus("NO_STATUS", 0)
    val PUBLIC_LISTED: CollectionStatus = CollectionStatus("PUBLIC_LISTED", 1)
    val WHITELIST_SALE: CollectionStatus = CollectionStatus("WHITELIST_SALE", 2)
    val MARKET: CollectionStatus = CollectionStatus("MARKET", 3)
  }
}
