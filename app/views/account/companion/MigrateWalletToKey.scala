package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object MigrateWalletToKey {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.mapping,
      constants.FormField.WALLET_ADDRESS.mapping,
      constants.FormField.SEED_PHRASE_1.mapping,
      constants.FormField.SEED_PHRASE_2.mapping,
      constants.FormField.SEED_PHRASE_3.mapping,
      constants.FormField.SEED_PHRASE_4.mapping,
      constants.FormField.SIGNUP_PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying())

  case class Data(username: String, walletAddress: String, seed1: String, seed2: String, seed3: String, seed4: String, password: String)

}
