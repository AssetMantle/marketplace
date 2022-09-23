package views.blockchainTransaction.companion

import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object SendCoin {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.VALIDATOR.mapping,
      constants.FormField.DELEGATE_AMOUNT.mapping,
      constants.FormField.AVAILABLE_AMOUNT.mapping,
      constants.FormField.GAS_AMOUNT.mapping,
      constants.FormField.GAS_PRICE.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.sendCoinConstraint))

  case class Data(validator: String, delegateAmount: MicroNumber, availableAmount: MicroNumber, gasAmount: Int, gasPrice: String, password: String)

}