package utilities

import schema.data.base.{HeightData, IDData}
import schema.id.OwnableID
import schema.id.base.{ClassificationID, IdentityID, OrderID}
import schema.list.PropertyList
import schema.property.Property
import schema.property.base.MetaProperty
import schema.qualified.Immutables
import schema.types.Height


object Order {

  def getOrderID(classificationID: ClassificationID, properties: Seq[Property], makerID: IdentityID, makerOwnableID: OwnableID, makerOwnableSplit: BigDecimal, creationHeight: Long, takerID: IdentityID, takerOwnableID: OwnableID, takerOwnableSplit: BigDecimal): OrderID = {
    val immutables = Immutables(PropertyList(properties ++ Seq(
      constants.Blockchain.ExchangeRateProperty.copy(data = AttoNumber(takerOwnableSplit).quotientTruncate(AttoNumber(constants.Blockchain.SmallestDec)).quotientTruncate(AttoNumber(makerOwnableSplit)).toDecData.toAnyData),
      constants.Blockchain.CreationHeightProperty.copy(data = HeightData(Height(creationHeight)).toAnyData),
      constants.Blockchain.MakerOwnableIDProperty.copy(data = IDData(makerOwnableID.toAnyID).toAnyData),
      constants.Blockchain.TakerOwnableIDProperty.copy(data = IDData(takerOwnableID.toAnyID).toAnyData),
      constants.Blockchain.MakerIDProperty.copy(data = IDData(makerID.toAnyID).toAnyData),
      constants.Blockchain.TakerIDProperty.copy(data = IDData(takerID.toAnyID).toAnyData))))
    utilities.ID.getOrderID(classificationID = classificationID, immutables = immutables)
  }

  def getDefaultImmutableMetas(others: Seq[MetaProperty]): Seq[MetaProperty] = others ++ Seq(
    constants.Blockchain.ExchangeRateProperty,
    constants.Blockchain.CreationHeightProperty,
    constants.Blockchain.MakerOwnableIDProperty,
    constants.Blockchain.TakerOwnableIDProperty,
    constants.Blockchain.MakerIDProperty,
    constants.Blockchain.TakerIDProperty
  )

  def getDefaultMutableMetas(expiryHeight: Long, makerOwnableSplit: BigDecimal, others: Seq[MetaProperty]): Seq[MetaProperty] = others ++ Seq(
    constants.Blockchain.ExpiryHeightProperty.copy(data = HeightData(Height(expiryHeight)).toAnyData),
    constants.Blockchain.MakerOwnableSplitProperty.copy(data = AttoNumber(makerOwnableSplit).toDecData.toAnyData),
  )

}
