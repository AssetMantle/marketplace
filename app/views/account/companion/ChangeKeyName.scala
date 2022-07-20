package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ChangeKeyName {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WALLET_ADDRESS.mapping,
      constants.FormField.KEY_NAME.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply)
  )

  case class Data(address: String, keyName: String, password: String)

}
