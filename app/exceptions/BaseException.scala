package exceptions

import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

class BaseException(val failure: constants.Response.Failure, val exception: Exception = null)(implicit module: String, logger: Logger) extends Exception {
  logger.error(failure.logMessage, exception)

  private def getMessage(msg: String) = s"${module}: ${this.failure.logMessage}: ${msg}"

  def notifyLog(message: String = "")(implicit executionContext: ExecutionContext): Future[Unit] = utilities.Telegram.send(this.getMessage(message))
}
