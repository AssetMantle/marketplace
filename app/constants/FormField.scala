package constants

import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.format.Formats.{bigDecimalFormat, doubleFormat}
import play.api.data.validation.Constraints
import utilities.MicroNumber
import utilities.NumericOperation.checkPrecision

import java.util.Date
import scala.util.matching.Regex

object FormField {
  //StringFormField
  val USERNAME = new StringFormField("USERNAME", 3, 50, RegularExpression.ACCOUNT_ID)
  val PASSWORD = new StringFormField("PASSWORD", 5, 128)
  val ADD_CURRENT_PASSWORD = new StringFormField("ADD_CURRENT_PASSWORD", 5, 128)
  val ADD_KEY_PASSWORD = new StringFormField("ADD_KEY_PASSWORD", 5, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val ADD_KEY_CONFIRM_PASSWORD = new StringFormField("ADD_KEY_CONFIRM_PASSWORD", 5, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val WALLET_ADDRESS = new StringFormField("WALLET_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
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
  val OLD_PASSWORD = new StringFormField("OLD_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val CHANGE_PASSWORD = new StringFormField("CHANGE_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val CHANGE_CONFIRM_PASSWORD = new StringFormField("CHANGE_CONFIRM_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SEEDS = new StringFormField("SEEDS", 3, 500, RegularExpression.ALL_SMALL_LETTERS_WITH_SPACE, Response.INVALID_SEEDS.message)
  val KEY_NAME = new StringFormField("KEY_NAME", 3, 128, RegularExpression.ALL_NUMBERS_ALL_LETTERS, Response.INVALID_KEY_NAME.message)
  val CONFIRM_USERNAME = new StringFormField("CONFIRM_USERNAME", 3, 50, RegularExpression.ACCOUNT_ID)
  val FROM_ADDRESS = new StringFormField("FROM_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val TO_ADDRESS = new StringFormField("TO_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val WHITELIST_NAME = new StringFormField("WHITELIST_NAME", 3, 50, RegularExpression.WHITELIST_NAME)
  val WHITELIST_DESCRIPTION = new StringFormField("WHITELIST_DESCRIPTION", 0, 256)
  val WHITELIST_ID = new StringFormField("WHITELIST_ID", 16, 16)
  val CALLBACK_URL = new StringFormField("CALLBACK_URL", 1, 1024, RegularExpression.ANY_STRING)

  val MANAGED_KEY_NAME = new StringFormField("MANAGED_KEY_NAME", 3, 50)
  val MANAGED_KEY_ADDRESS = new StringFormField("MANAGED_KEY_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val UNMANAGED_KEY_NAME = new StringFormField("UNMANAGED_KEY_NAME", 3, 50)
  val UNMANAGED_KEY_ADDRESS = new StringFormField("UNMANAGED_KEY_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val CHANGE_KEY_NAME = new StringFormField("CHANGE_KEY_NAME", 3, 50)
  val CHANGE_KEY_ADDRESS = new StringFormField("CHANGE_KEY_ADDRESS", 3, 50, RegularExpression.MANTLE_ADDRESS)

  val GAS_AMOUNT = new IntFormField("GAS_AMOUNT", 20000, 2000000)

  val WHITELIST_MAX_MEMBERS = new IntFormField("WHITELIST_MAX_MEMBERS", 1, Int.MaxValue)
  val WHITELIST_INVITE_START_EPOCH = new IntFormField("WHITELIST_INVITE_START_EPOCH", 1, Int.MaxValue)
  val WHITELIST_INVITE_END_EPOCH = new IntFormField("WHITELIST_INVITE_END_EPOCH", 1, Int.MaxValue)

  //BooleanFormField
  val RECEIVE_NOTIFICATIONS = new BooleanFormField("RECEIVE_NOTIFICATIONS")
  val USERNAME_AVAILABLE = new BooleanFormField("USERNAME_AVAILABLE")
  val SIGNUP_TERMS_CONDITIONS = new BooleanFormField("SIGNUP_TERMS_CONDITIONS")
  val STATUS = new BooleanFormField("STATUS")
  val ACTIVE = new BooleanFormField("ACTIVE")
  val MANAGED_KEY_DISCLAIMER = new BooleanFormField("MANAGED_KEY_DISCLAIMER")

  val GAS_PRICE = new SelectFormField("GAS_PRICE", Seq(constants.CommonConfig.Blockchain.LowGasPrice.toString, constants.CommonConfig.Blockchain.MediumGasPrice.toString, constants.CommonConfig.Blockchain.HighGasPrice.toString))

  val SEND_COIN_AMOUNT = new MicroNumberFormField("SEND_COIN_AMOUNT", MicroNumber.zero, MicroNumber(Int.MaxValue), 6)

  class StringFormField(fieldName: String, val minimumLength: Int, val maximumLength: Int, regex: Regex = RegularExpression.ANY_STRING, errorMessage: String = "Error Response") {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[String]) = name -> text(minLength = minimumLength, maxLength = maximumLength).verifying(Constraints.pattern(regex = regex, name = regex.pattern.toString, error = errorMessage))
  }

  class SelectFormField(fieldName: String, val options: Seq[String], errorMessage: String = "Error Response") {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[String]) = name -> text.verifying(constraint = field => options contains field, error = errorMessage)
  }

  class CustomSelectFormField(fieldName: String) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[String]) = name -> text
  }

  class IntFormField(fieldName: String, val minimumValue: Int, val maximumValue: Int) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[Int]) = name -> number(min = minimumValue, max = maximumValue)
  }

  class DateFormField(fieldName: String) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[Date]) = name -> date
  }

  class DoubleFormField(fieldName: String, val minimumValue: Double, val maximumValue: Double) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[Double]) = name -> of(doubleFormat).verifying(Constraints.max[Double](maximumValue), Constraints.min[Double](minimumValue))
  }

  class BigDecimalFormField(fieldName: String, val minimumValue: BigDecimal, val maximumValue: BigDecimal) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[BigDecimal]) = name -> of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue), Constraints.min[BigDecimal](minimumValue))
  }

  class BooleanFormField(fieldName: String) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[Boolean]) = name -> boolean
  }

  class MicroNumberFormField(fieldName: String, val minimumValue: MicroNumber, val maximumValue: MicroNumber, precision: Int = 2) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name

    def mapping: (String, Mapping[MicroNumber]) = name -> of(doubleFormat).verifying(Constraints.max[Double](maximumValue.toDouble), Constraints.min[Double](minimumValue.toDouble)).verifying(constants.Response.MICRO_NUMBER_PRECISION_MORE_THAN_REQUIRED.message, x => checkPrecision(precision, x.toString)).transform[MicroNumber](x => new MicroNumber(x), y => y.toDouble)
  }

  class NestedFormField(fieldName: String) {
    val name: String = fieldName
    val placeHolder: String = "PLACEHOLDER." + name
  }
}
