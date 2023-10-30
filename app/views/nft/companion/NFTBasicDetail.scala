package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object NFTBasicDetail {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_NAME.mapping,
      constants.FormField.NFT_DESCRIPTION.mapping,
      constants.FormField.NFT_TAGS.mapping,
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.NFT_ID.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.NFTBasicDetailConstraint))

  case class Data(name: String, description: String, tags: String, collectionId: String, nftId: String) {
    def getTags: Option[Seq[String]] = if (this.tags == "") None else Option(tags.split(constants.NFT.Tags.Separator).filter(_ != ""))
  }

}
