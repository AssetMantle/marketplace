package exceptions

import play.api.Logger

class BaseException(val failure: constants.Response.Failure, val exception: Exception = null)(implicit logger: Logger) extends Exception {
  logger.error(failure.logMessage, exception)
}
