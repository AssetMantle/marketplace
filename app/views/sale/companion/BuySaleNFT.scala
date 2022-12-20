package views.sale.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object BuySaleNFT {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.SALE_ID.mapping,
      constants.FormField.SALE_BUY_NFT_NUMBER.mapping,
      constants.FormField.GAS_AMOUNT.mapping,
      constants.FormField.GAS_PRICE.mapping,
      constants.FormField.PASSWORD.mapping
    )(Data.apply)(Data.unapply))

  case class Data(saleId: String, buyNFTs: Int, gasAmount: Int, gasPrice: Double, password: String)
}
