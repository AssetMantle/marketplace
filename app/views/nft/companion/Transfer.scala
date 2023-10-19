package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Transfer {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_ID.mapping,
      constants.FormField.TO_ACCOUNT_ID.mapping,
      constants.FormField.NFT_TRANSFER_AMOUNT.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftId: String, toAccountId: String, amount: Long, password: String)

}
