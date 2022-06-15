package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ForgotPasswordUsername {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.name -> constants.FormField.USERNAME.field
    )(Data.apply)(Data.unapply))

  case class Data(username: String)

}
