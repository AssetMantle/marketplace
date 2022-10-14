package views.collection.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Confirm {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(collectionId: String)

}
