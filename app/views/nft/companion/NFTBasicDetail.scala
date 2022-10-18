package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object NFTBasicDetail {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_NAME.mapping,
      constants.FormField.NFT_DESCRIPTION.mapping,
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.NFT_FILE_NAME.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(name: String, description: String, collectionId: String, fileName: String)

}
