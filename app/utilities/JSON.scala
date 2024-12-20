package utilities

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import exceptions.BaseException
import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.WSResponse

import scala.concurrent.{ExecutionContext, Future}

object JSON {

  def convertJsonStringToObject[T](jsonString: String)(implicit module: String, logger: Logger, reads: Reads[T]): T = {
    try {
      Json.fromJson[T](Json.parse(jsonString)) match {
        case JsSuccess(value: T, _: JsPath) => value
        case errors: JsError => logger.error(errors.toString)
          logger.error(jsonString)
          throw new BaseException(constants.Response.JSON_PARSE_EXCEPTION)
      }
    }
    catch {
      case jsonParseException: JsonParseException => logger.error(jsonParseException.getMessage, jsonParseException)
        throw new BaseException(constants.Response.JSON_PARSE_EXCEPTION)
      case jsonMappingException: JsonMappingException => logger.error(jsonMappingException.getMessage, jsonMappingException)
        throw new BaseException(constants.Response.JSON_MAPPING_EXCEPTION)
    }
  }

  def getResponseFromJson[T](response: Future[WSResponse])(implicit exec: ExecutionContext, logger: Logger, module: String, reads: Reads[T]): Future[T] = {
    response.map { response =>
      Json.fromJson[T](response.json) match {
        case JsSuccess(value: T, _: JsPath) => value
        case jsError: JsError => logger.error(response.json.toString())
          logger.error(jsError.toString)
          constants.Response.JSON_PARSE_EXCEPTION.throwBaseException()
      }
    }.recover {
      case jsonParseException: JsonParseException => logger.error(jsonParseException.getMessage, jsonParseException)
        constants.Response.JSON_PARSE_EXCEPTION.throwBaseException(jsonParseException)
      case jsonMappingException: JsonMappingException => logger.error(jsonMappingException.getMessage, jsonMappingException)
        constants.Response.JSON_MAPPING_EXCEPTION.throwBaseException(jsonMappingException)
      case baseException: BaseException => throw baseException
    }
  }

  def getResponseFromJson[T1, T2](wsResponse: Future[WSResponse])(implicit exec: ExecutionContext, logger: Logger, module: String, reads1: Reads[T1], reads2: Reads[T2]): Future[(Option[T1], Option[T2])] = {
    wsResponse.map { response =>
      Json.fromJson[T1](response.json) match {
        case JsSuccess(value: T1, _: JsPath) => (Option(value), None)
        case error: JsError => logger.error(response.json.toString())
          logger.error(error.toString)
          constants.Response.JSON_PARSE_EXCEPTION.throwBaseException()
      }
    }
  }.recover {
    case jsonParseException: JsonParseException => logger.error(jsonParseException.getMessage, jsonParseException)
      constants.Response.JSON_PARSE_EXCEPTION.throwBaseException(jsonParseException)
    case jsonMappingException: JsonMappingException => logger.error(jsonMappingException.getMessage, jsonMappingException)
      constants.Response.JSON_MAPPING_EXCEPTION.throwBaseException(jsonMappingException)
    case baseException: BaseException => throw baseException
  }

}