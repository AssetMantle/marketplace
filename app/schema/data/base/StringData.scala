package schema.data.base

import com.data.{AnyData, StringData => protoStringData}
import schema.data.Data
import schema.id.base.{DataID, HashID, StringID}

case class StringData(value: String) extends Data {
  def getType: StringID = constants.Data.StringDataTypeID

  def getBondWeight: Int = constants.Data.StringDataWeight

  def getID: DataID = DataID(typeID = constants.Data.StringDataTypeID, hashID = this.generateHashID)

  def zeroValue: Data = StringData("")

  def getBytes: Array[Byte] = this.value.getBytes

  def generateHashID: HashID = utilities.ID.generateHashID(this.getBytes)

  def asProtoStringData: protoStringData = protoStringData.newBuilder().setValue(this.value).build()

  def toAnyData: AnyData = AnyData.newBuilder().setStringData(this.asProtoStringData).build()

  def getProtoBytes: Array[Byte] = this.asProtoStringData.toByteString.toByteArray

  def viewString: String = this.value
}

object StringData {

  def apply(value: protoStringData): StringData = StringData(value.getValue)

  def apply(protoBytes: Array[Byte]): StringData = StringData(protoStringData.parseFrom(protoBytes))
}