package models.blockchain

import models.traits.{Entity2, GenericDaoImpl2, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.id.base.{AssetID, IdentityID}
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Split(ownerID: Array[Byte], assetID: Array[Byte], ownerIDString: String, assetIDString: String, value: BigInt, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) {

  def serialize: SplitSerialized = SplitSerialized(
    ownerID = this.ownerID,
    assetID = this.assetID,
    ownerIDString = this.ownerIDString,
    assetIDString = this.assetIDString,
    value = BigDecimal(this.value),
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def getBalanceAsMicroNumber: MicroNumber = MicroNumber(this.value)
}

case class SplitSerialized(ownerID: Array[Byte], assetID: Array[Byte], ownerIDString: String, assetIDString: String, value: BigDecimal, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[Array[Byte], Array[Byte]] {

  def id1: Array[Byte] = this.ownerID

  def id2: Array[Byte] = this.assetID

  def deserialize: Split = Split(
    ownerID = this.ownerID,
    assetID = this.assetID,
    ownerIDString = this.ownerIDString,
    assetIDString = this.assetIDString,
    value = this.value.toBigInt,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

}

private[blockchain] object Splits {

  class SplitTable(tag: Tag) extends Table[SplitSerialized](tag, "Split") with ModelTable2[Array[Byte], Array[Byte]] {

    def * = (ownerID, assetID, ownerIDString, assetIDString, value, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SplitSerialized.tupled, SplitSerialized.unapply)

    def ownerID = column[Array[Byte]]("ownerID", O.PrimaryKey)

    def assetID = column[Array[Byte]]("assetID", O.PrimaryKey)

    def ownerIDString = column[String]("ownerIDString")

    def assetIDString = column[String]("assetIDString")

    def value = column[BigDecimal]("value")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = ownerID

    def id2 = assetID

  }
}


@Singleton
class Splits @Inject()(
                        @NamedDatabase("explorer")
                        protected val dbConfigProvider: DatabaseConfigProvider,
                      )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[Splits.SplitTable, SplitSerialized, Array[Byte], Array[Byte]]() {

  implicit val module: String = constants.Module.BLOCKCHAIN_SPLIT

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new Splits.SplitTable(tag))

  object Service {

    def getByOwnerID(ownerId: IdentityID): Future[Seq[Split]] = filter(_.ownerID === ownerId.getBytes).map(_.map(_.deserialize))

    def getByAssetID(assetID: AssetID): Future[Seq[Split]] = filter(_.assetID === assetID.getBytes).map(_.map(_.deserialize))

    def getTotalSupply(assetID: AssetID): Future[BigInt] = filter(_.assetID === assetID.getBytes).map(_.map(_.value).sum.toBigInt)

    def getByOwnerIDAndAssetID(ownerId: IdentityID, assetID: AssetID): Future[Option[Split]] = filter(x => x.ownerID === ownerId.getBytes && x.assetID === assetID.getBytes).map(_.headOption).map(_.map(_.deserialize))

    def tryGetByOwnerIDAndAssetID(ownerId: IdentityID, assetID: AssetID): Future[Split] = tryGetById1AndId2(id1 = ownerId.getBytes, id2 = assetID.getBytes).map(_.deserialize)

  }

}