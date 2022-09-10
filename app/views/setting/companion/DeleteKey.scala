package views.setting.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object DeleteKey {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WALLET_ADDRESS.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(address: String, password: String)

}