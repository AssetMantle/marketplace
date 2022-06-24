package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object WalletMnemonics {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.name -> constants.FormField.USERNAME.field,
      constants.FormField.WALLET_ADDRESS.name -> constants.FormField.WALLET_ADDRESS.field,
      constants.FormField.SEED_PHRASE_1.name -> constants.FormField.SEED_PHRASE_1.field,
      constants.FormField.SEED_PHRASE_2.name -> constants.FormField.SEED_PHRASE_2.field,
      constants.FormField.SEED_PHRASE_3.name -> constants.FormField.SEED_PHRASE_3.field,
      constants.FormField.SEED_PHRASE_4.name -> constants.FormField.SEED_PHRASE_4.field,
    )(Data.apply)(Data.unapply))

  case class Data(username: String, walletAddress: String, seed1: String, seed2: String, seed3: String, seed4: String)

}
