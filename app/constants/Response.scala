package constants

import exceptions.BaseException
import play.api.Logger
import play.api.routing.JavaScriptReverseRoute

import scala.concurrent.{ExecutionContext, Future}

object Response {

  private val PREFIX = "RESPONSE."
  private val FAILURE_PREFIX = "FAILURE."
  private val WARNING_PREFIX = "WARNING."
  private val SUCCESS_PREFIX = "SUCCESS."
  private val INFO_PREFIX = "INFO."
  private val LOG_PREFIX = "LOG."

  val LOGGED_OUT = new Success("LOGGED_OUT")

  val ACCOUNT_INSERT_FAILED = new Failure("ACCOUNT_INSERT_FAILED")
  val ACCOUNT_UPSERT_FAILED = new Failure("ACCOUNT_UPSERT_FAILED")
  val ACCOUNT_NOT_FOUND = new Failure("ACCOUNT_NOT_FOUND")

  val INVALID_USERNAME_OR_PASSWORD = new Failure("INVALID_USERNAME_OR_PASSWORD")
  val INVALID_PASSWORD = new Failure("INVALID_PASSWORD")
  val INCORRECT_KEY_PASSWORD = new Failure("INCORRECT_KEY_PASSWORD")
  val INVALID_CURRENT_PASSWORD = new Failure("INVALID_CURRENT_PASSWORD")
  val PASSWORDS_DO_NOT_MATCH = new Failure("PASSWORDS_DO_NOT_MATCH")
  val TERMS_AND_CONDITION_NOT_ACCEPTED = new Failure("TERMS_AND_CONDITION_NOT_ACCEPTED")
  val MOU_NOT_ACCEPTED = new Failure("MOU_NOT_ACCEPTED")
  val USERNAME_UNAVAILABLE = new Failure("USERNAME_UNAVAILABLE")
  val PASSWORD_VALIDATION_FAILED = new Failure("PASSWORD_VALIDATION_FAILED")
  val OLD_AND_NEW_SAME_PASSWORD = new Failure("OLD_AND_NEW_SAME_PASSWORD")
  val INVALID_SEEDS_OR_ADDRESS = new Failure("INVALID_SEEDS_OR_ADDRESS")
  val INVALID_SEEDS_OR_ADDRESS_OR_PASSWORD = new Failure("INVALID_SEEDS_OR_ADDRESS_OR_PASSWORD")
  val MNEMONICS_LENGTH_NOT_12_OR_24 = new Failure("MNEMONICS_LENGTH_NOT_12_OR_24")

  val SIGN_UP_SUCCESSFUL = new Success("SIGN_UP_SUCCESSFUL")

  val JSON_PARSE_EXCEPTION = new Failure("JSON_PARSE_EXCEPTION")
  val JSON_MAPPING_EXCEPTION = new Failure("JSON_MAPPING_EXCEPTION")
  val CONNECT_EXCEPTION = new Failure("CONNECT_EXCEPTION")
  val NUMBER_FORMAT_EXCEPTION = new Failure("NUMBER_FORMAT_EXCEPTION")
  val DATE_FORMAT_ERROR = new Failure("DATE_FORMAT_ERROR")
  val INVALID_DATA_TYPE = new Failure("INVALID_DATA_TYPE")

  val INVALID_BECH32_ADDRESS = new Failure("INVALID_BECH32_ADDRESS")
  val KEY_GENERATION_FAILED = new Failure("KEY_GENERATION_FAILED")
  val INVALID_ACCOUNT_ADDRESS = new Failure("INVALID_ACCOUNT_ADDRESS")
  val INVALID_OPERATOR_ADDRESS = new Failure("INVALID_OPERATOR_ADDRESS")
  val INVALID_HRP_OR_BYTES = new Failure("INVALID_HRP_OR_BYTES")
  val INVALID_MNEMONICS = new Failure("INVALID_MNEMONICS")
  val INVALID_MNEMONICS_OR_USERNAME = new Failure("INVALID_MNEMONICS_OR_USERNAME")
  val INVALID_ACTIVE_KEY = new Failure("INVALID_ACTIVE_KEY")
  val ACTIVATING_UNMANAGED_KEY = new Failure("ACTIVATING_UNMANAGED_KEY")
  val KEY_NOT_PROVISIONED = new Failure("KEY_NOT_PROVISIONED")

  val IPFS_UPLOAD_FAILED = new Failure("IPFS_UPLOAD_FAILED")
  val ERROR_DOWNLOADING_IPFS_FILE = new Failure("ERROR_DOWNLOADING_IPFS_FILE")
  val AMAZON_S3_UPLOAD_FAILURE = new Failure("AMAZON_S3_UPLOAD_FAILURE")
  val AMAZON_S3_PROCESS_FAILURE = new Failure("AMAZON_S3_PROCESS_FAILURE")
  val AMAZON_S3_CLIENT_CONNECTION_FAILURE = new Failure("AMAZON_S3_CLIENT_CONNECTION_FAILURE")
  val AMAZON_S3_NON_VERSIONED_BUCKET = new Failure("AMAZON_S3_NON_VERSIONED_BUCKET")
  val COLLECTION_UPLOAD_ERROR = new Failure("COLLECTION_UPLOAD_ERROR")

  val NO_SUCH_ELEMENT_EXCEPTION = new Failure("NO_SUCH_ELEMENT_EXCEPTION")
  val NULL_POINTER_EXCEPTION = new Failure("NULL_POINTER_EXCEPTION")
  val INVALID_FILE_PATH_EXCEPTION = new Failure("INVALID_FILE_PATH_EXCEPTION")
  val FILE_SECURITY_EXCEPTION = new Failure("FILE_SECURITY_EXCEPTION")
  val GENERIC_EXCEPTION = new Failure("GENERIC_EXCEPTION")
  val I_O_EXCEPTION = new Failure("I_O_EXCEPTION")
  val FILE_NOT_FOUND_EXCEPTION = new Failure("FILE_NOT_FOUND_EXCEPTION")
  val FILE_ILLEGAL_ARGUMENT_EXCEPTION = new Failure("FILE_ILLEGAL_ARGUMENT_EXCEPTION")
  val CLASS_CAST_EXCEPTION = new Failure("CLASS_CAST_EXCEPTION")
  val FILE_UNSUPPORTED_OPERATION_EXCEPTION = new Failure("FILE_UNSUPPORTED_OPERATION_EXCEPTION")
  val NO_SUCH_FILE_EXCEPTION = new Failure("NO_SUCH_FILE_EXCEPTION")
  val FILE_UPLOAD_ERROR = new Failure("FILE_UPLOAD_ERROR")
  val FILE_TYPE_NOT_FOUND = new Failure("FILE_TYPE_NOT_FOUND")

  val USERNAME_NOT_FOUND = new Failure("USERNAME_NOT_FOUND")
  val ADDRESS_NOT_FOUND = new Failure("ADDRESS_NOT_FOUND")
  val TOKEN_NOT_FOUND = new Failure("TOKEN_NOT_FOUND")
  val INVALID_SESSION = new Failure("INVALID_SESSION")
  val UNAUTHORIZED = new Failure("UNAUTHORIZED")
  val INVALID_PAGE_NUMBER = new Failure("INVALID_PAGE_NUMBER")
  val INVALID_SEEDS = new Failure("INVALID_SEEDS")
  val SEEDS_NOT_FOUND = new Failure("SEEDS_NOT_FOUND")
  val INVALID_KEY_NAME = new Failure("INVALID_KEY_NAME")
  val INVALID_PASSWORD_OR_SEEDS = new Failure("INVALID_PASSWORD_OR_SEEDS")
  val HD_PATH_NOT_FOUND = new Failure("HD_PATH_NOT_FOUND")
  val INVALID_WALLET_ADDRESS = new Failure("INVALID_WALLET_ADDRESS")
  val START_TIME_GREATER_THAN_EQUAL_TO_END_TIME = new Failure("START_TIME_GREATER_THAN_EQUAL_TO_END_TIME")
  val START_TIME_LESS_THAN_CURRENT_TIME = new Failure("START_TIME_LESS_THAN_CURRENT_TIME")
  val SECONDARY_MARKET_END_EPOCH_EXCEEDS_LIMIT = new Failure("SECONDARY_MARKET_END_EPOCH_EXCEEDS_LIMIT")
  val SECONDARY_MARKET_NFT_AMOUNT_EXCEEDS_LIMIT = new Failure("SECONDARY_MARKET_NFT_AMOUNT_EXCEEDS_LIMIT")
  val SECONDARY_MARKET_CANNOT_USE_ALL_THREE_OPTIONS = new Failure("SECONDARY_MARKET_CANNOT_USE_ALL_THREE_OPTIONS")
  val CANNOT_DELETE_ACTIVE_KEY = new Failure("CANNOT_DELETE_ACTIVE_KEY")
  val MICRO_NUMBER_PRECISION_MORE_THAN_REQUIRED = new Failure("MICRO_NUMBER_PRECISION_MORE_THAN_REQUIRED")
  val FROM_AND_TO_ADDRESS_SAME = new Failure("FROM_AND_TO_ADDRESS_SAME")
  val INVALID_FROM_ADDRESS = new Failure("INVALID_FROM_ADDRESS")
  val INVALID_TO_ADDRESS = new Failure("INVALID_TO_ADDRESS")
  val INSUFFICIENT_BALANCE = new Failure("INSUFFICIENT_BALANCE")
  val MAXIMUM_COLLECTION_PROPERTIES_EXCEEDED = new Failure("MAXIMUM_COLLECTION_PROPERTIES_EXCEEDED")
  val COLLECTION_PROPERTIES_CONTAINS_DEFAULT_PROPERTIES = new Failure("COLLECTION_PROPERTIES_CONTAINS_DEFAULT_PROPERTIES")
  val COLLECTION_PROPERTIES_CONTAINS_DUPLICATE_PROPERTIES = new Failure("COLLECTION_PROPERTIES_CONTAINS_DUPLICATE_PROPERTIES")
  val COLLECTION_PROPERTY_NOT_DEFINED = new Failure("COLLECTION_PROPERTY_NOT_DEFINED")
  val INVALID_NFT_TAGS_LENGTH = new Failure("INVALID_NFT_TAGS_LENGTH")
  val MAXIMUM_NFT_TAGS_EXCEEDED = new Failure("MAXIMUM_NFT_TAGS_EXCEEDED")
  val REPEATED_NFT_TAGS = new Failure("REPEATED_NFT_TAGS")
  val INVALID_ORDER = new Failure("INVALID_ORDER")
  val ORDER_NOT_CREATED_ON_BLOCKCHAIN = new Failure("ORDER_NOT_CREATED_ON_BLOCKCHAIN")

  val UNKNOWN_TRANSACTION_MESSAGE = new Failure("UNKNOWN_TRANSACTION_MESSAGE")
  val ARITHMETIC_OPERATION_ON_DIFFERENT_COIN = new Failure("ARITHMETIC_OPERATION_ON_DIFFERENT_COIN")
  val COIN_AMOUNT_NEGATIVE = new Failure("COIN_AMOUNT_NEGATIVE")
  val INVALID_SIGNATURE = new Failure("INVALID_SIGNATURE")
  val NO_SUCH_PROPOSAL_CONTENT_TYPE = new Failure("NO_SUCH_PROPOSAL_CONTENT_TYPE")
  val INVALID_BASE64_ENCODING = new Failure("INVALID_BASE64_ENCODING")
  val NO_SUCH_PUBLIC_KEY_TYPE = new Failure("NO_SUCH_PUBLIC_KEY_TYPE")
  val UNKNOWN_GRANT_AUTHORIZATION_RESPONSE_STRUCTURE = new Failure("UNKNOWN_GRANT_AUTHORIZATION_RESPONSE_STRUCTURE")
  val TRANSACTION_PROCESSING_FAILED = new Failure("TRANSACTION_PROCESSING_FAILED")
  val TRANSACTION_ALREADY_IN_MEMPOOL = new Failure("TRANSACTION_ALREADY_IN_MEMPOOL")
  val INVALID_NUMBER_FORMAT = new Failure("INVALID_NUMBER_FORMAT")
  val ACCOUNT_TYPE_NOT_FOUND = new Failure("ACCOUNT_TYPE_NOT_FOUND")
  val BALANCE_FETCH_FAILED = new Failure("BALANCE_FETCH_FAILED")
  val NO_COLLECTION_TO_CREATE_WHITELIST = new Failure("NO_COLLECTION_TO_CREATE_WHITELIST")
  val WHITELIST_MAX_MEMBERS_REACHED = new Failure("WHITELIST_MAX_MEMBERS_REACHED")
  val NOT_WHITELIST_CREATOR = new Failure("NOT_WHITELIST_CREATOR")
  val NO_SUCH_DOCUMENT_TYPE_EXCEPTION = new Failure("NO_SUCH_DOCUMENT_TYPE_EXCEPTION")
  val NOT_COLLECTION_OWNER = new Failure("NOT_COLLECTION_OWNER")
  val NOT_NFT_OWNER = new Failure("NOT_NFT_OWNER")
  val NFT_OWNER_NOT_FOUND = new Failure("NFT_OWNER_NOT_FOUND")
  val NFT_NOT_MINTED = new Failure("NFT_NOT_MINTED")
  val SELLING_MINT_E_COLLECTIONS_NOT_ALLOWED = new Failure("SELLING_MINT_E_COLLECTIONS_NOT_ALLOWED")
  val NFT_CREATOR_NOT_ALLOWED_SECONDARY_SALE = new Failure("NFT_CREATOR_NOT_ALLOWED_SECONDARY_SALE")
  val CLASSIFICATION_ALREADY_DEFINED = new Failure("CLASSIFICATION_ALREADY_DEFINED")
  val FILE_SIZE_EXCEED_LIMIT = new Failure("FILE_SIZE_EXCEED_LIMIT")
  val INVALID_DOCUMENT_TYPE = new Failure("INVALID_DOCUMENT_TYPE")
  val INVALID_DEFAULT_VALUE = new Failure("INVALID_DEFAULT_VALUE")
  val NFT_PROPERTY_NOT_FOUND = new Failure("NFT_PROPERTY_NOT_FOUND")
  val NFT_PROPERTY_NAME_NOT_FOUND = new Failure("NFT_PROPERTY_NAME_NOT_FOUND")
  val NFT_PROPERTY_DESCRIPTION_NOT_FOUND = new Failure("NFT_PROPERTY_DESCRIPTION_NOT_FOUND")
  val NFT_PROPERTY_TYPE_NOT_FOUND = new Failure("NFT_PROPERTY_TYPE_NOT_FOUND")
  val INVALID_NFT_PROPERTY = new Failure("INVALID_NFT_PROPERTY")
  val NOT_META_PROPERTY = new Failure("NOT_META_PROPERTY")
  val NOT_MESA_PROPERTY = new Failure("NOT_MESA_PROPERTY")
  val NOT_GENESIS_CREATOR = new Failure("NOT_GENESIS_CREATOR")
  val COLLECTION_NOT_FOUND = new Failure("COLLECTION_NOT_FOUND")
  val COLLECTION_NOT_PUBLIC = new Failure("COLLECTION_NOT_PUBLIC")
  val NFT_ALREADY_ON_SALE = new Failure("NFT_ALREADY_ON_SALE")
  val CANNOT_CREATE_MORE_THAN_ONE_SALE = new Failure("CANNOT_CREATE_MORE_THAN_ONE_SALE")
  val SALE_NOT_STARTED_OR_EXPIRED = new Failure("SALE_NOT_STARTED_OR_EXPIRED")
  val COLLECTION_ID_OR_WHITELIST_ID_DOES_NOT_EXIST = new Failure("COLLECTION_ID_OR_WHITELIST_ID_DOES_NOT_EXIST")
  val NOT_ENOUGH_NFTS_IN_COLLECTION = new Failure("NOT_ENOUGH_NFTS_IN_COLLECTION")
  val NFT_NOT_FOUND = new Failure("NFT_NOT_FOUND")
  val CANNOT_SELL_MORE_THAN_ALLOWED_LIMIT = new Failure("CANNOT_SELL_MORE_THAN_ALLOWED_LIMIT")
  val DIRECTORY_CREATION_FAILED = new Failure("DIRECTORY_CREATION_FAILED")
  val CANNOT_SELL_TO_YOURSELF = new Failure("CANNOT_SELL_TO_YOURSELF")
  val NOT_MEMBER_OF_WHITELIST = new Failure("NOT_MEMBER_OF_WHITELIST")
  val SALE_NOT_STARTED = new Failure("SALE_NOT_STARTED")
  val EARLY_ACCESS_NOT_STARTED = new Failure("EARLY_ACCESS_NOT_STARTED")
  val SALE_EXPIRED = new Failure("SALE_EXPIRED")
  val EARLY_ACCESS_ENDED = new Failure("EARLY_ACCESS_ENDED")
  val NO_SALE_ON_NFT = new Failure("NO_SALE_ON_NFT")
  val NOT_NOTIFICATION_OWNER = new Failure("NOT_NOTIFICATION_OWNER")
  val NFT_NOT_ON_SALE = new Failure("NFT_NOT_ON_SALE")
  val NFT_NOT_ON_PUBLIC_LISTING = new Failure("NFT_NOT_ON_PUBLIC_LISTING")
  val HANDLE_MULTIPLE_NFT_QUANTITY_CASE = new Failure("HANDLE_MULTIPLE_NFT_QUANTITY_CASE")
  val NFT_ALREADY_MINTED = new Failure("NFT_ALREADY_MINTED")
  val MAXIMUM_NFT_MINT_PER_ACCOUNT_REACHED = new Failure("MAXIMUM_NFT_MINT_PER_ACCOUNT_REACHED")
  val NFT_ALREADY_SOLD = new Failure("NFT_ALREADY_SOLD")
  val SIGNING_FAILED = new Failure("SIGNING_FAILED")
  val TRANSACTION_NOT_FOUND = new Failure("TRANSACTION_NOT_FOUND")
  val ORDER_ID_NOT_FOUND = new Failure("ORDER_ID_NOT_FOUND")
  val INVALID_IDENTITY_ISSUE_MESSAGE = new Failure("INVALID_IDENTITY_ISSUE_MESSAGE")
  val ADDRES_ALREADY_PROVISIONED = new Failure("ADDRES_ALREADY_PROVISIONED")

  class Failure(private val response: String) {
    val message: String = PREFIX + FAILURE_PREFIX + response
    val logMessage: String = LOG_PREFIX + response

    def throwBaseException(exception: Exception = null)(implicit module: String, logger: Logger) = throw new BaseException(this, exception)

    def throwFutureBaseException(exception: Exception = null)(implicit module: String, logger: Logger, executionContext: ExecutionContext) = Future(throw new BaseException(this, exception))
  }

  class Warning(private val response: String, private val actionController: JavaScriptReverseRoute = null) {
    val message: String = PREFIX + WARNING_PREFIX + response
  }

  class Success(private val response: String) {
    val message: String = Response.PREFIX + Response.SUCCESS_PREFIX + response
  }

  class Info(private val response: String) {
    val message: String = PREFIX + INFO_PREFIX + response
  }

}
