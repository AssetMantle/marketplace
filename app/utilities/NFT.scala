package utilities

import schema.data.base.{NumberData, StringData}
import schema.id.base.{AssetID, ClassificationID, PropertyID, StringID}
import schema.property.base.MetaProperty
import schema.qualified.Immutables

object NFT {

  def getDefaultImmutableMetaProperties(name: String, description: String, fileHash: String, bondAmount: Long): Seq[MetaProperty] = Seq(
    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.NFT_NAME), typeID = constants.Data.StringDataTypeID), data = StringData(name).toAnyData),
//    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.NFT_DESCRIPTION), typeID = constants.Data.StringDataTypeID), data = StringData(description).toAnyData),
    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.FILE_HASH), typeID = constants.Data.StringDataTypeID), data = StringData(fileHash).toAnyData),
    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.BOND_AMOUNT), typeID = constants.Data.NumberDataTypeID), data = NumberData(bondAmount).toAnyData)
  )

  def getAssetID(classificationID: ClassificationID, immutables: Immutables): AssetID = utilities.ID.getAssetID(classificationID, immutables)

}
