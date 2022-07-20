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
  val ADD_CURRENT_PASSWORD = new StringFormField("ADD_CURRENT_PASSWORD", 5, 128)
  val ADD_KEY_PASSWORD = new StringFormField("ADD_KEY_PASSWORD", 5, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val ADD_KEY_CONFIRM_PASSWORD = new StringFormField("ADD_KEY_CONFIRM_PASSWORD", 5, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val WALLET_ADDRESS = new StringFormField("WALLET_ADDRESS", 45, 45, RegularExpression.ALL_NUMBERS_ALL_SMALL_LETTERS)
  val PARTIAL_MNEMONICS = new StringFormField("PARTIAL_MNEMONICS", 3, 128)
  val PUSH_NOTIFICATION_TOKEN = new StringFormField("PUSH_NOTIFICATION_TOKEN", 0, 200)
  val SIGNUP_PASSWORD = new StringFormField("SIGNUP_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SIGNUP_CONFIRM_PASSWORD = new StringFormField("CONFIRM_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SEED_PHRASE_1 = new StringFormField("SEED_PHRASE_1", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val SEED_PHRASE_2 = new StringFormField("SEED_PHRASE_2", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val SEED_PHRASE_3 = new StringFormField("SEED_PHRASE_3", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val SEED_PHRASE_4 = new StringFormField("SEED_PHRASE_4", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val FORGOT_PASSWORD = new StringFormField("FORGOT_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val FORGOT_CONFIRM_PASSWORD = new StringFormField("FORGOT_CONFIRM_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SEEDS = new StringFormField("SEEDS", 3, 500, RegularExpression.ALL_SMALL_LETTERS_WITH_SPACE, Response.INVALID_SEEDS.message)
  val KEY_NAME = new StringFormField("KEY_NAME", 3, 128, RegularExpression.ALL_NUMBERS_ALL_LETTERS, Response.INVALID_KEY_NAME.message)


  //BooleanFormField
  val RECEIVE_NOTIFICATIONS = new BooleanFormField("RECEIVE_NOTIFICATIONS")
  val USERNAME_AVAILABLE = new BooleanFormField("USERNAME_AVAILABLE")
  val SIGNUP_TERMS_CONDITIONS = new BooleanFormField("SIGNUP_TERMS_CONDITIONS")
  val STATUS = new BooleanFormField("STATUS")
  val ACTIVE = new BooleanFormField("ACTIVE")

  class StringFormField(fieldName: String, minimumLength: Int, maximumLength: Int, regex: Regex = RegularExpression.ANY_STRING, errorMessage: String = "Error Response") {
    val name: String = fieldName
    val field: Mapping[String] = text(minLength = minimumLength, maxLength = maximumLength).verifying(Constraints.pattern(regex = regex, name = regex.pattern.toString, error = errorMessage))
    val placeHolder: String = "PLACEHOLDER." + name
    def mapping: (String, Mapping[String]) = name -> field
  }

  class SelectFormField(fieldName: String, val options: Seq[String], errorMessage: String = "Error Response") {
    val name: String = fieldName
    val field: Mapping[String] = text.verifying(constraint = field => options contains field, error = errorMessage)

    def mapping: (String, Mapping[String]) = name -> field
  }

  class CustomSelectFormField(fieldName: String) {
    val name: String = fieldName
    val field: Mapping[String] = text
  }

  class IntFormField(fieldName: String, val minimumValue: Int, val maximumValue: Int) {
    val name: String = fieldName
    val field: Mapping[Int] = number(min = minimumValue, max = maximumValue)

    def mapping: (String, Mapping[Int]) = name -> field
  }

  class DateFormField(fieldName: String) {
    val name: String = fieldName
    val field: Mapping[Date] = date

    def mapping: (String, Mapping[Date]) = name -> field
  }

  class DoubleFormField(fieldName: String, val minimumValue: Double, val maximumValue: Double) {
    val name: String = fieldName
    val field: Mapping[Double] = of(doubleFormat).verifying(Constraints.max[Double](maximumValue), Constraints.min[Double](minimumValue))

    def mapping: (String, Mapping[Double]) = name -> field
  }

  class BigDecimalFormField(fieldName: String, val minimumValue: BigDecimal, val maximumValue: BigDecimal) {
    val name: String = fieldName
    val field: Mapping[BigDecimal] = of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue), Constraints.min[BigDecimal](minimumValue))

    def mapping: (String, Mapping[BigDecimal]) = name -> field
  }

  class BooleanFormField(fieldName: String) {
    val name: String = fieldName
    val field: Mapping[Boolean] = boolean

    def mapping: (String, Mapping[Boolean]) = name -> field
  }

  class NestedFormField(fieldName: String) {
    val name: String = fieldName
  }
}
