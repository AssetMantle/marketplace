package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object NFTTags {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_TAGS.mapping,
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.NFT_ID.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.nftTagsConstraint))

  case class Data(tags: String, collectionId: String, nftId: String) {

    def getTags: Seq[String] = tags.split(constants.NFT.Tags.Separator).filter(_ != "")
  }

}
