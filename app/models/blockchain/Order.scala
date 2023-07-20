package models.blockchain

import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.data.base.{DecData, HeightData, IDData, NumberData}
import schema.document.Document
import schema.id.OwnableID
import schema.id.base._
import schema.property.Property
import schema.property.base.MetaProperty
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Order(id: Array[Byte], idString: String, classificationID: Array[Byte], immutables: Array[Byte], mutables: Array[Byte]) extends Entity[Array[Byte]] {

  def getIDString: String = utilities.Secrets.base64URLEncoder(this.id)

  def getID: OrderID = OrderID(HashID(this.id))

  def getClassificationIDString: String = utilities.Secrets.base64URLEncoder(this.classificationID)

  def getClassificationID: ClassificationID = ClassificationID(this.classificationID)

  def getImmutables: Immutables = Immutables(this.immutables)

  def getMutables: Mutables = Mutables(this.mutables)

  def getDocument: Document = Document(this.getClassificationID, this.getImmutables, this.getMutables)

  def getProperty(id: PropertyID): Option[Property] = this.getDocument.getProperty(id)

  def getMakerID: IdentityID = {
    val property = this.getProperty(schema.constants.Properties.MakerIDProperty.getID)
    if (property.isDefined && property.get.isMeta) IdentityID(IDData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).getAnyID.getIdentityID) else IdentityID(HashID(Array[Byte]()))
  }

  def getMakerOwnableID: OwnableID = {
    val property = this.getProperty(schema.constants.Properties.MakerOwnableIDProperty.getID)
    if (property.isDefined && property.get.isMeta) OwnableID(IDData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).getProtoBytes) else OwnableID(IDData(StringID("")).getProtoBytes)
  }

  def getTakerOwnableID: OwnableID = {
    val property = this.getProperty(schema.constants.Properties.TakerOwnableIDProperty.getID)
    if (property.isDefined && property.get.isMeta) OwnableID(IDData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).getProtoBytes) else OwnableID(IDData(StringID("")).getProtoBytes)
  }

  def getExchangeRate: BigDecimal = {
    val property = this.getProperty(schema.constants.Properties.ExchangeRateProperty.getID)
    if (property.isDefined && property.get.isMeta) DecData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).getValue else schema.data.constants.ZeroDec
  }

  def getExpiryHeight: Long = {
    val property = this.getProperty(schema.constants.Properties.ExpiryHeightProperty.getID)
    if (property.isDefined && property.get.isMeta) HeightData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).value.value else -1
  }

  def getMakerOwnableSplit: BigInt = {
    val property = this.getProperty(schema.constants.Properties.MakerOwnableSplitProperty.getID)
    if (property.isDefined && property.get.isMeta) NumberData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes).value else BigInt(1)
  }

  def mutate(properties: Seq[Property]): Order = this.copy(mutables = this.getMutables.mutate(properties).getProtoBytes)

  def getExpiryFromNow(latestBlock: Int): Long = ((this.getExpiryHeight - latestBlock) * constants.Blockchain.MaxOrderHours) / constants.Blockchain.MaxOrderExpiry
}

private[blockchain] object Orders {

  class OrderTable(tag: Tag) extends Table[Order](tag, "Order") with ModelTable[Array[Byte]] {

    def * = (id, idString, classificationID, immutables, mutables) <> (Order.tupled, Order.unapply)

    def id = column[Array[Byte]]("id", O.PrimaryKey)

    def idString = column[String]("idString")

    def classificationID = column[Array[Byte]]("classificationID")

    def immutables = column[Array[Byte]]("immutables")

    def mutables = column[Array[Byte]]("mutables")

  }

}

@Singleton
class Orders @Inject()(
                        @NamedDatabase("explorer")
                        protected val dbConfigProvider: DatabaseConfigProvider,
                      )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Orders.OrderTable, Order, Array[Byte]]() {

  implicit val module: String = constants.Module.BLOCKCHAIN_ORDER

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new Orders.OrderTable(tag))

  object Service {

    def get(id: String): Future[Option[Order]] = getById(utilities.Secrets.base64URLDecode(id))

    def get(ids: Seq[String]): Future[Seq[Order]] = filter(_.id.inSet(ids.map(utilities.Secrets.base64URLDecode)))

    def get(id: Array[Byte]): Future[Option[Order]] = getById(id)

    def tryGet(id: String): Future[Order] = tryGetById(utilities.Secrets.base64URLDecode(id))

    def tryGet(id: OrderID): Future[Order] = tryGetById(id.getBytes)

    def filterExistingIds(ids: Seq[String]): Future[Seq[String]] = customQuery(tableQuery.filter(_.idString.inSet(ids)).map(_.idString).result)

  }

}