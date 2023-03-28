package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Mint {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_ID.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftId: String, password: String)

}
