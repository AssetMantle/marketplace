package views.wallet.companion

import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object Wrap {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WRAP_COIN_AMOUNT.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(wrapCoinAmount: MicroNumber, password: String)

}
