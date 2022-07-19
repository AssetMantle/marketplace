package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object UnmanagedAddress {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.UNMANAGED_ADDRESS_WALLET_NAME.name-> constants.FormField.UNMANAGED_ADDRESS_WALLET_NAME.field,
      constants.FormField.UNMANAGED_ADDRESS_NEW_ADDRESS.name-> constants.FormField.UNMANAGED_ADDRESS_NEW_ADDRESS.field,
      constants.FormField.UNMANAGED_ADDRESS_DEFAULT_MEMO.name-> constants.FormField.UNMANAGED_ADDRESS_DEFAULT_MEMO.field,
      constants.FormField.UNMANAGED_ADDRESS_PASSWORD.name-> constants.FormField.UNMANAGED_ADDRESS_PASSWORD.field,
    )(Data.apply)(Data.unapply))

  case class Data(walletName: String, address: String, defaultMemo: String, password: String)

}