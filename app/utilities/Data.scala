package utilities

import schema.data.Data
import schema.data.base._
import schema.id.base.{DataID, StringID}

object Data {

  def getTypeID(`type`: String): StringID = `type` match {
    case constants.NFT.Data.STRING => schema.constants.Data.StringDataTypeID
    case constants.NFT.Data.BOOLEAN => schema.constants.Data.BooleanDataTypeID
    case constants.NFT.Data.NUMBER => schema.constants.Data.NumberDataTypeID
    case constants.NFT.Data.DECIMAL => schema.constants.Data.DecDataTypeID
    case _ => schema.constants.Data.StringDataTypeID
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
}
