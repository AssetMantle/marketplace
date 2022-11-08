package models.common

import play.api.libs.json.{Json, OFormat, Reads, Writes}

object Collection {

  case class Property(name: String, `type`: String, defaultValue: String, meta: Boolean = true, mutable: Boolean = false)

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
