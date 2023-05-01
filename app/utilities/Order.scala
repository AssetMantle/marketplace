package utilities

import schema.data.base.{DecData, HeightData, IDData, NumberData}
import schema.id.OwnableID
import schema.id.base.{ClassificationID, IdentityID, OrderID}
import schema.list.PropertyList
import schema.property.Property
import schema.property.base.MetaProperty
import schema.qualified.Immutables
import schema.types.Height


object Order {

  def getOrderID(classificationID: ClassificationID, properties: Seq[Property], makerID: IdentityID, makerOwnableID: OwnableID, makerOwnableSplit: BigInt, creationHeight: Long, takerID: IdentityID, takerOwnableID: OwnableID, takerOwnableSplit: BigInt): OrderID = {
    val immutables = Immutables(PropertyList(properties ++ Seq(
      schema.constants.Properties.ExchangeRateProperty.copy(data = DecData(BigDecimal(takerOwnableSplit)).quotientTruncate(DecData(BigDecimal(makerOwnableSplit)))),
      schema.constants.Properties.CreationHeightProperty.copy(data = HeightData(Height(creationHeight))),
      schema.constants.Properties.MakerOwnableIDProperty.copy(data = IDData(makerOwnableID)),
      schema.constants.Properties.TakerOwnableIDProperty.copy(data = IDData(takerOwnableID)),
      schema.constants.Properties.MakerIDProperty.copy(data = IDData(makerID)),
      schema.constants.Properties.TakerIDProperty.copy(data = IDData(takerID))))
    )
    schema.utilities.ID.getOrderID(classificationID = classificationID, immutables = immutables)
  }

  def getDefaultImmutableMetas(others: Seq[MetaProperty]): Seq[MetaProperty] = others ++ Seq(
    schema.constants.Properties.ExchangeRateProperty,
    schema.constants.Properties.CreationHeightProperty,
    schema.constants.Properties.MakerOwnableIDProperty,
    schema.constants.Properties.TakerOwnableIDProperty,
    schema.constants.Properties.MakerIDProperty,
    schema.constants.Properties.TakerIDProperty
  )

  def getDefaultMutableMetas(expiryHeight: Long, makerOwnableSplit: BigInt, others: Seq[MetaProperty]): Seq[MetaProperty] = others ++ Seq(
    schema.constants.Properties.ExpiryHeightProperty.copy(data = HeightData(Height(expiryHeight))),
    schema.constants.Properties.MakerOwnableSplitProperty.copy(data = NumberData(makerOwnableSplit)),
  )

}
