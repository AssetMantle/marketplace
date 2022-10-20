package controllers.error

import play.api.http._
import controllers.error.{HttpErrorHandler => customHttpErrorHandler}
import javax.inject._

@Singleton
class ErrorHandler @Inject()(
                              jsonHttpErrorHandler: JsonHttpErrorHandler,
                              httpErrorHandler: customHttpErrorHandler,
                            ) extends PreferredMediaTypeHttpErrorHandler(
  "application/json" -> jsonHttpErrorHandler,
  "application/x-www-form-urlencoded" -> httpErrorHandler,
  "multipart/form-data" -> httpErrorHandler,
  "text/plain" -> httpErrorHandler,
  "application/xml" -> httpErrorHandler,
  "text/html" -> httpErrorHandler,
)