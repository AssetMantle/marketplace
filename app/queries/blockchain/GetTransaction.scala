package queries.blockchain

import exceptions.BaseException
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}
import queries.responses.blockchain.TransactionResponse.Response

import java.net.ConnectException
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetTransaction @Inject()()(implicit wsClient: WSClient, configuration: Configuration, executionContext: ExecutionContext) {

  private implicit val module: String = constants.Module.GET_TRANSACTION

  private implicit val logger: Logger = Logger(this.getClass)

  private val url = constants.CommonConfig.Blockchain.RestEndPoint + "/cosmos/tx/v1beta1/txs/"

  private def action(txHash: String): Future[Response] = utilities.JSON.getResponseFromJson[Response](wsClient.url(url + txHash).get)


  object Service {

    def get(txHash: String): Future[Response] = action(txHash).recover {
      case connectException: ConnectException => throw new BaseException(constants.Response.CONNECT_EXCEPTION, connectException)
    }

  }

}