package views.collection.companion

import models.common.{Collection => commonCollection}
import play.api.data.Form
import play.api.data.Forms.{mapping, seq}

object DefineCapabilities {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_CAPABILITY_FRACTIONALISABLE.mapping,
      constants.FormField.COLLECTION_CAPABILITY_LOCKABLE.mapping,
      constants.FormField.COLLECTION_CAPABILITY_BURNABLE.mapping,

    )(Data.apply)(Data.unapply))

  case class Data(fractionalisable: Boolean, lockable: Boolean,burnable: Boolean) {
  }

}
