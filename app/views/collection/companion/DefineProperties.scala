package views.collection.companion

import models.common.{Collection => commonCollection}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{mapping, seq}

object DefineProperties {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.SAVE_COLLECTION_DRAFT.mapping,
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

  case class Property(name: Option[String], propertyType: String, defaultValue: Option[String], mutable: Boolean, hide: Boolean) {

    def validate: Boolean = if (this.defaultValue.isDefined) constants.NFT.Data.isCastable(`type` = this.propertyType, value = this.defaultValue.get) else true

  }

  case class Data(collectionId: String, saveAsDraft: Boolean, properties: Seq[Property]) {

    def getSerializableProperties()(implicit module: String, logger: Logger): Seq[commonCollection.Property] = {
      val userDefinedProperties = this.properties.filter(_.name.isDefined).map(property => commonCollection.Property(name = property.name.get.trim, `type` = property.propertyType, defaultValue = property.defaultValue.getOrElse(""), mutable = property.mutable, meta = !property.hide))
      if (userDefinedProperties.map(_.validate).forall(identity)) userDefinedProperties else constants.Response.INVALID_NFT_PROPERTY.throwBaseException()
    }

  }

}
