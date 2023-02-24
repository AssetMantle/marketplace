package constants

import schema.id.base.StringID

import scala.util.Try

object NFT {

  object Tags {
    val MinimumLength = 2
    val MaximumLength = 15
    val Separator = ","
    val MaximumAllowed = 5
  }

  object Data {
    val STRING = "STRING"
    val BOOLEAN = "BOOLEAN"
    val NUMBER = "NUMBER"

    val TypesList: Seq[String] = Seq(STRING, NUMBER, BOOLEAN)

    val TRUE = "TRUE"
    val SMALL_TRUE = "true"
    val FALSE = "FALSE"
    val SMALL_FALSE = "false"

    def isBooleanType(value: String): Boolean = value == TRUE || value == SMALL_TRUE || value == FALSE || value == SMALL_FALSE

    def isCastable(`type`: String, value: String): Boolean = `type` match {
      case constants.NFT.Data.STRING => true
      case constants.NFT.Data.NUMBER => Try(BigDecimal(value)).isSuccess
      case constants.NFT.Data.BOOLEAN => constants.NFT.Data.isBooleanType(value)
      case _ => false
    }
  }

  case class Property(name: String, `type`: String, `value`: String, meta: Boolean, mutable: Boolean)

  object Sale {
    val MaxSaleAmount: BigDecimal = BigDecimal("1000000000")
    val MaxCreatorFee: BigDecimal = BigDecimal("40")
  }
}
