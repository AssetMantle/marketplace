package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object VerifyMnemonics {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.CONFIRM_USERNAME.mapping,
      constants.FormField.WALLET_ADDRESS.mapping,
      constants.FormField.SEED_PHRASE_1.mapping,
      constants.FormField.SEED_PHRASE_2.mapping,
      constants.FormField.SEED_PHRASE_3.mapping,
      constants.FormField.SEED_PHRASE_4.mapping,
      constants.FormField.SIGNUP_PASSWORD.mapping,
      constants.FormField.SIGNUP_CONFIRM_PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.verifyMnemonicsConstraint))

  case class Data(username: String, walletAddress: String, seed1: String, seed2: String, seed3: String, seed4: String, password: String, confirmPassword: String)

}
