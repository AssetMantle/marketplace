package views.sale.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AcceptWhitelistSaleOffer {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITELIST_SALE_ID.mapping,
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(saleId: String, password: String)

}
