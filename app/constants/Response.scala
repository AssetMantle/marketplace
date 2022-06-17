package constants

import play.api.routing.JavaScriptReverseRoute

object Response {

  private val PREFIX = "RESPONSE."
  private val FAILURE_PREFIX = "FAILURE."
  private val WARNING_PREFIX = "WARNING."
  private val SUCCESS_PREFIX = "SUCCESS."
  private val INFO_PREFIX = "INFO."
  private val LOG_PREFIX = "LOG."

  val ACCOUNT_INSERT_FAILED = new Failure("ACCOUNT_INSERT_FAILED")
  val ACCOUNT_UPSERT_FAILED = new Failure("ACCOUNT_UPSERT_FAILED")
  val ACCOUNT_NOT_FOUND = new Failure("ACCOUNT_NOT_FOUND")

  val INVALID_USERNAME_OR_PASSWORD = new Failure("INVALID_USERNAME_OR_PASSWORD")
  val INVALID_PASSWORD = new Failure("INVALID_PASSWORD")
  val PASSWORDS_DO_NOT_MATCH = new Failure("PASSWORDS_DO_NOT_MATCH")
  val USERNAME_UNAVAILABLE = new Failure("USERNAME_UNAVAILABLE")

  val SIGN_UP_SUCCESSFUL = new Success("SIGN_UP_SUCCESSFUL")

  val JSON_PARSE_EXCEPTION = new Failure("JSON_PARSE_EXCEPTION")
  val JSON_MAPPING_EXCEPTION = new Failure("JSON_MAPPING_EXCEPTION")

  val INVALID_BECH32_ADDRESS = new Failure("INVALID_BECH32_ADDRESS")
  val KEY_GENERATION_FAILED = new Failure("KEY_GENERATION_FAILED")
  val INVALID_ACCOUNT_ADDRESS = new Failure("INVALID_ACCOUNT_ADDRESS")
  val INVALID_OPERATOR_ADDRESS = new Failure("INVALID_OPERATOR_ADDRESS")
  val INVALID_HRP_OR_BYTES = new Failure("INVALID_HRP_OR_BYTES")
  val INVALID_MNEMONICS = new Failure("INVALID_MNEMONICS")

  val IPFS_UPLOAD_FAILED = new Failure("IPFS_UPLOAD_FAILED")
  val AMAZON_S3_UPLOAD_FAILURE = new Failure("AMAZON_S3_UPLOAD_FAILURE")
  val AMAZON_S3_PROCESS_FAILURE = new Failure("AMAZON_S3_PROCESS_FAILURE")
  val AMAZON_S3_CLIENT_CONNECTION_FAILURE = new Failure("AMAZON_S3_CLIENT_CONNECTION_FAILURE")
  val AMAZON_S3_NON_VERSIONED_BUCKET = new Failure("AMAZON_S3_NON_VERSIONED_BUCKET")

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

  class Failure(private val response: String, private val actionController: JavaScriptReverseRoute = null) {
    val message: String = PREFIX + FAILURE_PREFIX + response
    val action: String = utilities.JsRoutes.getJsRouteString(actionController)
    val logMessage: String = LOG_PREFIX + response
  }

  class Warning(private val response: String, private val actionController: JavaScriptReverseRoute = null) {
    val message: String = PREFIX + WARNING_PREFIX + response
    val action: String = utilities.JsRoutes.getJsRouteString(actionController)
  }

  class Success(private val response: String, private val actionController: JavaScriptReverseRoute = null) {
    val message: String = Response.PREFIX + Response.SUCCESS_PREFIX + response
    val action: String = utilities.JsRoutes.getJsRouteString(actionController)
  }

  class Info(private val response: String, private val actionController: JavaScriptReverseRoute = null) {
    val message: String = PREFIX + INFO_PREFIX + response
    val action: String = utilities.JsRoutes.getJsRouteString(actionController)
  }

}