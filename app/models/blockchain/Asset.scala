package models.blockchain

import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.data.base.{DecData, HeightData}
import schema.document.Document
import schema.id.base._
import schema.property.Property
import schema.property.base.MetaProperty
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Asset(id: Array[Byte], idString: String, classificationID: Array[Byte], immutables: Array[Byte], mutables: Array[Byte], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[Array[Byte]] {

  def getIDString: String = utilities.Secrets.base64URLEncoder(this.id)

  def getID: AssetID = AssetID(HashID(this.id))

  def getClassificationIDString: String = utilities.Secrets.base64URLEncoder(this.classificationID)

  def getClassificationID: ClassificationID = ClassificationID(this.classificationID)

  def getImmutables: Immutables = Immutables(this.immutables)

  def getMutables: Mutables = Mutables(this.mutables)

  def getDocument: Document = Document(this.getClassificationID, this.getImmutables, this.getMutables)

  def getProperty(id: PropertyID): Option[Property] = this.getDocument.getProperty(id)

  def getSupply: DecData = {
    val supply = this.getProperty(constants.Blockchain.SupplyProperty.getID)
    DecData((if (supply.isDefined) MetaProperty(supply.get.getProtoBytes) else constants.Blockchain.SupplyProperty).getData.getProtoBytes)
  }

  def getBurnHeight: HeightData = {
    val burnHeight = this.getProperty(constants.Blockchain.BurnHeightProperty.getID)
    HeightData((if (burnHeight.isDefined) MetaProperty(burnHeight.get.getProtoBytes) else constants.Blockchain.BurnHeightProperty).getData.getProtoBytes)
  }

  def getLockHeight: HeightData = {
    val lock = this.getProperty(constants.Blockchain.LockProperty.getID)
    HeightData((if (lock.isDefined) MetaProperty(lock.get.getProtoBytes) else constants.Blockchain.LockProperty).getData.getProtoBytes)
  }

  def mutate(properties: Seq[Property]): Asset = this.copy(mutables = this.getMutables.mutate(properties).getProtoBytes)
}

object Assets {

  implicit val module: String = constants.Module.BLOCKCHAIN_ASSET

  implicit val logger: Logger = Logger(this.getClass)

  class DataTable(tag: Tag) extends Table[Asset](tag, "Asset") with ModelTable[Array[Byte]] {

    def * = (id, idString, classificationID, immutables, mutables, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (Asset.tupled, Asset.unapply)

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
class Assets @Inject()(
                        @NamedDatabase("explorer")
                        protected val databaseConfigProvider: DatabaseConfigProvider
                      )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Assets.DataTable, Asset, Array[Byte]](
    databaseConfigProvider,
    Assets.TableQuery,
    executionContext,
    Assets.module,
    Assets.logger
  ) {

  object Service {

    def get(id: String): Future[Option[Asset]] = getById(utilities.Secrets.base64URLDecode(id))

    def get(id: AssetID): Future[Option[Asset]] = getById(id.getBytes)

    def get(id: Array[Byte]): Future[Option[Asset]] = getById(id)

    def getIDsAlreadyExists(ids: Seq[String]): Future[Seq[String]] = filter(_.idString.inSet(ids)).map(_.map(_.idString))

    def tryGet(id: AssetID): Future[Asset] = tryGetById(id.getBytes)

    def tryGet(id: String): Future[Asset] = tryGetById(utilities.Secrets.base64URLDecode(id))


  }

}