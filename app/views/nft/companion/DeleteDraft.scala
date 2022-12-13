package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object DeleteDraft {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_ID.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftFileName: String)

}
