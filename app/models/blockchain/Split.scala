package models.blockchain

import models.traits.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.id.OwnableID
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Split(ownerID: Array[Byte], ownableID: Array[Byte], value: BigDecimal, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[Array[Byte], Array[Byte]] {

  def id1: Array[Byte] = this.ownerID

  def id2: Array[Byte] = this.ownableID

}

object Splits {

  implicit val module: String = constants.Module.BLOCKCHAIN_SPLIT

  implicit val logger: Logger = Logger(this.getClass)

  class DataTable(tag: Tag) extends Table[Split](tag, "Split") with ModelTable2[Array[Byte], Array[Byte]] {

    def * = (ownerID, ownableID, value, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (Split.tupled, Split.unapply)

    def ownerID = column[Array[Byte]]("ownerID", O.PrimaryKey)

    def ownableID = column[Array[Byte]]("ownableID", O.PrimaryKey)

    def value = column[BigDecimal]("value")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = ownerID

    def id2 = ownableID

  }

  val TableQuery = new TableQuery(tag => new DataTable(tag))

}

@Singleton
class Splits @Inject()(
                        @NamedDatabase("explorer")
                        protected val databaseConfigProvider: DatabaseConfigProvider
                      )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[Splits.DataTable, Split, Array[Byte], Array[Byte]](
    databaseConfigProvider,
    Splits.TableQuery,
    executionContext,
    Splits.module,
    Splits.logger
  ) {
  object Service {

    def getByOwnerID(ownerId: IdentityID): Future[Seq[Split]] = filter(_.ownerID === ownerId.getBytes)

    def getByOwnableID(ownableID: OwnableID): Future[Seq[Split]] = filter(_.ownableID === ownableID.getBytes)

    def getTotalSupply(ownableID: OwnableID): Future[BigDecimal] = filter(_.ownableID === ownableID.getBytes).map(_.map(_.value).sum)

    def getByOwnerIDAndOwnableID(ownerId: IdentityID, ownableID: OwnableID): Future[Option[Split]] = filter(x => x.ownerID === ownerId.getBytes && x.ownableID === ownableID.getBytes).map(_.headOption)

    def tryGetByOwnerIDAndOwnableID(ownerId: IdentityID, ownableID: OwnableID): Future[Split] = tryGetById1AndId2(id1 = ownerId.getBytes, id2 = ownableID.getBytes)

  }

}