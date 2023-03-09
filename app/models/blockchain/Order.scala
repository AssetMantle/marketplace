package models.blockchain

import models.traits.{Entity, GenericDaoImpl, Logging, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.data.base.{DecData, IDData}
import schema.document.Document
import schema.id.OwnableID
import schema.id.base._
import schema.property.Property
import schema.property.base.MetaProperty
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Order(id: Array[Byte], idString: String, classificationID: Array[Byte], immutables: Array[Byte], mutables: Array[Byte], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[Array[Byte]] {

  def getIDString: String = utilities.Secrets.base64URLEncoder(this.id)

  def getID: OrderID = OrderID(HashID(this.id))

  def getClassificationIDString: String = utilities.Secrets.base64URLEncoder(this.classificationID)

  def getClassificationID: ClassificationID = ClassificationID(this.classificationID)

  def getImmutables: Immutables = Immutables(this.immutables)

  def getMutables: Mutables = Mutables(this.mutables)

  def getDocument: Document = Document(this.getClassificationID, this.getImmutables, this.getMutables)

  def getProperty(id: PropertyID): Option[Property] = this.getDocument.getProperty(id)

  def getMakerID: IdentityID = {
    val property = this.getProperty(constants.Blockchain.MakerIDProperty.getID)
    if (property.isDefined && property.get.isMeta) IdentityID(IDData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).getAnyID.getIdentityID) else IdentityID(HashID(Array[Byte]()))
  }

  def getMakerOwnableID: OwnableID = {
    val property = this.getProperty(constants.Blockchain.MakerOwnableIDProperty.getID)
    if (property.isDefined && property.get.isMeta) OwnableID(IDData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).getProtoBytes) else OwnableID(IDData(StringID("").toAnyID).getProtoBytes)
  }

  def getTakerOwnableID: OwnableID = {
    val property = this.getProperty(constants.Blockchain.TakerOwnableIDProperty.getID)
    if (property.isDefined && property.get.isMeta) OwnableID(IDData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).getProtoBytes) else OwnableID(IDData(StringID("").toAnyID).getProtoBytes)
  }

  def getExchangeRate: BigDecimal = {
    val property = this.getProperty(constants.Blockchain.ExchangeRateProperty.getID)
    if (property.isDefined && property.get.isMeta) DecData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).value.toBigDecimal else constants.Blockchain.SmallestDec
  }

  def getMakerOwnableSplit: BigDecimal = {
    val property = this.getProperty(constants.Blockchain.MakerOwnableSplitProperty.getID)
    if (property.isDefined && property.get.isMeta) DecData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).value.toBigDecimal else constants.Blockchain.SmallestDec
  }

  def mutate(properties: Seq[Property]): Order = this.copy(mutables = this.getMutables.mutate(properties).getProtoBytes)
}

object Orders {

  implicit val module: String = constants.Module.BLOCKCHAIN_ORDER

  implicit val logger: Logger = Logger(this.getClass)

  class DataTable(tag: Tag) extends Table[Order](tag, "Order") with ModelTable[Array[Byte]] {

    def * = (id, idString, classificationID, immutables, mutables, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (Order.tupled, Order.unapply)

    def id = column[Array[Byte]]("id", O.PrimaryKey)

    def idString = column[String]("idString")

    def classificationID = column[Array[Byte]]("classificationID")

    def immutables = column[Array[Byte]]("immutables")

    def mutables = column[Array[Byte]]("mutables")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

  }

  val TableQuery = new TableQuery(tag => new DataTable(tag))

}

@Singleton
class Orders @Inject()(
                        @NamedDatabase("explorer")
                        protected val databaseConfigProvider: DatabaseConfigProvider
                      )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Orders.DataTable, Order, Array[Byte]](
    databaseConfigProvider,
    Orders.TableQuery,
    executionContext,
    Orders.module,
    Orders.logger
  ) {

  object Service {

    def get(id: String): Future[Option[Order]] = getById(utilities.Secrets.base64URLDecode(id))

    def get(id: Array[Byte]): Future[Option[Order]] = getById(id)

    def tryGet(id: String): Future[Order] = tryGetById(utilities.Secrets.base64URLDecode(id))

    def tryGet(id: OrderID): Future[Order] = tryGetById(id.getBytes)

  }

}