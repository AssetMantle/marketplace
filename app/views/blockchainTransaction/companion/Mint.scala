package views.blockchainTransaction.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Mint {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_ID.mapping,
      constants.FormField.GAS_AMOUNT.mapping,
      constants.FormField.GAS_PRICE.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftId: String, gasAmount: Int, gasPrice: Double, password: String)

}