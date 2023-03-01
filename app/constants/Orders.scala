package constants

import schema.data.base.{NumberData, StringData}
import schema.id.base.{PropertyID, StringID}
import schema.property.Property
import schema.property.base.MetaProperty

object Orders {

  val ORIGIN = "origin"

  def bondAmountMetaProperty: Property = MetaProperty(constants.Blockchain.BondAmountProperty.id, NumberData(560L).toAnyData)

  val OriginMetaProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID(ORIGIN), typeID = constants.Data.StringDataTypeID), data = StringData("MantlePlaceSecondaryMarket").toAnyData)

}
