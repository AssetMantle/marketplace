package views.collection.companion

import models.master.CollectionProperty
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, seq}

object DefineProperties {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.COLLECTION_PROPERTIES.name -> seq(
        mapping(
          constants.FormField.COLLECTION_PROPERTY_NAME.optionalMapping,
          constants.FormField.COLLECTION_PROPERTY_TYPE.mapping,
          constants.FormField.COLLECTION_PROPERTY_FIXED_VALUE.optionalMapping,
          constants.FormField.COLLECTION_PROPERTY_REQUIRED.mapping,
          constants.FormField.COLLECTION_PROPERTY_MUTABLE.mapping,
          constants.FormField.COLLECTION_PROPERTY_HIDE_VALUE.mapping,
        )(Property.apply)(Property.unapply)
      )
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.defineCollectionPropertiesConstraint))

  case class Property(name: Option[String], propertyType: String, fixedValue: Option[String], required: Boolean, mutable: Boolean, hideValue: Boolean)

  case class Data(collectionId: String, properties: Seq[Property]) {

    def getSerializableProperties(id: String) = this.properties.filter(_.name.isDefined).map(property => CollectionProperty(id = id, propertyName = property.name.get, propertyType = property.propertyType, required = property.required, mutable = property.mutable, fixedValue = property.fixedValue, hideValue = property.hideValue))

  }

}
