
package constants

import schema.id.base.AssetID

object SecondaryMarket {

  case class Status(id: Int, value: String)

  val NOT_STARTED: Status = Status(0, "NOT_STARTED")
  val LIVE: Status = Status(1, "LIVE")
  val SOLD_OUT: Status = Status(2, "SOLD_OUT")
  val ENDED: Status = Status(3, "ENDED")

  val TokensAllowed: Seq[String] = Seq("umntl")
  val AssetTokensAllowed: Seq[AssetID] = TokensAllowed.map(x => schema.document.CoinAsset.getCoinAssetID(x))
}
