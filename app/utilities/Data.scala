package utilities

import com.data.AnyData
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
    case constants.NFT.Data.STRING => StringData(value).getID
    case constants.NFT.Data.BOOLEAN => BooleanData(value.toBoolean).getID
    case constants.NFT.Data.NUMBER => NumberData(value.toLong).getID
    case _ => StringData(value).getID
  }

  def getAnyData(`type`: String, value: String): AnyData = `type` match {
    case constants.NFT.Data.STRING => StringData(value).toAnyData
    case constants.NFT.Data.BOOLEAN => BooleanData(value.toBoolean).toAnyData
    case constants.NFT.Data.NUMBER => NumberData(value.toLong).toAnyData
    case _ => StringData(value).toAnyData
  }
}
