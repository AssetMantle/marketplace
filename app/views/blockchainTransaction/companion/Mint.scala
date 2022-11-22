package views.blockchainTransaction.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Mint {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_FILE_NAME.mapping,
      constants.FormField.GAS_AMOUNT.mapping,
      constants.FormField.GAS_PRICE.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.mintConstraint))

  case class Data(nftId: String, gasAmount: Int, gasPrice: String, password: String)

}