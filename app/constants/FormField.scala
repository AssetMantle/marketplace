package constants

import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.data.validation.Constraints
import play.api.data.{FormError, Mapping}
import play.api.i18n.{Messages, MessagesProvider}
import utilities.MicroNumber
import utilities.NumericOperation.checkPrecision

import java.net.URL
import java.util.Date

object FormField {

  private val PLACEHOLDER_PREFIX = "PLACEHOLDER."
  private val SELECT_ERROR_PREFIX = "SELECT_ERROR_PREFIX."
  private val RADIO_BUTTON_ERROR_PREFIX = "RADIO_BUTTON_ERROR_PREFIX."
  private val DATE_ERROR_PREFIX = "DATE_ERROR_PREFIX."
  private val URL_ERROR_PREFIX = "URL_ERROR_PREFIX."
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
  val FROM_KEPLR_ADDRESS: StringFormField = StringFormField("FROM_KEPLR_ADDRESS", 45, 45, RegularExpression.MANTLE_ADDRESS)
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
  val COLLECTION_NAME: StringFormField = StringFormField("COLLECTION_NAME", 3, 30)
  val COLLECTION_DESCRIPTION: StringFormField = StringFormField("COLLECTION_DESCRIPTION", 3, 256)
  val COLLECTION_ID: StringFormField = StringFormField("COLLECTION_ID", 16, 16)
  val COLLECTION_PROPERTY_NAME: StringFormField = StringFormField("COLLECTION_PROPERTY_NAME", 1, 30)
  val COLLECTION_PROPERTY_DEFAULT_VALUE: StringFormField = StringFormField("COLLECTION_PROPERTY_DEFAULT_VALUE", 1, 30)
  val NFT_NAME: StringFormField = StringFormField("NFT_NAME", 3, 50)
  val NFT_DESCRIPTION: StringFormField = StringFormField("NFT_DESCRIPTION", 3, 256)
  val NFT_ID: StringFormField = StringFormField("NFT_ID", 64, 64)
  val NFT_TAGS: StringFormField = StringFormField("NFT_TAGS", 0, (constants.NFT.Tags.MaximumLength + 1) * constants.NFT.Tags.MaximumAllowed)
  val NFT_PROPERTY_NAME: StringFormField = StringFormField("NFT_PROPERTY_NAME", 1, 30)
  val NFT_PROPERTY_VALUE: StringFormField = StringFormField("NFT_PROPERTY_VALUE", 1, 30)
  val COLLECTION_TWITTER: StringFormField = StringFormField("COLLECTION_TWITTER", 1, 15, RegularExpression.TWITTER_USERNAME)
  val COLLECTION_INSTAGRAM: StringFormField = StringFormField("COLLECTION_INSTAGRAM", 1, 30, RegularExpression.INSTAGRAM_USERNAME)
  val SALE_ID: StringFormField = StringFormField("SALE_ID", 16, 16)
  val PUBLIC_LISTING_ID: StringFormField = StringFormField("PUBLIC_LISTING_ID", 16, 16)
  val NOTIFICATION_ID: StringFormField = StringFormField("NOTIFICATION_ID", 16, 16)

  // UrlFormField
  val COLLECTION_WEBSITE: UrlFormField = UrlFormField("COLLECTION_WEBSITE")

  // IntFormField
  val GAS_AMOUNT: IntFormField = IntFormField("GAS_AMOUNT", 20000, 2000000)
  val WHITELIST_MAX_MEMBERS: IntFormField = IntFormField("WHITELIST_MAX_MEMBERS", 1, 100000)
  val SALE_NFT_NUMBER: IntFormField = IntFormField("SALE_NFT_NUMBER", 1, 100000)
  val PUBLIC_LISTING_NFT_NUMBER: IntFormField = IntFormField("PUBLIC_LISTING_NFT_NUMBER", 1, 100000)
  val SALE_MAX_MINT_PER_ACCOUNT: IntFormField = IntFormField("SALE_MAX_MINT_PER_ACCOUNT", 1, 100000)
  val PUBLIC_LISTING_MAX_MINT_PER_ACCOUNT: IntFormField = IntFormField("SALE_MAX_MINT_PER_ACCOUNT", 1, 100000)
  val SALE_BUY_NFT_NUMBER: IntFormField = IntFormField("SALE_BUY_NFT_NUMBER", 1, 100000)
  val PUBLIC_LISTING_BUY_NFT_NUMBER: IntFormField = IntFormField("PUBLIC_LISTING_BUY_NFT_NUMBER", 1, 100000)


  // DoubleFormField
  val GAS_PRICE: BigDecimalFormField = BigDecimalFormField("GAS_PRICE", constants.Blockchain.LowGasPrice, constants.Blockchain.HighGasPrice)

  // EpochFormField
  val WHITELIST_INVITE_START_EPOCH: EpochFormField = EpochFormField("WHITELIST_INVITE_START_EPOCH", 1, Int.MaxValue)
  val WHITELIST_INVITE_END_EPOCH: EpochFormField = EpochFormField("WHITELIST_INVITE_END_EPOCH", 1, Int.MaxValue)
  val NFT_SALE_START_EPOCH: EpochFormField = EpochFormField("NFT_SALE_START_EPOCH", 1, Int.MaxValue)
  val NFT_SALE_END_EPOCH: EpochFormField = EpochFormField("NFT_SALE_END_EPOCH", 1, Int.MaxValue)
  val PUBLIC_LISTING_START_EPOCH: EpochFormField = EpochFormField("PUBLIC_LISTING_START_EPOCH", 1, Int.MaxValue)
  val PUBLIC_LISTING_END_EPOCH: EpochFormField = EpochFormField("PUBLIC_LISTING_END_EPOCH", 1, Int.MaxValue)

  // BooleanFormField
  val RECEIVE_NOTIFICATIONS: BooleanFormField = BooleanFormField("RECEIVE_NOTIFICATIONS")
  val USERNAME_AVAILABLE: BooleanFormField = BooleanFormField("USERNAME_AVAILABLE")
  val SIGNUP_TERMS_CONDITIONS: BooleanFormField = BooleanFormField("SIGNUP_TERMS_CONDITIONS")
  val MANAGED_KEY_DISCLAIMER: BooleanFormField = BooleanFormField("MANAGED_KEY_DISCLAIMER")
  val NSFW_COLLECTION: BooleanFormField = BooleanFormField("NSFW_COLLECTION")
  val SAVE_COLLECTION_DRAFT: BooleanFormField = BooleanFormField("SAVE_COLLECTION_DRAFT")
  val SAVE_NFT_DRAFT: BooleanFormField = BooleanFormField("SAVE_NFT_DRAFT")
  val COLLECTION_PROPERTY_MUTABLE: BooleanFormField = BooleanFormField("COLLECTION_PROPERTY_MUTABLE")
  val COLLECTION_PROPERTY_SET_DEFAULT_VALUE: BooleanFormField = BooleanFormField("COLLECTION_PROPERTY_SET_DEFAULT_VALUE")
  val COLLECTION_PROPERTY_HIDE: BooleanFormField = BooleanFormField("COLLECTION_PROPERTY_HIDE")
  val CREATE_COLLECTION_TERMS_AND_CONDITION: BooleanFormField = BooleanFormField("CREATE_COLLECTION_TERMS_AND_CONDITION")
  val CREATE_COLLECTION_MOU: BooleanFormField = BooleanFormField("CREATE_COLLECTION_MOU")
  val MARK_ALL_NOTIFICATION_READ: BooleanFormField = BooleanFormField("MARK_ALL_NOTIFICATION_READ")
  val MINT_NFT: BooleanFormField = BooleanFormField("MINT_NFT")

  // SelectFormField
  val COLLECTION_CATEGORY: SelectFormField = SelectFormField("COLLECTION_CATEGORY", Seq(constants.Collection.Category.ART, constants.Collection.Category.PHOTOGRAPHY, constants.Collection.Category.MISCELLANEOUS))
  val COLLECTION_PROPERTY_TYPE: SelectFormField = SelectFormField("COLLECTION_PROPERTY_TYPE", Seq(constants.NFT.Data.STRING, constants.NFT.Data.DECIMAL, constants.NFT.Data.BOOLEAN))

  // CustomSelect
  val SELECT_WHITELIST_ID: CustomSelectFormField = CustomSelectFormField("SELECT_WHITELIST_ID")
  val SELECT_COLLECTION_ID: CustomSelectFormField = CustomSelectFormField("SELECT_COLLECTION_ID")

  // MicroNumberFormField
  val SEND_COIN_AMOUNT: MicroNumberFormField = MicroNumberFormField("SEND_COIN_AMOUNT", MicroNumber.smallest, MicroNumber(Int.MaxValue), 6)
  val NFT_WHITELIST_SALE_PRICE: MicroNumberFormField = MicroNumberFormField("NFT_WHITELIST_SALE_PRICE", MicroNumber.smallest, MicroNumber(Int.MaxValue), 6)
  val PUBLIC_LISTING_PRICE: MicroNumberFormField = MicroNumberFormField("PUBLIC_LISTING_PRICE", MicroNumber.smallest, MicroNumber(Int.MaxValue), 6)

  // NestedFormField
  val COLLECTION_PROPERTIES: NestedFormField = NestedFormField("COLLECTION_PROPERTIES")
  val NFT_PROPERTIES: NestedFormField = NestedFormField("NFT_PROPERTIES")

  // RadioFormField

  // BigDecimalFormField
  val NFT_SALE_CREATOR_FEE: BigDecimalFormField = BigDecimalFormField("NFT_SALE_CREATOR_FEE", 0.0, constants.NFT.Sale.MaxCreatorFee)

  case class StringFormField(name: String, minimumLength: Int, maximumLength: Int, regularExpression: RegularExpression = RegularExpression.ANY_STRING, errorMessage: String = "Regular expression validation failed!") {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[String]) = name -> text(minLength = minimumLength, maxLength = maximumLength).verifying(Constraints.pattern(regex = regularExpression.regex, name = regularExpression.regex.pattern.toString, error = errorMessage))

    // TODO
    //  def ignoredMapping: (String, Mapping[String]) = name -> ignored[String]("defaultValue")

    def optionalMapping: (String, Mapping[Option[String]]) = name -> optional(text(minLength = minimumLength, maxLength = maximumLength).verifying(Constraints.pattern(regex = regularExpression.regex, name = regularExpression.regex.pattern.toString, error = errorMessage)))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_LENGTH_ERROR, minimumLength)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_LENGTH_ERROR, maximumLength)

    def getRegexErrorMessage()(implicit messagesProvider: MessagesProvider): String = regularExpression.getRegExErrorMessage()
  }

  case class RadioFormField(name: String, options: Seq[(String, String)], errorMessage: String = "Option not found") {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[String]) = name -> text.verifying(constraint = field => options.map(_._1) contains field, error = errorMessage)

    def optionalMapping: (String, Mapping[Option[String]]) = name -> optional(text.verifying(constraint = field => options.map(_._1) contains field, error = errorMessage))

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(RADIO_BUTTON_ERROR_PREFIX + name)
  }

  case class SelectFormField(name: String, options: Seq[String], errorMessage: String = "Option not found") {
    val placeHolder: String = options.head

    def mapping: (String, Mapping[String]) = name -> text.verifying(constraint = field => options contains field, error = errorMessage)

    def optionalMapping: (String, Mapping[Option[String]]) = name -> optional(text.verifying(constraint = field => options contains field, error = errorMessage))

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(SELECT_ERROR_PREFIX + name)
  }

  case class CustomSelectFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[String]) = name -> text

    def optionalMapping: (String, Mapping[Option[String]]) = name -> optional(text)

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(SELECT_ERROR_PREFIX + name)
  }

  case class IntFormField(name: String, minimumValue: Int, maximumValue: Int) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Int]) = name -> number(min = minimumValue, max = maximumValue)

    def optionalMapping: (String, Mapping[Option[Int]]) = name -> optional(number(min = minimumValue, max = maximumValue))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimumValue)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximumValue)

    def getCustomFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(CUSTOM_FIELD_ERROR_PREFIX + name, minimumValue, maximumValue)

  }

  case class EpochFormField(name: String, minimum: Int, maximum: Int) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Int]) = name -> number(min = minimum, max = maximum)

    def optionalMapping: (String, Mapping[Option[Int]]) = name -> optional(number(min = minimum, max = maximum))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimum)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximum)

    def getCustomFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(CUSTOM_FIELD_ERROR_PREFIX + name, minimum, maximum)

  }

  case class DateFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Date]) = name -> date

    def optionalMapping: (String, Mapping[Option[Date]]) = name -> optional(date)

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(DATE_ERROR_PREFIX + name)
  }

  case class BigDecimalFormField(name: String, minimumValue: BigDecimal, maximumValue: BigDecimal) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[BigDecimal]) = name -> of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue), Constraints.min[BigDecimal](minimumValue))

    def optionalMapping: (String, Mapping[Option[BigDecimal]]) = name -> optional(of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue), Constraints.min[BigDecimal](minimumValue)))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimumValue)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximumValue)

  }

  case class BooleanFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[Boolean]) = name -> boolean
  }

  implicit object UrlFormatter extends Formatter[URL] {
    override val format: Option[(String, Nil.type)] = Some(("URL", Nil))

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], URL] = parsing(new URL(_), "Invalid URL", Nil)(key, data)

    override def unbind(key: String, value: URL): Map[String, String] = Map(key -> value.toString)
  }

  case class UrlFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[URL]) = name -> of[URL]

    def optionalMapping: (String, Mapping[Option[URL]]) = name -> optional(of[URL])

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(URL_ERROR_PREFIX + name)
  }

  case class MicroNumberFormField(name: String, minimumValue: MicroNumber, maximumValue: MicroNumber, precision: Int = 2) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def mapping: (String, Mapping[MicroNumber]) = name -> of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue.toBigDecimal), Constraints.min[BigDecimal](minimumValue.toBigDecimal)).verifying(constants.Response.MICRO_NUMBER_PRECISION_MORE_THAN_REQUIRED.message, x => checkPrecision(precision, x.toString)).transform[MicroNumber](x => new MicroNumber(x), y => y.toBigDecimal)

    def optionalMapping: (String, Mapping[Option[MicroNumber]]) = name -> optional(of(bigDecimalFormat).verifying(Constraints.max[BigDecimal](maximumValue.toBigDecimal), Constraints.min[BigDecimal](minimumValue.toBigDecimal)).verifying(constants.Response.MICRO_NUMBER_PRECISION_MORE_THAN_REQUIRED.message, x => checkPrecision(precision, x.toString)).transform[MicroNumber](x => new MicroNumber(x), y => y.toBigDecimal))

    def getMinimumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MINIMUM_VALUE_ERROR, minimumValue.toString)

    def getMaximumFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(MAXIMUM_VALUE_ERROR, maximumValue.toString)

  }

  case class NestedFormField(name: String) {
    val placeHolder: String = PLACEHOLDER_PREFIX + name

    def getFieldErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages(NESTED_ERROR_PREFIX + name)
  }
}
