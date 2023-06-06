package models.blockchain

import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.data.Data
import schema.data.base.ListData
import schema.document.Document
import schema.id.base._
import schema.property.Property
import schema.property.base.MetaProperty
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Identity(id: Array[Byte], idString: String, classificationID: Array[Byte], immutables: Array[Byte], mutables: Array[Byte]) extends Entity[Array[Byte]] {

  def getIDString: String = utilities.Secrets.base64URLEncoder(this.id)

  def getID: IdentityID = IdentityID(HashID(this.id))

  def getClassificationIDString: String = utilities.Secrets.base64URLEncoder(this.classificationID)

  def getClassificationID: ClassificationID = ClassificationID(this.classificationID)

  def getImmutables: Immutables = Immutables(this.immutables)

  def getMutables: Mutables = Mutables(this.mutables)

  def getDocument: Document = Document(this.getClassificationID, this.getImmutables, this.getMutables)

  def getProperty(id: PropertyID): Option[Property] = this.getDocument.getProperty(id)

  def getAuthentication: ListData = {
    val property = this.getProperty(schema.constants.Properties.AuthenticationProperty.getID)
    if (property.isDefined) {
      if (property.get.isMeta) ListData(MetaProperty(property.get.getProtoBytes).getData.toAnyData.getListData)
      else ListData(schema.constants.Properties.AuthenticationProperty.getProtoBytes)
    } else ListData(Seq())
  }

  def getAuthenticationAddress: Seq[String] = this.getAuthentication.getAnyDataList.map(x => Data(x).viewString)

  def mutate(properties: Seq[Property]): Identity = this.copy(mutables = this.getMutables.mutate(properties).getProtoBytes)

}

object Identities {

  implicit val module: String = constants.Module.BLOCKCHAIN_IDENTITY

  implicit val logger: Logger = Logger(this.getClass)

  class DataTable(tag: Tag) extends Table[Identity](tag, "Identity") with ModelTable[Array[Byte]] {

    def * = (id, idString, classificationID, immutables, mutables) <> (Identity.tupled, Identity.unapply)

    def id = column[Array[Byte]]("id", O.PrimaryKey)

    def idString = column[String]("idString")

    def classificationID = column[Array[Byte]]("classificationID")

    def immutables = column[Array[Byte]]("immutables")

    def mutables = column[Array[Byte]]("mutables")
  }

  val TableQuery = new TableQuery(tag => new DataTable(tag))

}

@Singleton
class Identities @Inject()(
                            @NamedDatabase("explorer")
                            protected val databaseConfigProvider: DatabaseConfigProvider
                          )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Identities.DataTable, Identity, Array[Byte]](
    databaseConfigProvider,
    Identities.TableQuery,
    executionContext,
    Identities.module,
    Identities.logger
  ) {

  object Service {

    def get(id: String): Future[Option[Identity]] = getById(utilities.Secrets.base64URLDecode(id))

    def get(id: IdentityID): Future[Option[Identity]] = getById(id.getBytes)

    def get(id: Array[Byte]): Future[Option[Identity]] = getById(id)

    def getIDsAlreadyExists(ids: Seq[String]): Future[Seq[String]] = filter(_.idString.inSet(ids)).map(_.map(_.idString))

    def tryGet(id: String): Future[Identity] = tryGetById(utilities.Secrets.base64URLDecode(id))

    def tryGet(id: IdentityID): Future[Identity] = tryGetById(id.getBytes)

  }

}