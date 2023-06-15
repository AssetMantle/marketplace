package models.blockchain

import models.traits.{Entity2, GenericDaoImpl2, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.id.OwnableID
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Split(ownerID: Array[Byte], ownableID: Array[Byte], protoOwnableID: Array[Byte], ownerIDString: String, ownableIDString: String, value: BigInt) {

  def serialize: Splits.SplitSerialized = Splits.SplitSerialized(
    ownerID = this.ownerID,
    ownableID = this.ownableID,
    protoOwnableID = this.protoOwnableID,
    ownerIDString = this.ownerIDString,
    ownableIDString = this.ownableIDString,
    value = BigDecimal(this.value))

  def getBalanceAsMicroNumber: MicroNumber = MicroNumber(this.value)
}

private[blockchain] object Splits {

  case class SplitSerialized(ownerID: Array[Byte], ownableID: Array[Byte], protoOwnableID: Array[Byte], ownerIDString: String, ownableIDString: String, value: BigDecimal) extends Entity2[Array[Byte], Array[Byte]] {

    def id1: Array[Byte] = this.ownerID

    def id2: Array[Byte] = this.ownableID

    def deserialize()(implicit module: String, logger: Logger): Split = Split(
      ownerID = this.ownerID,
      ownableID = this.ownableID,
      protoOwnableID = this.protoOwnableID,
      ownerIDString = this.ownerIDString,
      ownableIDString = this.ownableIDString,
      value = this.value.toBigInt,
    )

  }

  class SplitTable(tag: Tag) extends Table[SplitSerialized](tag, "Split") with ModelTable2[Array[Byte], Array[Byte]] {

    def * = (ownerID, ownableID, protoOwnableID, ownerIDString, ownableIDString, value) <> (SplitSerialized.tupled, SplitSerialized.unapply)

    def ownerID = column[Array[Byte]]("ownerID", O.PrimaryKey)

    def ownableID = column[Array[Byte]]("ownableID", O.PrimaryKey)

    def protoOwnableID = column[Array[Byte]]("protoOwnableID")

    def ownerIDString = column[String]("ownerIDString")

    def ownableIDString = column[String]("ownableIDString")

    def value = column[BigDecimal]("value")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = ownerID

    def id2 = ownableID

  }

}


@Singleton
class Splits @Inject()(
                        @NamedDatabase("explorer")
                        protected val dbConfigProvider: DatabaseConfigProvider,
                      )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[Splits.SplitTable, Splits.SplitSerialized, Array[Byte], Array[Byte]]() {

  implicit val module: String = constants.Module.BLOCKCHAIN_SPLIT

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new Splits.SplitTable(tag))

  object Service {

    def getByOwnerID(ownerId: IdentityID): Future[Seq[Split]] = filter(_.ownerID === ownerId.getBytes).map(_.map(_.deserialize))

    def getByOwnableID(ownableID: OwnableID): Future[Seq[Split]] = filter(_.ownableID === ownableID.getBytes).map(_.map(_.deserialize))

    def getTotalSupply(ownableID: OwnableID): Future[BigInt] = filter(_.ownableID === ownableID.getBytes).map(_.map(_.value).sum.toBigInt)

    def getByOwnerIDAndOwnableID(ownerId: IdentityID, ownableID: OwnableID): Future[Option[Split]] = filter(x => x.ownerID === ownerId.getBytes && x.ownableID === ownableID.getBytes).map(_.headOption).map(_.map(_.deserialize))

    def tryGetByOwnerIDAndOwnableID(ownerId: IdentityID, ownableID: OwnableID): Future[Split] = tryGetById1AndId2(id1 = ownerId.getBytes, id2 = ownableID.getBytes).map(_.deserialize)

  }

}