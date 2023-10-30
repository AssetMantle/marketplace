package views.collection.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object UpdateRoyalty {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.COLLECTION_ROYALTY.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(collectionId: String, royalty: BigDecimal)

}
