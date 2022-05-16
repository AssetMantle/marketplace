package views.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object SignUp {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.name -> constants.FormField.USERNAME.field,
      constants.FormField.PASSWORD.name -> constants.FormField.PASSWORD.field,
      constants.FormField.CONFIRM_PASSWORD.name -> constants.FormField.CONFIRM_PASSWORD.field,
      constants.FormField.NAME.name -> constants.FormField.NAME.field
    )(Data.apply)(Data.unapply)
  )

  case class Data(userName: String,password: String, confirmPassword: String,name: String)
}