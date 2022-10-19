package views.nft.companion

import models.common.Collection.{Property => collectionProperty}
import models.common.NFT.{Property => nftProperty}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{mapping, seq}

object SetProperties {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.NFT_FILE_NAME.mapping,
      constants.FormField.SAVE_NFT_DRAFT.mapping,
      constants.FormField.NFT_PROPERTIES.name -> seq(
        mapping(
          constants.FormField.NFT_PROPERTY_NAME.mapping,
          constants.FormField.NFT_PROPERTY_VALUE.mapping,
        )(Property.apply)(Property.unapply))
    )(Data.apply)(Data.unapply))


  case class Property(name: String, `value`: String)

  case class Data(collectionId: String, fileName: String, saveNFTDraft: Boolean, properties: Seq[Property]) {

    def getNFTProperties(collectionProperties: Seq[collectionProperty])(implicit logger: Logger, module: String): Seq[nftProperty] = this.properties.map { x =>
      val collectionProperty = collectionProperties.find(_.name == x.name).getOrElse(constants.Response.NFT_PROPERTY_NOT_FOUND.throwBaseException())
      nftProperty(name = x.name, `type` = collectionProperty.name, `value` = x.`value`, meta = collectionProperty.meta, mutable = collectionProperty.mutable)
    }

  }


}
