package utilities

import schema.data.base.{AccAddressData, ListData, NumberData, StringData}
import schema.id.base.{IdentityID, PropertyID, StringID}
import schema.list.PropertyList
import schema.property.Property
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified.Immutables

object Identity {

  private val idPropertyID = PropertyID(keyID = StringID("id"), typeID = constants.Data.StringDataTypeID)
  private val originPropertyID = PropertyID(keyID = StringID("origin"), typeID = constants.Data.StringDataTypeID)
  private val twitterPropertyID = PropertyID(keyID = StringID("twitter"), typeID = constants.Data.StringDataTypeID)
  private val note1PropertyID = PropertyID(keyID = StringID("note1"), typeID = constants.Data.StringDataTypeID)
  private val note2PropertyID = PropertyID(keyID = StringID("note2"), typeID = constants.Data.StringDataTypeID)
  private val extraPropertyID = PropertyID(keyID = StringID("extra"), typeID = constants.Data.StringDataTypeID)

  val getOriginMetaProperty: Property = MetaProperty(originPropertyID, StringData("MantlePlace"))

  def getIDMetaProperty(value: String): Property = MetaProperty(idPropertyID, StringData(value))

  def getTwitterMetaProperty(value: String): Property = MetaProperty(twitterPropertyID, StringData(value))

  def getNote1MetaProperty(value: String): Property = MetaProperty(note1PropertyID, StringData(value))

  def getNote2MesaProperty(value: String): Property = MesaProperty(note2PropertyID, StringData(value).getDataID)

  def getExtraMesaProperty(value: String): Property = MesaProperty(extraPropertyID, StringData(value).getDataID)

  val getBondAmountMetaProperty: Property = MetaProperty(schema.constants.Properties.BondAmountProperty.id, NumberData(2560L))

  def getAuthenticationProperty(addresses: Seq[String]): Property = schema.constants.Properties.AuthenticationProperty.copy(data = ListData(addresses.map(x => AccAddressData(utilities.Crypto.convertAddressToAccAddressBytes(x)))))

  def getMantlePlaceIdentityID(id: String): IdentityID = {
    val immutables = Immutables(PropertyList(Seq(getOriginMetaProperty, getBondAmountMetaProperty, getIDMetaProperty(id), getExtraMesaProperty(""))))
    schema.utilities.ID.getIdentityID(classificationID = constants.Blockchain.MantlePlaceIdentityClassificationID, immutables = immutables)
  }

}
