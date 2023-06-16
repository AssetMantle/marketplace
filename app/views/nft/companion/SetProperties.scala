package views.nft.companion

import models.common.Collection.{Property => collectionProperty}
import models.common.NFT.{BaseNFTProperty, Property => nftProperty}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{mapping, seq}

object SetProperties {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.NFT_ID.mapping,
      constants.FormField.SAVE_NFT_DRAFT.mapping,
      constants.FormField.NFT_PROPERTIES.name -> seq(
        mapping(
          constants.FormField.NFT_PROPERTY_NAME.mapping,
          constants.FormField.NFT_PROPERTY_VALUE.mapping,
        )(Property.apply)(Property.unapply))
    )(Data.apply)(Data.unapply))


  case class Property(name: String, value: String)

  case class Data(collectionId: String, nftId: String, saveNFTDraft: Boolean, properties: Seq[Property]) {

    def validate(collectionProperties: Seq[collectionProperty])(implicit logger: Logger, module: String): Boolean = {
      val collectionNameTypeMap = collectionProperties.map(x => x.name -> x.`type`).toMap
      this.properties.map(_.name).toSet.equals(collectionProperties.map(_.name).toSet) && this.properties.forall(x => collectionNameTypeMap.get(x.name).fold(false)(y => constants.NFT.Data.isCastable(`type` = y, value = x.value)))
    }

    def getNFTProperties(collectionProperties: Seq[collectionProperty])(implicit logger: Logger, module: String): Seq[BaseNFTProperty] = if (this.validate(collectionProperties)) this.properties.map { x =>
      val collectionProperty = collectionProperties.find(_.name == x.name).getOrElse(constants.Response.NFT_PROPERTY_NOT_FOUND.throwBaseException())
      nftProperty(name = x.name, `type` = collectionProperty.`type`, value = x.value, meta = collectionProperty.meta, mutable = collectionProperty.mutable).toBaseNFTProperty
    } else constants.Response.INVALID_NFT_PROPERTY.throwBaseException()

  }


}
