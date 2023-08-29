package utilities

import schema.data.base.{HeightData, NumberData}
import schema.id.base.{AssetID, IdentityID, OrderID}


object Order {

  def getOrderID(makerID: IdentityID, makerAssetID: AssetID, makerSplit: NumberData, expiryHeight: HeightData, takerAssetID: AssetID, takerSplit: NumberData): OrderID = {
    schema.document.PutOrder.getPutOrderID(
      makerID = makerID,
      makerAssetID = makerAssetID,
      takerAssetID = takerAssetID,
      makerSplit = makerSplit,
      takerSplit = takerSplit,
      expiryHeight = expiryHeight
    )
  }

}
