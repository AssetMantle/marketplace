package views.wallet.companion

import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object SendCoin {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.TO_ADDRESS.mapping,
      constants.FormField.SEND_COIN_AMOUNT.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(toAddress: String, sendCoinAmount: MicroNumber, password: String)

}
