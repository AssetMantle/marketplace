package constants

import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.format.Formats.{bigDecimalFormat, doubleFormat}
import play.api.data.validation.Constraints
import play.api.i18n.{Messages, MessagesProvider}
import utilities.MicroNumber
import utilities.NumericOperation.checkPrecision

import java.util.Date

object FormField {

  private val PLACEHOLDER_PREFIX = "PLACEHOLDER."
  private val SELECT_ERROR_PREFIX = "SELECT_ERROR_PREFIX."
  private val DATE_ERROR_PREFIX = "DATE_ERROR_PREFIX."
  private val NESTED_ERROR_PREFIX = "NESTED_ERROR_PREFIX."
  private val MINIMUM_LENGTH_ERROR = "MINIMUM_LENGTH_ERROR"
  private val MAXIMUM_LENGTH_ERROR = "MAXIMUM_LENGTH_ERROR"
  private val MINIMUM_VALUE_ERROR = "MINIMUM_VALUE_ERROR"
  private val MAXIMUM_VALUE_ERROR = "MAXIMUM_VALUE_ERROR"
  private val CUSTOM_FIELD_ERROR_PREFIX = "CUSTOM_FIELD_ERROR_PREFIX."

  //StringFormField
  val USERNAME: StringFormField = StringFormField("USERNAME", 3, 50, RegularExpression.ACCOUNT_ID)
  val PASSWORD: StringFormField = StringFormField("PASSWORD", 5, 128)
  val WALLET_ADDRESS: StringFormField = StringFormField("WALLET_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val PUSH_NOTIFICATION_TOKEN: StringFormField = StringFormField("PUSH_NOTIFICATION_TOKEN", 0, 200)
  val SIGNUP_PASSWORD: StringFormField = StringFormField("SIGNUP_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SIGNUP_CONFIRM_PASSWORD: StringFormField = StringFormField("CONFIRM_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SEED_PHRASE_1: StringFormField = StringFormField("SEED_PHRASE_1", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val SEED_PHRASE_2: StringFormField = StringFormField("SEED_PHRASE_2", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val SEED_PHRASE_3: StringFormField = StringFormField("SEED_PHRASE_3", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val SEED_PHRASE_4: StringFormField = StringFormField("SEED_PHRASE_4", 3, 20, RegularExpression.ALL_SMALL_LETTERS, Response.INVALID_SEEDS.message)
  val FORGOT_PASSWORD: StringFormField = StringFormField("FORGOT_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val FORGOT_CONFIRM_PASSWORD: StringFormField = StringFormField("FORGOT_CONFIRM_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val OLD_PASSWORD: StringFormField = StringFormField("OLD_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val CHANGE_PASSWORD: StringFormField = StringFormField("CHANGE_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val CHANGE_CONFIRM_PASSWORD: StringFormField = StringFormField("CHANGE_CONFIRM_PASSWORD", 8, 128, RegularExpression.PASSWORD, Response.INVALID_PASSWORD.message)
  val SEEDS: StringFormField = StringFormField("SEEDS", 3, 500, RegularExpression.ALL_SMALL_LETTERS_WITH_SPACE, Response.INVALID_SEEDS.message)
  val CONFIRM_USERNAME: StringFormField = StringFormField("CONFIRM_USERNAME", 3, 50, RegularExpression.ACCOUNT_ID)
  val FROM_ADDRESS: StringFormField = StringFormField("FROM_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val TO_ADDRESS: StringFormField = StringFormField("TO_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val WHITELIST_NAME: StringFormField = StringFormField("WHITELIST_NAME", 3, 50, RegularExpression.WHITELIST_NAME)
  val WHITELIST_DESCRIPTION: StringFormField = StringFormField("WHITELIST_DESCRIPTION", 0, 256)
  val WHITELIST_ID: StringFormField = StringFormField("WHITELIST_ID", 16, 16)
  val CALLBACK_URL: StringFormField = StringFormField("CALLBACK_URL", 1, 1024, RegularExpression.ANY_STRING)
  val MANAGED_KEY_NAME: StringFormField = StringFormField("MANAGED_KEY_NAME", 3, 50)
  val MANAGED_KEY_ADDRESS: StringFormField = StringFormField("MANAGED_KEY_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val UNMANAGED_KEY_NAME: StringFormField = StringFormField("UNMANAGED_KEY_NAME", 3, 50)
  val UNMANAGED_KEY_ADDRESS: StringFormField = StringFormField("UNMANAGED_KEY_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
  val CHANGE_KEY_NAME: StringFormField = StringFormField("CHANGE_KEY_NAME", 3, 50)
  val CHANGE_KEY_ADDRESS: StringFormField = StringFormField("CHANGE_KEY_ADDRESS", 3, 50, RegularExpression.MANTLE_ADDRESS)

  val GAS_AMOUNT: IntFormField = IntFormField("GAS_AMOUNT", 20000, 2000000)
  val WHITELIST_MAX_MEMBERS: IntFormField = IntFormField("WHITELIST_MAX_MEMBERS", 1, Int.MaxValue)

  val USER_BIO = new StringFormField("USER_BIO", 3, 200)
  val INSTAGRAM_USERNAME = new StringFormField("INSTAGRAM_USERNAME", 3, 50)
  val WHITELIST_INVITE_START_EPOCH: IntFormField = IntFormField("WHITELIST_INVITE_START_EPOCH", 1, Int.MaxValue)
  val WHITELIST_INVITE_END_EPOCH: IntFormField = IntFormField("WHITELIST_INVITE_END_EPOCH", 1, Int.MaxValue)

  //BooleanFormField
  val RECEIVE_NOTIFICATIONS: BooleanFormField = BooleanFormField("RECEIVE_NOTIFICATIONS")
  val USERNAME_AVAILABLE: BooleanFormField = BooleanFormField("USERNAME_AVAILABLE")
  val SIGNUP_TERMS_CONDITIONS: BooleanFormField = BooleanFormField("SIGNUP_TERMS_CONDITIONS")
  val MANAGED_KEY_DISCLAIMER: BooleanFormField = BooleanFormField("MANAGED_KEY_DISCLAIMER")

  val GAS_PRICE: SelectFormField = SelectFormField("GAS_PRICE", Seq(constants.CommonConfig.Blockchain.LowGasPrice.toString, constants.CommonConfig.Blockchain.MediumGasPrice.toString, constants.CommonConfig.Blockchain.HighGasPrice.toString))

  val SEND_COIN_AMOUNT: MicroNumberFormField = MicroNumberFormField("SEND_COIN_AMOUNT", MicroNumber.zero, MicroNumber(Int.MaxValue), 6)

  case class StringFormField(name: String, minimumLength: Int, maximumLength: Int, regularExpression: RegularExpression = RegularExpression.ANY_STRING, errorMessage: String = "Regular expression validation failed!") {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[String]) = name -> text(minLength = minimumLength, maxLength = maximumLength).verifying(Constraints.pattern(regex = regularExpression.regex, name = regularExpression.regex.pattern.toString, error = errorMessage))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_LENGTH_ERROR, minimumLength)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_LENGTH_ERROR, maximumLength)

    def getRegexErrorMessage()(implicit messagesProvider: MessagesProvider): String = regularExpression.getRegExErrorMessage()
  }

  case class SelectFormField(name: String, options: Seq[String], errorMessage: String = "Option not found") {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[String]) = name -> text.verifying(constraint = field => options contains field, error = errorMessage)

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(SELECT_ERROR_PREFIX + name)
  }

  case class CustomSelectFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[String]) = name -> text

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(SELECT_ERROR_PREFIX + name)
  }

  case class IntFormField(name: String, minimumValue: Int, maximumValue: Int) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Int]) = name -> number(min = minimumValue, max = maximumValue)

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimumValue)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximumValue)

    def getCustomFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(CUSTOM_FIELD_ERROR_PREFIX + name, minimumValue, maximumValue)

  }

  case class DateFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Date]) = name -> date

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(DATE_ERROR_PREFIX + name)
  }

  case class DoubleFormField(name: String, minimumValue: Double, maximumValue: Double) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Double]) = name -> of(doubleFormat).verifying(Constraints.max[Double](maximumValue), Constraints.min[Double](minimumValue))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimumValue)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximumValue)

  }

  case class BigDecimalFormField(name: String, minimumValue: BigDecimal, maximumValue: BigDecimal) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[BigDecimal]) = name -> of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue), Constraints.min[BigDecimal](minimumValue))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimumValue)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximumValue)

  }

  case class BooleanFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Boolean]) = name -> boolean
  }

  case class MicroNumberFormField(name: String, minimumValue: MicroNumber, maximumValue: MicroNumber, precision: Int = 2) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[MicroNumber]) = name -> of(doubleFormat).verifying(Constraints.max[Double](maximumValue.toDouble), Constraints.min[Double](minimumValue.toDouble)).verifying(constants.Response.MICRO_NUMBER_PRECISION_MORE_THAN_REQUIRED.message, x => checkPrecision(precision, x.toString)).transform[MicroNumber](x => new MicroNumber(x), y => y.toDouble)

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimumValue)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximumValue)

  }

  case class NestedFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(NESTED_ERROR_PREFIX + name)
  }
}
