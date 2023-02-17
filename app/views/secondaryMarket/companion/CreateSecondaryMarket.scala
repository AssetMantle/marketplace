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
      constants.FormField.SECONDARY_MARKET_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.createSecondaryMarket))

  case class Data(nftId: String, price: MicroNumber, endEpoch: Int) {

    def toNewSecondaryMarket(collectionId: String): SecondaryMarket = SecondaryMarket(id = utilities.IdGenerator.getRandomHexadecimal, collectionId = collectionId, price = this.price, denom = constants.Blockchain.StakingToken, endTimeEpoch = this.endEpoch)

  }
}
