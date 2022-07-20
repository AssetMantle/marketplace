package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AddCustodialKey {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WALLET_ADDRESS.mapping,
      constants.FormField.SEEDS.mapping,
      constants.FormField.KEY_NAME.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.addCustodialKeyConstraint)
  )

  case class Data(address: String, seeds: String, keyName: String, password: String)

}
