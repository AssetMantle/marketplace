package views.collection.companion

import models.common.{Collection => commonCollection}
import play.api.data.Form
import play.api.data.Forms.{mapping, seq}

object DefineProperties {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.SAVE_COLLECTION_DRAFT.mapping,
      constants.FormField.FRACTIONALIZED_NFT.mapping,
      constants.FormField.COLLECTION_PROPERTIES.name -> seq(
        mapping(
          constants.FormField.COLLECTION_PROPERTY_NAME.optionalMapping,
          constants.FormField.COLLECTION_PROPERTY_TYPE.mapping,
          constants.FormField.COLLECTION_PROPERTY_DEFAULT_VALUE.optionalMapping,
          constants.FormField.COLLECTION_PROPERTY_MUTABLE.mapping,
          constants.FormField.COLLECTION_PROPERTY_HIDE.mapping,
        )(Property.apply)(Property.unapply).verifying(constants.FormConstraint.collectionPropertiesConstraint)
      )
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.defineCollectionPropertiesConstraint))

  case class Property(name: Option[String], propertyType: String, defaultValue: Option[String], mutable: Boolean, hide: Boolean)

  case class Data(collectionId: String, saveAsDraft: Boolean, fractionalizedNFT: Boolean, properties: Seq[Property]) {

    def getSerializableProperties: Seq[commonCollection.Property] = {
      val userDefinedProperties = this.properties.filter(_.name.isDefined).map(property => commonCollection.Property(name = property.name.get.trim, `type` = property.propertyType, defaultValue = property.defaultValue.getOrElse(""), mutable = property.mutable, meta = !property.hide))
      if (this.fractionalizedNFT) userDefinedProperties :+ commonCollection.Property(name = "supply", `type` = constants.NFT.Data.NUMBER, defaultValue = "0", mutable = false, meta = true)
      else userDefinedProperties
    }

  }

}
