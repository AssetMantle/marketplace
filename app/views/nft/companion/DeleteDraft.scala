package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object DeleteDraft {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_FILE_NAME.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftFileName: String)

}
