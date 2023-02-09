package transactions.blockchain

import constants.Response.Failure
import exceptions.BaseException
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.{Configuration, Logger}
import transactions.responses.blockchain.BroadcastTxSyncResponse

import java.net.ConnectException
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BroadcastTxSync @Inject()()(implicit wsClient: WSClient, configuration: Configuration, executionContext: ExecutionContext) {

  private implicit val module: String = constants.Module.BROADCAST_TX_SYNC

  private implicit val logger: Logger = Logger(this.getClass)

  private val url = constants.Blockchain.RPCEndPoint + "/broadcast_tx_sync?tx=0x"

  private def action(txRawHex: String) = utilities.JSON.getResponseFromJson[BroadcastTxSyncResponse.Response, BroadcastTxSyncResponse.ErrorResponse](wsClient.url(url + txRawHex).get)

  object Service {
    def get(txRawHex: String): Future[(Option[BroadcastTxSyncResponse.Response], Option[BroadcastTxSyncResponse.ErrorResponse])] = action(txRawHex).recover {
      case connectException: ConnectException => throw new BaseException(constants.Response.CONNECT_EXCEPTION, connectException)
      case baseException: BaseException => throw baseException
    }
  }

}
