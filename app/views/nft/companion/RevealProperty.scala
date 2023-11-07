package views.nft.companion

import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.mapping
import schema.data.base._
import schema.data.{Data => schemaData}

object RevealProperty {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_PROPERTY_TYPE.mapping,
      constants.FormField.NFT_PROPERTY_VALUE.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(propertyType: String, propertyValue: String, password: String) {

    def getData()(implicit module: String, logger: Logger): schemaData = try {
      propertyType match {
        case constants.NFT.Data.STRING => StringData(this.propertyValue)
        case constants.NFT.Data.NUMBER => NumberData(BigInt(this.propertyValue))
        case constants.NFT.Data.DECIMAL => DecData(this.propertyValue)
        case constants.NFT.Data.HEIGHT => HeightData(this.propertyValue.toLong)
        case constants.NFT.Data.BOOLEAN => BooleanData(this.propertyValue.toBoolean)
        case _ => constants.Response.INVALID_DATA_TYPE.throwBaseException()
      }
    } catch {
      case _: Exception => constants.Response.INVALID_DATA_TYPE.throwBaseException()
    }

  }

}
