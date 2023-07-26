package models.common

import models.master.NFTProperty
import play.api.Logger
import play.api.libs.functional.syntax.toAlternativeOps
import play.api.libs.json._

object NFT {

  implicit val module: String = constants.Module.NFT_PROPERTY

  implicit val logger: Logger = Logger(this.getClass)

  // Used to store all properties in one row 
  abstract class BaseNFTProperty {
    val name: String
    val meta: Boolean
    val mutable: Boolean

    def `type`: String

    def valueAsString: String

    def toNFTProperty(nftId: String): NFTProperty = NFTProperty(nftId = nftId, name = this.name, `type` = this.`type`, value = this.valueAsString, meta = this.meta, mutable = this.mutable)
  }

  object BaseNFTProperty {

    def apply(NFTProperty: NFTProperty): BaseNFTProperty = NFTProperty.`type` match {
      case constants.NFT.Data.STRING => StringProperty(name = NFTProperty.name, value = NFTProperty.value, meta = NFTProperty.meta, mutable = NFTProperty.mutable)
      case constants.NFT.Data.NUMBER => NumberProperty(name = NFTProperty.name, value = BigInt(NFTProperty.value), meta = NFTProperty.meta, mutable = NFTProperty.mutable)
      case constants.NFT.Data.BOOLEAN => BooleanProperty(name = NFTProperty.name, value = NFTProperty.value.toBoolean, meta = NFTProperty.meta, mutable = NFTProperty.mutable)
      case constants.NFT.Data.DECIMAL => DecimalProperty(name = NFTProperty.name, value = BigDecimal(NFTProperty.value), meta = NFTProperty.meta, mutable = NFTProperty.mutable)
    }

  }

  case class StringProperty(name: String, value: String, meta: Boolean, mutable: Boolean) extends BaseNFTProperty {
    def `type`: String = constants.NFT.Data.STRING

    def valueAsString: String = this.value
  }

  implicit val stringPropertyReads: Reads[StringProperty] = Json.reads[StringProperty]

  implicit val stringPropertyWrites: Writes[StringProperty] = Json.writes[StringProperty]

  case class DecimalProperty(name: String, value: BigDecimal, meta: Boolean, mutable: Boolean) extends BaseNFTProperty {
    def `type`: String = constants.NFT.Data.DECIMAL

    def valueAsString: String = this.value.toString
  }

  implicit val decimalPropertyReads: Reads[DecimalProperty] = Json.reads[DecimalProperty]

  implicit val decimalPropertyWrites: Writes[DecimalProperty] = Json.writes[DecimalProperty]


  case class NumberProperty(name: String, value: BigInt, meta: Boolean, mutable: Boolean) extends BaseNFTProperty {
    def `type`: String = constants.NFT.Data.NUMBER

    def valueAsString: String = this.value.toString
  }

  implicit val numberPropertyReads: Reads[NumberProperty] = Json.reads[NumberProperty]

  implicit val numberPropertyWrites: Writes[NumberProperty] = Json.writes[NumberProperty]

  case class BooleanProperty(name: String, value: Boolean, meta: Boolean, mutable: Boolean) extends BaseNFTProperty {
    def `type`: String = constants.NFT.Data.BOOLEAN

    def valueAsString: String = this.value.toString
  }

  implicit val booleanPropertyReads: Reads[BooleanProperty] = Json.reads[BooleanProperty]

  implicit val booleanPropertyWrites: Writes[BooleanProperty] = Json.writes[BooleanProperty]

  implicit val baseNFTPropertyWrites: Writes[BaseNFTProperty] = {
    case stringProperty: StringProperty => Json.toJson(stringProperty)
    case numberProperty: NumberProperty => Json.toJson(numberProperty)
    case booleanProperty: BooleanProperty => Json.toJson(booleanProperty)
    case decimalProperty: DecimalProperty => Json.toJson(decimalProperty)
    case _ => constants.Response.NFT_PROPERTY_TYPE_NOT_FOUND.throwBaseException()
  }

  implicit val baseNFTPropertyReads: Reads[BaseNFTProperty] = {
    Json.format[NumberProperty].map(x => x: BaseNFTProperty) or
      Json.format[StringProperty].map(x => x: BaseNFTProperty) or
      Json.format[DecimalProperty].map(x => x: BaseNFTProperty) or
      Json.format[BooleanProperty].map(x => x: BaseNFTProperty)
  }

  case class Property(name: String, `type`: String, value: String, meta: Boolean = true, mutable: Boolean = false) {

    def valid: Boolean = utilities.Properties.validateTypeValue(`type` = this.`type`, value = this.value)

    def toBaseNFTProperty: BaseNFTProperty = if (this.valid) this.`type` match {
      case constants.NFT.Data.STRING => StringProperty(name = this.name, value = this.value, meta = this.meta, mutable = this.mutable)
      case constants.NFT.Data.NUMBER => NumberProperty(name = this.name, value = BigInt(this.value), meta = this.meta, mutable = this.mutable)
      case constants.NFT.Data.DECIMAL => DecimalProperty(name = this.name, value = BigDecimal(this.value), meta = this.meta, mutable = this.mutable)
      case constants.NFT.Data.BOOLEAN => BooleanProperty(name = this.name, value = this.value == constants.NFT.Data.TRUE || this.value == constants.NFT.Data.SMALL_TRUE, meta = this.meta, mutable = this.mutable)
      case _ => constants.Response.NFT_PROPERTY_TYPE_NOT_FOUND.throwBaseException()
    } else constants.Response.INVALID_NFT_PROPERTY.throwBaseException()
  }

  implicit val propertyWrites: Writes[Property] = Json.writes[Property]

  implicit val propertyReads: Reads[Property] = Json.reads[Property]

  implicit val propertyFormat: OFormat[Property] = Json.using[Json.WithDefaultValues].format[Property]

}
