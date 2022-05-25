package constants

import play.api.routing.JavaScriptReverseRoute

object Response {

  val PREFIX = "RESPONSE."
  val FAILURE_PREFIX = "FAILURE."
  val WARNING_PREFIX = "WARNING."
  val SUCCESS_PREFIX = "SUCCESS."
  val INFO_PREFIX = "INFO."
  val LOG_PREFIX = "LOG."
  val KEY_ASSET = "asset"
  val KEY_FIAT = "fiat"
  val KEY_NEGOTIATION_ID = "negotiation_id"
  val KEY_ORDER_ID = "order_id"
  val KEY_EXECUTED = "executed"

  val ACCOUNT_INSERT_FAILED = new Failure("ACCOUNT_INSERT_FAILED")
  val ACCOUNT_UPSERT_FAILED = new Failure("ACCOUNT_UPSERT_FAILED")
  val ACCOUNT_NOT_FOUND = new Failure("ACCOUNT_NOT_FOUND")

  val INVALID_USERNAME_OR_PASSWORD = new Failure("INVALID_USERNAME_OR_PASSWORD")
  val INVALID_PASSWORD = new Failure("INVALID_PASSWORD")
  val PASSWORDS_DO_NOT_MATCH = new Failure("PASSWORDS_DO_NOT_MATCH")
  val USERNAME_UNAVAILABLE = new Failure("USERNAME_UNAVAILABLE")

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
