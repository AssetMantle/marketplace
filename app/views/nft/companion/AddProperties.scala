package views.nft.companion

import play.api.data.Form
import play.api.data.Forms.{mapping, seq}

object AddProperties {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_FILE_NAME.mapping,
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.NFT_PROPERTIES.name -> seq(
        mapping(
          constants.FormField.NFT_PROPERTY_NAME.mapping,
          constants.FormField.NFT_PROPERTY_VALUE.mapping
        )(Property.apply)(Property.unapply)
      )
    )(Data.apply)(Data.unapply))

  case class Property(name: String, `value`: String)

  case class Data(fileName: String, collectionId: String, properties: Seq[Property])
}
