package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ChangePassword {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.OLD_PASSWORD.mapping,
      constants.FormField.CHANGE_PASSWORD.mapping,
      constants.FormField.CHANGE_CONFIRM_PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.changePasswordConstraint))

  case class Data(oldPassword: String, newPassword: String, confirmNewPassword: String)

}
