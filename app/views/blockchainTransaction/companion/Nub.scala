package views.blockchainTransaction.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Nub {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.GAS_AMOUNT.mapping,
      constants.FormField.GAS_PRICE.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(gasAmount: Int, gasPrice: BigDecimal, password: String)

}