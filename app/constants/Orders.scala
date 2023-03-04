package constants

import schema.data.base.{NumberData, StringData}
import schema.id.base.{PropertyID, StringID}
import schema.property.base.MetaProperty

object Orders {

  val ORIGIN = "origin"

  val BondAmountMetaProperty: MetaProperty = MetaProperty(constants.Blockchain.BondAmountProperty.id, NumberData(560L).toAnyData)

  val OriginMetaProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID(ORIGIN), typeID = constants.Data.StringDataTypeID), data = StringData("MantlePlaceSecondaryMarket").toAnyData)

  val ImmutableMetas: Seq[MetaProperty] = Seq(OriginMetaProperty, BondAmountMetaProperty)
}
