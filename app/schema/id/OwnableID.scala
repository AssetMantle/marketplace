package schema.id

import com.assetmantle.schema.ids.base.AnyOwnableID
import org.slf4j.{Logger, LoggerFactory}
import schema.id.base.{AssetID, CoinID}

abstract class OwnableID extends ID {
  def toAnyOwnableID: AnyOwnableID

  def isCoinId: Boolean

}

object OwnableID {

  implicit val module: String = constants.Module.SCHEMA_OWNABLE_ID

  implicit val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def apply(anyOwnableID: AnyOwnableID): OwnableID = anyOwnableID.getImplCase.getNumber match {
    case 1 => AssetID(anyOwnableID.getAssetID)
    case 2 => CoinID(anyOwnableID.getCoinID)
    case _ => throw new IllegalArgumentException("INVALID_OWNABLE_ID_IMPL_CASE_NUMBER: " + anyOwnableID.getImplCase.getNumber.toString)
  }

  def apply(protoBytes: Array[Byte]): OwnableID = OwnableID(AnyOwnableID.parseFrom(protoBytes))
}