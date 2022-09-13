package views.setting.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ChangeManagedToUnmanaged {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.MANAGED_KEY_ADDRESS.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(address: String, password: String)

}