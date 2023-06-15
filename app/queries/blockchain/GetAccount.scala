package queries.blockchain

import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}
import queries.responses.blockchain.AccountResponse

import java.net.ConnectException
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetAccount @Inject()()(implicit wsClient: WSClient, configuration: Configuration, executionContext: ExecutionContext) {

  implicit val module: String = constants.Module.GET_ACCOUNT

  implicit val logger: Logger = Logger(this.getClass)

  private val path = "/cosmos/auth/v1beta1/accounts/"

  private val url = constants.Blockchain.RestEndPoint + path

  private def action(address: String): Future[AccountResponse.Response] = utilities.JSON.getResponseFromJson[AccountResponse.Response](wsClient.url(url + address).get)

  object Service {
    def get(address: String): Future[AccountResponse.Response] = action(address).recover {
      case connectException: ConnectException => constants.Response.CONNECT_EXCEPTION.throwBaseException(connectException)
    }
  }

}
