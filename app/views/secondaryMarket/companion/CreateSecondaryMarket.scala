package views.secondaryMarket.companion

import models.master.SecondaryMarket
import play.api.data.Form
import play.api.data.Forms.mapping
import schema.id.base.OrderID
import utilities.MicroNumber

object CreateSecondaryMarket {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_ID.mapping,
      constants.FormField.SELL_QUANTITY.mapping,
      constants.FormField.SECONDARY_MARKET_PRICE.mapping,
      constants.FormField.SECONDARY_MARKET_END_HOURS.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftId: String, sellQuantity: Long, price: MicroNumber, endHours: Int, password: String) {

    def toNewSecondaryMarket(collectionId: String, sellerId: String, orderID: OrderID): SecondaryMarket = SecondaryMarket(id = utilities.IdGenerator.getRandomHexadecimal, orderId = orderID.asString, nftId = this.nftId, collectionId = collectionId, sellerId = sellerId, price = this.price, quantity = this.sellQuantity, denom = constants.Blockchain.StakingToken, endHours = this.endHours, externallyMade = false, completed = false, cancelled = false, expired = false, status = None)

  }
}
