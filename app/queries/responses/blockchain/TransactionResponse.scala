package queries.responses.blockchain

import play.api.libs.json.{Json, Reads}
import utilities.Date.RFC3339

object TransactionResponse {

  case class TxResponse(height: String, txhash: String, code: Int, raw_log: String, gas_wanted: String, gas_used: String, timestamp: RFC3339)

  implicit val txResponseReads: Reads[TxResponse] = Json.reads[TxResponse]

  case class Response(tx_response: TxResponse) {

    def isSuccess: Boolean = this.tx_response.code == 0

    def log: String = this.tx_response.raw_log

  }

  implicit val responseReads: Reads[Response] = Json.reads[Response]

}
