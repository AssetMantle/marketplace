package models.master

import models.Trait.{Entity3, GenericDaoImpl3, Logging, ModelTable3}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


case class NFTProperty(nftId: String, name: String, `type`: String, `value`: String, meta: Boolean, mutable: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity3[String, String, String] with Logging {
  def id1: String = nftId

  def id2: String = name

  def id3: String = `type`

  def asProperty: constants.NFT.Property = constants.NFT.Property(name = this.name, `type` = this.`type`, `value` = this.`value`, meta = this.meta, mutable = this.mutable)

}

object NFTProperties {

  implicit val module: String = constants.Module.MASTER_NFT_PROPERTY

  implicit val logger: Logger = Logger(this.getClass)

  class NFTPropertyTable(tag: Tag) extends Table[NFTProperty](tag, "NFTProperty") with ModelTable3[String, String, String] {

    def * = (nftId, name, `type`, `value`, meta, mutable, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTProperty.tupled, NFTProperty.unapply)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def name = column[String]("name", O.PrimaryKey)

    def `type` = column[String]("type", O.PrimaryKey)

    def `value` = column[String]("value")

    def meta = column[Boolean]("meta")

    def mutable = column[Boolean]("mutable")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = nftId

    def id2 = `name`

    def id3 = `type`
  }

  val TableQuery = new TableQuery(tag => new NFTPropertyTable(tag))
}

@Singleton
class NFTProperties @Inject()(
                               protected val databaseConfigProvider: DatabaseConfigProvider
                             )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl3[NFTProperties.NFTPropertyTable, NFTProperty, String, String, String](
    databaseConfigProvider,
    NFTProperties.TableQuery,
    executionContext,
    NFTProperties.module,
    NFTProperties.logger
  ) {

  object Service {

    def addMultiple(properties: Seq[NFTProperty]): Future[Unit] = create(properties)

    def add(property: NFTProperty): Future[Unit] = create(property)

    def getForNFT(nftId: String): Future[Seq[NFTProperty]] = filter(_.nftId === nftId)

    def deleteByNFTIds(nftIDs: Seq[String]): Future[Int] = filterAndDelete(_.nftId.inSet(nftIDs))

    def tryGet(nftId: String, name: String): Future[NFTProperty] = tryGetById1Id2Id3(id1 = nftId, id2 = name, id3 = constants.NFT.Data.STRING)

    def deleteProperty(nftId: String, name: String): Future[Int] = delete(id1 = nftId, id2 = name, id3 = constants.NFT.Data.STRING)

  }
}