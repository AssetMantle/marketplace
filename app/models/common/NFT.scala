package models.common

import models.master.NFTProperty
import play.api.Logger
import play.api.libs.functional.syntax.toAlternativeOps
import play.api.libs.json._

import scala.util.Try

object NFT {

  implicit val module: String = constants.Module.NFT_PROPERTY

  implicit val logger: Logger = Logger(this.getClass)

  @deprecated
  val SMALL_STRING = "string"
  @deprecated
  val SMALL_NUMBER = "number"
  @deprecated
  val NUMBER = "NUMBER"

  abstract class BaseNFTProperty {
    val name: String
    val meta: Boolean
    val mutable: Boolean

    def `type`: String

    def valueAsString: String

    def toNFTProperty(fileName: String): NFTProperty = NFTProperty(fileName = fileName, name = this.name, `type` = this.`type`, `value` = if (this.valueAsString == "") "None" else this.valueAsString, meta = this.meta, mutable = this.mutable)
  }

  implicit val baseNFTPropertyWrites: Writes[BaseNFTProperty] = {
    case stringProperty: StringProperty => Json.toJson(stringProperty)
    case decimalProperty: DecimalProperty => Json.toJson(decimalProperty)
    case booleanProperty: BooleanProperty => Json.toJson(booleanProperty)
    case _ => constants.Response.NFT_PROPERTY_TYPE_NOT_FOUND.throwBaseException()
  }

  implicit val baseNFTPropertyReads: Reads[BaseNFTProperty] = {
    Json.format[DecimalProperty].map(x => x: BaseNFTProperty) or
      Json.format[StringProperty].map(x => x: BaseNFTProperty) or
      Json.format[BooleanProperty].map(x => x: BaseNFTProperty)
  }

  case class StringProperty(name: String, `value`: String, meta: Boolean, mutable: Boolean) extends BaseNFTProperty {
    def `type`: String = constants.NFT.Data.STRING

    def valueAsString: String = this.`value`
  }

  implicit val stringPropertyReads: Reads[StringProperty] = Json.reads[StringProperty]

  implicit val stringPropertyWrites: Writes[StringProperty] = Json.writes[StringProperty]

  case class DecimalProperty(name: String, `value`: BigDecimal, meta: Boolean, mutable: Boolean) extends BaseNFTProperty {
    def `type`: String = constants.NFT.Data.DECIMAL

    def valueAsString: String = this.`value`.toString()
  }

  implicit val decimalPropertyReads: Reads[DecimalProperty] = Json.reads[DecimalProperty]

  implicit val decimalPropertyWrites: Writes[DecimalProperty] = Json.writes[DecimalProperty]

  case class BooleanProperty(name: String, `value`: Boolean, meta: Boolean, mutable: Boolean) extends BaseNFTProperty {
    def `type`: String = constants.NFT.Data.BOOLEAN

    def valueAsString: String = this.`value`.toString
  }

  implicit val booleanPropertyReads: Reads[BooleanProperty] = Json.reads[BooleanProperty]

  implicit val booleanPropertyWrites: Writes[BooleanProperty] = Json.writes[BooleanProperty]

  case class Property(name: String, `type`: String, `value`: String, meta: Boolean = true, mutable: Boolean = false) {

    def valid: Boolean = this.`type` match {
      case constants.NFT.Data.STRING | SMALL_STRING => true
      case constants.NFT.Data.DECIMAL | NUMBER | SMALL_NUMBER => Try(BigDecimal(this.`value`)).isSuccess
      case constants.NFT.Data.BOOLEAN => this.`value` == constants.NFT.Data.TRUE || this.`value` == constants.NFT.Data.SMALL_TRUE || this.`value` == constants.NFT.Data.FALSE || this.`value` == constants.NFT.Data.SMALL_FALSE
      case _ => constants.Response.NFT_PROPERTY_TYPE_NOT_FOUND.throwBaseException()
    }

    def toBaseNFTProperty: BaseNFTProperty = if (this.valid) this.`type` match {
      case constants.NFT.Data.STRING | SMALL_STRING => StringProperty(name = this.name, `value` = this.`value`, meta = this.meta, mutable = this.mutable)
      case constants.NFT.Data.DECIMAL | NUMBER | SMALL_NUMBER => DecimalProperty(name = this.name, `value` = BigDecimal(this.`value`), meta = this.meta, mutable = this.mutable)
      case constants.NFT.Data.BOOLEAN => BooleanProperty(name = this.name, `value` = this.`value` == constants.NFT.Data.TRUE || this.`value` == constants.NFT.Data.SMALL_TRUE, meta = this.meta, mutable = this.mutable)
      case _ => constants.Response.NFT_PROPERTY_TYPE_NOT_FOUND.throwBaseException()
    } else constants.Response.INVALID_NFT_PROPERTY.throwBaseException()
  }

  object Property {

    implicit val propertyWrites: Writes[Property] = Json.writes[Property]

    implicit val propertyReads: Reads[Property] = Json.reads[Property]

    implicit val propertyFormat: OFormat[Property] = Json.using[Json.WithDefaultValues].format[Property]
  }

}
