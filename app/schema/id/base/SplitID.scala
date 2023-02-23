
package schema.id.base

import com.ids.{AnyID, SplitID => protoSplitID}
import schema.id.{ID, OwnableID}

case class SplitID(ownerID: IdentityID, ownableID: OwnableID) extends ID {

  def getBytes: Array[Byte] = this.ownerID.getBytes ++ this.ownableID.getBytes

  def asString: String = this.ownerID.asString + constants.Blockchain.IDSeparator + this.ownableID.asString

  def asProtoSplitID: protoSplitID = protoSplitID.newBuilder().setOwnerID(this.ownerID.asProtoIdentityID).setOwnableID(this.ownableID.toAnyOwnableID).build()

  def toAnyID: AnyID = AnyID.newBuilder().setSplitID(this.asProtoSplitID).build()

  def getProtoBytes: Array[Byte] = this.asProtoSplitID.toByteString.toByteArray
}

object SplitID {
  def apply(anyID: protoSplitID): SplitID = SplitID(ownerID = IdentityID(anyID.getOwnerID), ownableID = OwnableID(anyID.getOwnableID))
}