package views.collection.companion

import models.master.CollectionProperty
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, seq}

object DefineProperties {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.COLLECTION_PROPERTY.name -> seq(optional(mapping(
        constants.FormField.COLLECTION_PROPERTY_NAME.mapping,
        constants.FormField.COLLECTION_PROPERTY_TYPE.mapping,
        constants.FormField.COLLECTION_PROPERTY_FIXED_VALUE.optionalMapping,
        constants.FormField.COLLECTION_PROPERTY_REQUIRED.mapping,
        constants.FormField.COLLECTION_PROPERTY_MUTABLE.mapping,
        constants.FormField.COLLECTION_PROPERTY_HIDE_VALUE.mapping,
      )(Property.apply)(Property.unapply)))
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.defineCollectionPropertiesConstraint))

  case class Property(name: String, propertyType: String, fixedValue: Option[String], required: Boolean, mutable: Boolean, hideValue: Boolean) {

    def toSerializable(id: String): CollectionProperty = CollectionProperty(id = id, propertyName = name, propertyType = propertyType, required = required, mutable = mutable, fixedValue = fixedValue, hideValue = hideValue)

  }

  case class Data(collectionId: String, properties: Seq[Option[Property]])

}
