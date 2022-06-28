package constants

import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.format.Formats.{bigDecimalFormat, doubleFormat}
import play.api.data.validation.Constraints

import java.util.Date
import scala.util.matching.Regex

object FormField {
  //StringFormField
  val USERNAME = new StringFormField("USERNAME", 3, 50, RegularExpression.ACCOUNT_ID)
  val PASSWORD = new StringFormField("PASSWORD", 5, 128)
  val WALLET_ADDRESS = new StringFormField("WALLET_ADDRESS", 45, 45, RegularExpression.ALL_NUMBERS_ALL_SMALL_LETTERS)
  val PARTIAL_MNEMONICS = new StringFormField("PARTIAL_MNEMONICS", 3, 128)
  val PUSH_NOTIFICATION_TOKEN = new StringFormField("PUSH_NOTIFICATION_TOKEN", 0, 200)
  val SIGNUP_PASSWORD = new StringFormField("SIGNUP_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SIGNUP_CONFIRM_PASSWORD = new StringFormField("CONFIRM_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SEED_PHRASE_1 = new StringFormField("SEED_PHRASE_1", 3, 20)
  val SEED_PHRASE_2 = new StringFormField("SEED_PHRASE_2", 3, 20)
  val SEED_PHRASE_3 = new StringFormField("SEED_PHRASE_3", 3, 20)
  val SEED_PHRASE_4 = new StringFormField("SEED_PHRASE_4", 3, 20)
  val FORGOT_PASSWORD = new StringFormField("FORGOT_PASSWORD", 5, 128)
  val FORGOT_CONFIRM_PASSWORD = new StringFormField("FORGOT_CONFIRM_PASSWORD", 5, 128)


  //BooleanFormField
  val RECEIVE_NOTIFICATIONS = new BooleanFormField("RECEIVE_NOTIFICATIONS")
  val USERNAME_AVAILABLE = new BooleanFormField("USERNAME_AVAILABLE")
  val SIGNUP_TERMS_CONDITIONS = new BooleanFormField("SIGNUP_TERMS_CONDITIONS")
  val STATUS = new BooleanFormField("STATUS")

  class StringFormField(fieldName: String, minimumLength: Int, maximumLength: Int, regex: Regex = RegularExpression.ANY_STRING, errorMessage: String = "Error Response") {
    val name: String = fieldName
    val field: Mapping[String] = text(minLength = minimumLength, maxLength = maximumLength).verifying(Constraints.pattern(regex = regex, name = regex.pattern.toString, error = errorMessage))
  }

  class SelectFormField(fieldName: String, val options: Seq[String], errorMessage: String = "Error Response") {
    val name: String = fieldName
    val field: Mapping[String] = text.verifying(constraint = field => options contains field, error = errorMessage)
  }

  class CustomSelectFormField(fieldName: String) {
    val name: String = fieldName
    val field: Mapping[String] = text
  }

  class IntFormField(fieldName: String, val minimumValue: Int, val maximumValue: Int) {
    val name: String = fieldName
    val field: Mapping[Int] = number(min = minimumValue, max = maximumValue)
  }

  class DateFormField(fieldName: String) {
    val name: String = fieldName
    val field: Mapping[Date] = date
  }

  class DoubleFormField(fieldName: String, val minimumValue: Double, val maximumValue: Double) {
    val name: String = fieldName
    val field: Mapping[Double] = of(doubleFormat).verifying(Constraints.max[Double](maximumValue), Constraints.min[Double](minimumValue))
  }

  class BigDecimalFormField(fieldName: String, val minimumValue: BigDecimal, val maximumValue: BigDecimal) {
    val name: String = fieldName
    val field: Mapping[BigDecimal] = of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue), Constraints.min[BigDecimal](minimumValue))
  }

  class BooleanFormField(fieldName: String) {
    val name: String = fieldName
    val field: Mapping[Boolean] = boolean
  }

  class NestedFormField(fieldName: String) {
    val name: String = fieldName
  }
}
