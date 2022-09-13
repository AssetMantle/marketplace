package views.setting.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AddManagedKey {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.MANAGED_KEY_NAME.mapping,
      constants.FormField.MANAGED_KEY_ADDRESS.mapping,
      constants.FormField.PASSWORD.mapping,
      constants.FormField.SEEDS.mapping,
  )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.addManagedKeyConstraint))

  case class Data(keyName: String, address: String, password: String, seeds: String)

}