package constants

import schema.data.base.{NumberData, StringData}
import schema.id.base.{PropertyID, StringID}
import schema.property.base.MetaProperty

object Orders {

  val ORIGIN = "origin"

  val BondAmountMetaProperty: MetaProperty = MetaProperty(schema.constants.Properties.BondAmountProperty.getID, NumberData(552L))

  val OriginMetaProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID(ORIGIN), typeID = schema.constants.Data.StringDataTypeID), data = StringData("MantlePlaceSecondaryMarket"))

  val ImmutableMetas: Seq[MetaProperty] = Seq(OriginMetaProperty, BondAmountMetaProperty)
}
