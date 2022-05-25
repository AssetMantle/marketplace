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
