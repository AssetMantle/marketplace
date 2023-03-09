package utilities

import schema.data.base.{NumberData, StringData}
import schema.id.base.{IdentityID, PropertyID, StringID}
import schema.list.PropertyList
import schema.property.Property
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified.Immutables

object Identity {

  private var anyPendingTx = false

  def anyTxInProgress: Boolean = anyPendingTx

  def setTxInProgress(): Unit = {
    anyPendingTx = true
  }

  def setTxComplete(): Unit = {
    anyPendingTx = false
  }

  private val idPropertyID = PropertyID(keyID = StringID("id"), typeID = constants.Data.StringDataTypeID)
  private val originPropertyID = PropertyID(keyID = StringID("origin"), typeID = constants.Data.StringDataTypeID)
  private val twitterPropertyID = PropertyID(keyID = StringID("twitter"), typeID = constants.Data.StringDataTypeID)
  private val note1PropertyID = PropertyID(keyID = StringID("note1"), typeID = constants.Data.StringDataTypeID)
  private val note2PropertyID = PropertyID(keyID = StringID("note2"), typeID = constants.Data.StringDataTypeID)
  private val extraPropertyID = PropertyID(keyID = StringID("extra"), typeID = constants.Data.StringDataTypeID)

  val getOriginMetaProperty: Property = MetaProperty(originPropertyID, StringData("MantlePlace").toAnyData)

  def getIDMetaProperty(value: String): Property = MetaProperty(idPropertyID, StringData(value).toAnyData)

  def getTwitterMetaProperty(value: String): Property = MetaProperty(twitterPropertyID, StringData(value).toAnyData)

  def getNote1MetaProperty(value: String): Property = MetaProperty(note1PropertyID, StringData(value).toAnyData)

  def getNote2MesaProperty(value: String): Property = MesaProperty(note2PropertyID, StringData(value).getDataID)

  def getExtraMesaProperty(value: String): Property = MesaProperty(extraPropertyID, StringData(value).getDataID)

  val getBondAmountMetaProperty: Property = MetaProperty(constants.Blockchain.BondAmountProperty.id, NumberData(2560L).toAnyData)

  def getMantlePlaceIdentityID(id: String): IdentityID = {
    val immutables = Immutables(PropertyList(Seq(getOriginMetaProperty, getBondAmountMetaProperty, getIDMetaProperty(id), getExtraMesaProperty(""))))
    utilities.ID.getIdentityID(classificationID = constants.Blockchain.MantlePlaceIdentityClassificationID, immutables = immutables)
  }

}
