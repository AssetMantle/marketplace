package utilities

import models.master.NFTProperty
import schema.data.base.{BooleanData, NumberData, StringData}
import schema.id.base.{AssetID, ClassificationID, PropertyID, StringID}
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified.Immutables

object NFT {

  def getDefaultImmutableMetaProperties(name: String, description: String, fileHash: String, bondAmount: Long): Seq[MetaProperty] = Seq(
    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.NFT_NAME), typeID = constants.Data.StringDataTypeID), data = StringData(name).toAnyData),
    //    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.NFT_DESCRIPTION), typeID = constants.Data.StringDataTypeID), data = StringData(description).toAnyData),
    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.FILE_HASH), typeID = constants.Data.StringDataTypeID), data = StringData(fileHash).toAnyData),
    MetaProperty(id = PropertyID(keyID = StringID(constants.Collection.DefaultProperty.BOND_AMOUNT), typeID = constants.Data.NumberDataTypeID), data = NumberData(bondAmount).toAnyData)
  )

  def getAssetID(classificationID: ClassificationID, immutables: Immutables): AssetID = utilities.ID.getAssetID(classificationID, immutables)

  def metaPropertyToNFTProperty(nftId: String, metaProperty: MetaProperty, mutable: Boolean): NFTProperty = metaProperty.getData.getType.value match {
    case constants.Data.BooleanDataTypeID.value => NFTProperty(nftId = nftId, name = metaProperty.id.keyID.value, `type` = constants.NFT.Data.BOOLEAN, `value` = BooleanData(metaProperty.getData.getProtoBytes).value.toString, meta = true, mutable = mutable)
    case constants.Data.StringDataTypeID.value => NFTProperty(nftId = nftId, name = metaProperty.id.keyID.value, `type` = constants.NFT.Data.STRING, `value` = StringData(metaProperty.getData.getProtoBytes).value, meta = true, mutable = mutable)
    case constants.Data.NumberDataTypeID.value => NFTProperty(nftId = nftId, name = metaProperty.id.keyID.value, `type` = constants.NFT.Data.NUMBER, `value` = NumberData(metaProperty.getData.getProtoBytes).value.toString, meta = true, mutable = mutable)
    case _ => throw new IllegalArgumentException("NFT_PROPERTY_UNKNOWN_DATA_TYPE")
  }

  def mesaPropertyToNFTProperty(nftId: String, mesaProperty: MesaProperty, mutable: Boolean): NFTProperty = mesaProperty.getType.value match {
    case constants.Data.BooleanDataTypeID.value => NFTProperty(nftId = nftId, name = mesaProperty.id.keyID.value, `type` = constants.NFT.Data.BOOLEAN, `value` = mesaProperty.getDataID.getHashIDString, meta = false, mutable = mutable)
    case constants.Data.StringDataTypeID.value => NFTProperty(nftId = nftId, name = mesaProperty.id.keyID.value, `type` = constants.NFT.Data.STRING, `value` = mesaProperty.getDataID.getHashIDString, meta = false, mutable = mutable)
    case constants.Data.NumberDataTypeID.value => NFTProperty(nftId = nftId, name = mesaProperty.id.keyID.value, `type` = constants.NFT.Data.NUMBER, `value` = mesaProperty.getDataID.getHashIDString, meta = false, mutable = mutable)
    case _ => throw new IllegalArgumentException("NFT_PROPERTY_UNKNOWN_DATA_TYPE")
  }
}
