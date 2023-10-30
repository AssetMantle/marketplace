package views.sale.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object StopSale {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.SALE_ID.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(saleId: String)
}
