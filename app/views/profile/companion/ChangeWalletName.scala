package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ChangeWalletName {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WALLET_NAME_PREVIOUS.name-> constants.FormField.WALLET_NAME_PREVIOUS.field,
      constants.FormField.WALLET_NAME_NEW.name-> constants.FormField.WALLET_NAME_NEW.field
    )(Data.apply)(Data.unapply))

  case class Data(previousName: String, NewName: String)

}