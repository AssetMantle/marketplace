package models.blockchain

import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.data.base.NumberData
import schema.document.Document
import schema.id.base.{ClassificationID, HashID, PropertyID}
import schema.property.Property
import schema.property.base.MetaProperty
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Classification(id: Array[Byte], idString: String, immutables: Array[Byte], mutables: Array[Byte]) extends Entity[Array[Byte]] {

  def getIDString: String = utilities.Secrets.base64URLEncoder(this.id)

  def getID: ClassificationID = ClassificationID(HashID(this.id))

  def getImmutables: Immutables = Immutables(this.immutables)

  def getMutables: Mutables = Mutables(this.mutables)

  def getDocument: Document = Document(this.getID, this.getImmutables, this.getMutables)

  def getProperty(id: PropertyID): Option[Property] = this.getDocument.getProperty(id)

  def getBondAmount: MicroNumber = {
    val property = this.getProperty(schema.constants.Properties.BondAmountProperty.getID)
    MicroNumber((if (property.isDefined && property.get.isMeta) NumberData(MetaProperty(property.get.getProtoBytes).getData.getProtoBytes) else NumberData(0)).value)
  }
}

private[blockchain] object Classifications {

  class ClassificationTable(tag: Tag) extends Table[Classification](tag, "Classification") with ModelTable[Array[Byte]] {

    def * = (id, idString, immutables, mutables) <> (Classification.tupled, Classification.unapply)

    def id = column[Array[Byte]]("id", O.PrimaryKey)

    def idString = column[String]("idString")

    def immutables = column[Array[Byte]]("immutables")

    def mutables = column[Array[Byte]]("mutables")

  }

}

@Singleton
class Classifications @Inject()(
                                 @NamedDatabase("explorer")
                                 protected val dbConfigProvider: DatabaseConfigProvider,
                               )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Classifications.ClassificationTable, Classification, Array[Byte]]() {

  implicit val module: String = constants.Module.BLOCKCHAIN_CLASSIFICATION

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new Classifications.ClassificationTable(tag))

  object Service {

    def get(id: String): Future[Option[Classification]] = getById(utilities.Secrets.base64URLDecode(id))

    def get(id: Array[Byte]): Future[Option[Classification]] = getById(id)

    def tryGet(id: String): Future[Classification] = tryGetById(utilities.Secrets.base64URLDecode(id))

    def tryGet(id: Array[Byte]): Future[Classification] = tryGetById(id)

    def fetchAll: Future[Seq[Classification]] = getAll

    def getIDsAlreadyExists(ids: Seq[String]): Future[Seq[String]] = filter(_.idString.inSet(ids)).map(_.map(_.idString))

  }
}