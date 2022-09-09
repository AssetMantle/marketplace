package controllers.error

import play.api.http._

import javax.inject._

@Singleton
class HttpErrorHandler @Inject()(
                                  xmlHttpRequestHandler: XmlHttpRequestHandler,
                                  jsonHttpErrorHandler: JsonHttpErrorHandler,
                                  httpErrorHandler: DefaultHttpErrorHandler,
                                ) extends PreferredMediaTypeHttpErrorHandler(
  "application/json" -> jsonHttpErrorHandler,
  "application/x-www-form-urlencoded" -> httpErrorHandler,
  "multipart/form-data" -> httpErrorHandler,
  "text/plain" -> httpErrorHandler,
  "application/xml" -> xmlHttpRequestHandler,

)