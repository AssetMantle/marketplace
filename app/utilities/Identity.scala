package utilities

import schema.data.base.{AccAddressData, ListData, NumberData, StringData}
import schema.id.base.{IdentityID, PropertyID, StringID}
import schema.list.PropertyList
import schema.property.Property
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified.{Immutables, Mutables}

object Identity {

  private val idPropertyID = PropertyID(keyID = StringID("id"), typeID = schema.data.constants.StringDataTypeID)
  private val originPropertyID = PropertyID(keyID = StringID("origin"), typeID = schema.data.constants.StringDataTypeID)
  private val note1PropertyID = PropertyID(keyID = StringID("note1"), typeID = schema.data.constants.StringDataTypeID)
  private val note2PropertyID = PropertyID(keyID = StringID("note2"), typeID = schema.data.constants.StringDataTypeID)
  private val note3PropertyID = PropertyID(keyID = StringID("note3"), typeID = schema.data.constants.StringDataTypeID)
  private val note4PropertyID = PropertyID(keyID = StringID("note4"), typeID = schema.data.constants.StringDataTypeID)
  private val originMetaProperty: Property = MetaProperty(originPropertyID, StringData("MantlePlace"))

  def getIDMetaProperty(value: String): Property = MetaProperty(idPropertyID, StringData(value))

  def getNote1MetaProperty(value: String): Property = MetaProperty(note1PropertyID, StringData(value))

  def getNote2MetaProperty(value: String): Property = MetaProperty(note2PropertyID, StringData(value))

  def getNote3MesaProperty(value: String): Property = MesaProperty(note3PropertyID, StringData(value).getDataID)

  def getNote4MesaProperty(value: String): Property = MesaProperty(note4PropertyID, StringData(value).getDataID)

  val bondAmountMetaProperty: Property = MetaProperty(schema.constants.Properties.BondAmountProperty.id, NumberData(4104L))

  def getImmutableMetas(id: String): Immutables = Immutables(PropertyList(Seq(originMetaProperty, getIDMetaProperty(id))))

  def getImmutableMesas: Immutables = Immutables(Seq())

  def getImmutables(id: String): Immutables = Immutables(getImmutableMetas(id).propertyList.properties ++ getImmutableMesas.propertyList.properties)

  def getAuthenticationProperty(addresses: Seq[String]): Property = schema.constants.Properties.AuthenticationProperty.copy(data = ListData(addresses.map(x => AccAddressData(x))))

  def getMutableMetas(addresses: Seq[String]): Mutables = Mutables(PropertyList(Seq(bondAmountMetaProperty, getAuthenticationProperty(addresses), getNote1MetaProperty(""), getNote2MetaProperty(""))))

  def getMutableMesas: Mutables = Mutables(PropertyList(Seq(getNote3MesaProperty(""), getNote4MesaProperty(""))))

  def getMutables(addresses: Seq[String]): Mutables = Mutables(getMutableMetas(addresses).propertyList.properties ++ getMutableMesas.propertyList.properties)

  def getMantlePlaceIdentityID(id: String): IdentityID = {
    schema.utilities.ID.getIdentityID(classificationID = constants.Transaction.IdentityClassificationID, immutables = getImmutables(id))
  }

}
