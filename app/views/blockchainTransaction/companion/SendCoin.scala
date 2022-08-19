package views.blockchainTransaction.companion

import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object SendCoin {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.FROM_ADDRESS.mapping,
      constants.FormField.TO_ADDRESS.mapping,
      constants.FormField.SEND_COIN_AMOUNT.mapping,
      constants.FormField.GAS_AMOUNT.mapping,
      constants.FormField.GAS_PRICE.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.sendCoinConstraint))

  case class Data(fromAddress: String, toAddress: String, sendCoinAmount: MicroNumber, gasAmount: Int, gasPrice: MicroNumber, password: String)

}