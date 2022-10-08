package models.common

import play.api.libs.json.{Json, OWrites, Reads}

object Notification {
  case class Template(name: String, parameters: Seq[String])

  object Template {
    implicit val templateReads: Reads[Template] = Json.reads[Template]

    implicit val templateWrites: OWrites[Template] = Json.writes[Template]
  }
}
