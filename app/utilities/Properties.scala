package utilities

import models.master.NFTProperty
import play.api.Logger
import schema.data.Data
import schema.data.base.{BooleanData, DecData, NumberData, StringData}
import schema.id.base.{DataID, PropertyID, StringID}
import schema.property.base.{MesaProperty, MetaProperty}

import scala.util.Try

object Properties {

  implicit val module: String = constants.Module.UTILITIES_PROPERTIES

  implicit val logger: Logger = Logger(this.getClass)

  def validateTypeValue(`type`: String, value: String): Boolean = `type` match {
    case constants.NFT.Data.STRING => true
    case constants.NFT.Data.NUMBER => Try(value.toLong).isSuccess
    case constants.NFT.Data.DECIMAL => Try(BigDecimal(value)).isSuccess
    case constants.NFT.Data.BOOLEAN => Try(value.toBoolean).isSuccess // value == constants.NFT.Data.TRUE || value == constants.NFT.Data.SMALL_TRUE || value == constants.NFT.Data.FALSE || value == constants.NFT.Data.SMALL_FALSE
    case _ => constants.Response.NFT_PROPERTY_TYPE_NOT_FOUND.throwBaseException()
  }

  def getTypeID(`type`: String): StringID = `type` match {
    case constants.NFT.Data.STRING => schema.data.constants.StringDataTypeID
    case constants.NFT.Data.BOOLEAN => schema.data.constants.BooleanDataTypeID
    case constants.NFT.Data.NUMBER => schema.data.constants.NumberDataTypeID
    case constants.NFT.Data.DECIMAL => schema.data.constants.DecDataTypeID
    case _ => throw new IllegalArgumentException(s"INVALID_DATA_TYPE: ${`type`}")
  }

  def getDataID(`type`: String, value: String): DataID = `type` match {
    case constants.NFT.Data.STRING => StringData(value).getDataID
    case constants.NFT.Data.BOOLEAN => if (value != "") BooleanData(value.toBoolean).getDataID else BooleanData(false).getDataID
    case constants.NFT.Data.NUMBER => if (value != "") NumberData(value.toLong).getDataID else NumberData(0).getDataID
    case constants.NFT.Data.DECIMAL => if (value != "") DecData(value).getDataID else DecData(0.0).getDataID
    case _ => throw new IllegalArgumentException(s"INVALID_DATA: ${`type`} + $value")
  }

  def getData(`type`: String, value: String): Data = `type` match {
    case constants.NFT.Data.STRING => StringData(value)
    case constants.NFT.Data.BOOLEAN => if (value != "") BooleanData(value.toBoolean) else BooleanData(false)
    case constants.NFT.Data.NUMBER => if (value != "") NumberData(value.toLong) else NumberData(0)
    case constants.NFT.Data.DECIMAL => if (value != "") DecData(value) else DecData(0.0)
    case _ => throw new IllegalArgumentException(s"INVALID_DATA: ${`type`} + $value")
  }

  def getCollectionDefaultImmutableMetaProperties(collectionName: String, creatorID: String, nftName: String = "", fileHash: String = ""): Seq[MetaProperty] = Seq(
    MetaProperty(id = PropertyID(keyID = StringID(constants.Properties.DefaultProperty.NFT_NAME), typeID = schema.data.constants.StringDataTypeID), data = StringData(nftName)),
    MetaProperty(id = PropertyID(keyID = StringID(constants.Properties.DefaultProperty.COLLECTION_NAME), typeID = schema.data.constants.StringDataTypeID), data = StringData(collectionName)),
    MetaProperty(id = PropertyID(keyID = StringID(constants.Properties.DefaultProperty.FILE_HASH), typeID = schema.data.constants.StringDataTypeID), data = StringData(fileHash)),
    MetaProperty(id = PropertyID(keyID = StringID(constants.Properties.DefaultProperty.CREATOR_ID), typeID = schema.data.constants.StringDataTypeID), data = StringData(creatorID)) // Should be id not identityID for web3 to find the value.
  )

  def getNFTDefaultImmutableMetaProperties(name: String, collectionName: String, fileHash: String, bondAmount: Long, creatorID: String): Seq[MetaProperty] = {
    getCollectionDefaultImmutableMetaProperties(collectionName = collectionName, creatorID = creatorID, nftName = name, fileHash = fileHash) :+
      MetaProperty(id = PropertyID(keyID = StringID(constants.Properties.DefaultProperty.BOND_AMOUNT), typeID = schema.data.constants.NumberDataTypeID), data = NumberData(bondAmount))
  }

  def metaPropertyToNFTProperty(nftId: String, metaProperty: MetaProperty, mutable: Boolean): NFTProperty = metaProperty.getData.getType.value match {
    case schema.data.constants.BooleanDataTypeID.value => NFTProperty(nftId = nftId, name = metaProperty.id.keyID.value, `type` = constants.NFT.Data.BOOLEAN, value = BooleanData(metaProperty.getData.getProtoBytes).value.toString, meta = true, mutable = mutable)
    case schema.data.constants.StringDataTypeID.value => NFTProperty(nftId = nftId, name = metaProperty.id.keyID.value, `type` = constants.NFT.Data.STRING, value = StringData(metaProperty.getData.getProtoBytes).value, meta = true, mutable = mutable)
    case schema.data.constants.NumberDataTypeID.value => NFTProperty(nftId = nftId, name = metaProperty.id.keyID.value, `type` = constants.NFT.Data.NUMBER, value = NumberData(metaProperty.getData.getProtoBytes).value.toString, meta = true, mutable = mutable)
    case schema.data.constants.DecDataTypeID.value => NFTProperty(nftId = nftId, name = metaProperty.id.keyID.value, `type` = constants.NFT.Data.DECIMAL, value = DecData(metaProperty.getData.getProtoBytes).value.toString, meta = true, mutable = mutable)
    case _ => throw new IllegalArgumentException("NFT_PROPERTY_UNKNOWN_DATA_TYPE")
  }

  def mesaPropertyToNFTProperty(nftId: String, mesaProperty: MesaProperty, mutable: Boolean): NFTProperty = mesaProperty.getType.value match {
    case schema.data.constants.BooleanDataTypeID.value => NFTProperty(nftId = nftId, name = mesaProperty.id.keyID.value, `type` = constants.NFT.Data.BOOLEAN, value = mesaProperty.getDataID.getHashIDString, meta = false, mutable = mutable)
    case schema.data.constants.StringDataTypeID.value => NFTProperty(nftId = nftId, name = mesaProperty.id.keyID.value, `type` = constants.NFT.Data.STRING, value = mesaProperty.getDataID.getHashIDString, meta = false, mutable = mutable)
    case schema.data.constants.NumberDataTypeID.value => NFTProperty(nftId = nftId, name = mesaProperty.id.keyID.value, `type` = constants.NFT.Data.NUMBER, value = mesaProperty.getDataID.getHashIDString, meta = false, mutable = mutable)
    case schema.data.constants.DecDataTypeID.value => NFTProperty(nftId = nftId, name = mesaProperty.id.keyID.value, `type` = constants.NFT.Data.DECIMAL, value = mesaProperty.getDataID.getHashIDString, meta = false, mutable = mutable)
    case _ => throw new IllegalArgumentException("NFT_PROPERTY_UNKNOWN_DATA_TYPE")
  }
}
