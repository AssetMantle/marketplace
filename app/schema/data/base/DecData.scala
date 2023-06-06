package schema.data.base

import com.assetmantle.schema.data.base.{AnyData, AnyListableData, DecData => protoDecData}
import schema.data.ListableData
import schema.id.base.{DataID, HashID, StringID}

case class DecData(value: BigDecimal) extends ListableData {

  def toPlainString: String = schema.constants.Data.DecStringFormat.format(this.value)

  def getType: StringID = schema.constants.Data.DecDataTypeID

  def getBondWeight: Int = schema.constants.Data.DecDataWeight

  def getValue: BigDecimal = this.value

  def getDataID: DataID = DataID(typeID = schema.constants.Data.DecDataTypeID, hashID = this.generateHashID)

  def zeroValue: DecData = DecData(BigDecimal(0))

  def getBytes: Array[Byte] = this.toPlainString.getBytes

  def generateHashID: HashID = if (this.value == this.zeroValue.value) schema.utilities.ID.generateHashID() else schema.utilities.ID.generateHashID(this.getBytes)

  def asProtoDecData: protoDecData = protoDecData.newBuilder().setValue(this.toPlainString).build()

  def toAnyData: AnyData = AnyData.newBuilder().setDecData(this.asProtoDecData).build()

  def toAnyListableData: AnyListableData = AnyListableData.newBuilder().setDecData(this.asProtoDecData).build()

  def getProtoBytes: Array[Byte] = this.asProtoDecData.toByteString.toByteArray

  def viewString: String = "Decimal: " + this.toPlainString

  def validSortable: Boolean = this.getValue.abs <= schema.constants.Data.DecDataMaxValue

  def getSortableDecBytes: Array[Byte] = {
    if (!this.validSortable) throw new IllegalArgumentException("INVALID_DEC_DATA_FOR_SORTED_BYTES")
    else {
      if (this.getValue == schema.constants.Data.DecDataMaxValue) "max".getBytes
      else if (this.getValue == (-1 * schema.constants.Data.DecDataMaxValue)) "--".getBytes
      else {
        val f = java.lang.String.format("%18s", this.getValue.abs.toString.split("\\.").head).replace(" ", "0")
        val l = java.lang.String.format("%-18s", this.getValue.abs.toString.split("\\.").last).replace(" ", "0")
        if (this.getValue < 0) "-".getBytes ++ (f + "." + l).getBytes
        else (f + "." + l).getBytes
      }
    }
  }

  def quotientTruncate(that: DecData): DecData = DecData(BigDecimal((this.getValue * schema.constants.Data.DecFactor).toBigInt / (that.getValue * schema.constants.Data.DecFactor).toBigInt) / schema.constants.Data.DecFactor)

  def multiplyTruncate(that: DecData): DecData = DecData(BigDecimal((this.getValue * schema.constants.Data.DecFactor).toBigInt * (that.getValue * schema.constants.Data.DecFactor).toBigInt) / schema.constants.Data.DecFactor)
}

object DecData {

  def apply(value: protoDecData): DecData = DecData(BigDecimal(value.getValue))

  def apply(protoBytes: Array[Byte]): DecData = DecData(protoDecData.parseFrom(protoBytes))

  def apply(value: String): DecData = DecData(BigDecimal(value))

}
