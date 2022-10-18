package models.common

import play.api.libs.json.{Json, OFormat, Reads, Writes}

object NFT {

  case class Property(name: String, `type`: String, `value`: String,  meta: Boolean = true, mutable: Boolean = false)

  object Property {

    implicit val propertyWrites: Writes[Property] = Json.writes[Property]

    implicit val propertyReads: Reads[Property] = Json.reads[Property]

    implicit val propertyFormat: OFormat[Property] = Json.using[Json.WithDefaultValues].format[Property]
  }

}
