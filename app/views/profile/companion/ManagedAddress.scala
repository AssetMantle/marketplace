package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ManagedAddress {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.MANAGED_ADDRESS_WALLET_NAME.name-> constants.FormField.MANAGED_ADDRESS_WALLET_NAME.field,
      constants.FormField.MANAGED_ADDRESS_NEW_ADDRESS.name-> constants.FormField.MANAGED_ADDRESS_NEW_ADDRESS.field,
      constants.FormField.MANAGED_ADDRESS_PASSWORD.name-> constants.FormField.MANAGED_ADDRESS_PASSWORD.field,
      constants.FormField.SEED_PHRASE_1.name-> constants.FormField.SEED_PHRASE_1.field,
      constants.FormField.SEED_PHRASE_2.name-> constants.FormField.SEED_PHRASE_2.field,
  )(Data.apply)(Data.unapply))

  case class Data(walletName: String, address: String, password: String, seed1: String, seed2: String)

}