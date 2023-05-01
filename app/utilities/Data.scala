package utilities

import schema.data.Data
import schema.data.base._
import schema.id.base.{DataID, StringID}

object Data {

  def getTypeID(`type`: String): StringID = `type` match {
    case constants.NFT.Data.STRING => constants.Data.StringDataTypeID
    case constants.NFT.Data.BOOLEAN => constants.Data.BooleanDataTypeID
    case constants.NFT.Data.NUMBER => constants.Data.NumberDataTypeID
    case _ => constants.Data.StringDataTypeID
  }

  def getDataID(`type`: String, value: String): DataID = `type` match {
    case constants.NFT.Data.STRING => StringData(value).getDataID
    case constants.NFT.Data.BOOLEAN => if (value != "") BooleanData(value.toBoolean).getDataID else BooleanData(false).getDataID
    case constants.NFT.Data.NUMBER => NumberData(value.toLong).getDataID
    case _ => throw new IllegalArgumentException("INVALID_DATA")
  }

  def getData(`type`: String, value: String): Data = `type` match {
    case constants.NFT.Data.STRING => StringData(value)
    case constants.NFT.Data.BOOLEAN => if (value != "") BooleanData(value.toBoolean) else BooleanData(false)
    case constants.NFT.Data.NUMBER => NumberData(value.toLong)
    case _ => throw new IllegalArgumentException("INVALID_DATA")
  }
}
