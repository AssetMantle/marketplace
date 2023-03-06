package schema.id.base

import com.ids.{AnyID, AnyOwnableID, AssetID => protoAssetID}
import schema.id.OwnableID

case class AssetID(hashID: HashID) extends OwnableID {

  def getBytes: Array[Byte] = this.hashID.getBytes

  def asString: String = utilities.Secrets.base64URLEncoder(this.getBytes)

  def asProtoAssetID: protoAssetID = protoAssetID.newBuilder().setHashID(this.hashID.asProtoHashID).build()

  def toAnyOwnableID: AnyOwnableID = AnyOwnableID.newBuilder().setAssetID(this.asProtoAssetID).build()

  def toAnyID: AnyID = AnyID.newBuilder().setAssetID(this.asProtoAssetID).build()

  def getProtoBytes: Array[Byte] = this.asProtoAssetID.toByteString.toByteArray

  def isCoinId: Boolean = false

}

object AssetID {
  def apply(anyID: protoAssetID): AssetID = AssetID(HashID(anyID.getHashID))
}
