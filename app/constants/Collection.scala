package constants

import schema.data.base.StringData
import schema.id.base.{PropertyID, StringID}
import schema.property.base.MetaProperty

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
    val CREATOR_ID = "creatorID"

    val list: Seq[String] = Seq(NFT_NAME, NFT_DESCRIPTION, FILE_HASH, CREATOR_ID)

    val MetaProperties: Seq[MetaProperty] = list.map(x => MetaProperty(id = PropertyID(keyID = StringID(x), typeID = constants.Data.StringDataTypeID), data = StringData("").toAnyData))

  }

  case class CollectionStatus(name: String, id: Int)

  object Status {
    val NO_STATUS: CollectionStatus = CollectionStatus("NO_STATUS", 0)
    val PUBLIC_LISTED: CollectionStatus = CollectionStatus("PUBLIC_LISTED", 1)
    val WHITELIST_SALE: CollectionStatus = CollectionStatus("WHITELIST_SALE", 2)
    val MARKET: CollectionStatus = CollectionStatus("MARKET", 3)
  }
}
