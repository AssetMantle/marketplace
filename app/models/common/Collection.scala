package models.common

import play.api.libs.json.{Json, OFormat, Reads, Writes}
import schema.id.base.{PropertyID, StringID}
import schema.property.base.{MesaProperty, MetaProperty}

object Collection {

  case class Property(name: String, `type`: String, defaultValue: String, meta: Boolean = true, mutable: Boolean = false) {

    def getPropertyID: PropertyID = PropertyID(keyID = StringID(this.name), typeID = utilities.Data.getTypeID(this.`type`))

    def toMetaProperty: MetaProperty = MetaProperty(id = this.getPropertyID, data = utilities.Data.getData(`type` = this.`type`, value = this.defaultValue))

    def toMesaProperty: MesaProperty = MesaProperty(id = this.getPropertyID, dataID = utilities.Data.getDataID(`type` = this.`type`, value = this.defaultValue))

  }

  object Property {

    implicit val propertyWrites: Writes[Property] = Json.writes[Property]

    implicit val propertyReads: Reads[Property] = Json.reads[Property]

    implicit val propertyFormat: OFormat[Property] = Json.using[Json.WithDefaultValues].format[Property]
  }

  case class SocialProfile(name: String, url: String)

  object SocialProfile {
    implicit val writes: Writes[SocialProfile] = Json.writes[SocialProfile]

    implicit val reads: Reads[SocialProfile] = Json.reads[SocialProfile]
  }


}
