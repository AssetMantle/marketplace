package views.blockchainTransaction.companion

import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object FundWallet {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.FROM_KEPLR_ADDRESS.mapping,
      constants.FormField.TO_ADDRESS.mapping,
      constants.FormField.SEND_COIN_AMOUNT.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(fromAddress: String, toAddress: String, sendCoinAmount: MicroNumber)

}