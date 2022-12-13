package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ChangeActiveKey {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WALLET_ADDRESS.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(address: String)

}
