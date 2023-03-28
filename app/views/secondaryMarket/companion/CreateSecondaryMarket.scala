package views.secondaryMarket.companion

import models.master.SecondaryMarket
import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object CreateSecondaryMarket {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_ID.mapping,
      constants.FormField.SECONDARY_MARKET_PRICE.mapping,
      constants.FormField.SECONDARY_MARKET_END_HOURS.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftId: String, price: MicroNumber, endHours: Int, password: String) {

    def toNewSecondaryMarket(collectionId: String, sellerId: String, quantity: Int): SecondaryMarket = SecondaryMarket(id = utilities.IdGenerator.getRandomHexadecimal, orderId = None, nftId = this.nftId, collectionId = collectionId, sellerId = sellerId, price = this.price, quantity = quantity, denom = constants.Blockchain.StakingToken, endHours = this.endHours, externallyMade = false, delete = false)

  }
}
