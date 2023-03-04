package views.secondaryMarket.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Buy {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_ID.mapping,
      constants.FormField.SECONDARY_MARKET_ID.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftId: String, secondaryMarketId: String, password: String)
}
