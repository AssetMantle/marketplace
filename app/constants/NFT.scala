package constants

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
    val DECIMAL = "DECIMAL"
    val BOOLEAN = "BOOLEAN"

    val TypesList: Seq[String] = Seq(STRING, DECIMAL, BOOLEAN)

    @deprecated
    val NUMBER = "NUMBER"
    @deprecated
    val SMALL_STRING = "string"
    @deprecated
    val SMALL_NUMBER = "number"

    val TRUE = "TRUE"
    val SMALL_TRUE = "true"
    val FALSE = "FALSE"
    val SMALL_FALSE = "false"

    def isBooleanType(value: String): Boolean = value == TRUE || value == SMALL_TRUE || value == FALSE || value == SMALL_FALSE

    def isCastable( `type`: String, value: String): Boolean = `type`match {
      case constants.NFT.Data.STRING => true
      case constants.NFT.Data.DECIMAL => Try(BigDecimal(value)).isSuccess
      case constants.NFT.Data.BOOLEAN => constants.NFT.Data.isBooleanType(value)
      case _ => false
    }
  }
}
