package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


case class NFTTag(tagName: String, nftId: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] with Logging {
  def id1: String = tagName

  def id2: String = nftId
}

object NFTTags {

  implicit val module: String = constants.Module.MASTER_NFT_TAG

  implicit val logger: Logger = Logger(this.getClass)

  class NFTTagTable(tag: Tag) extends Table[NFTTag](tag, "NFTTag") with ModelTable2[String, String] {

    def * = (tagName, nftId, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTTag.tupled, NFTTag.unapply)

    def tagName = column[String]("tagName", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = tagName

    def id2 = nftId
  }

  val TableQuery = new TableQuery(tag => new NFTTagTable(tag))
}

@Singleton
class NFTTags @Inject()(
                         protected val databaseConfigProvider: DatabaseConfigProvider
                       )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[NFTTags.NFTTagTable, NFTTag, String, String](
    databaseConfigProvider,
    NFTTags.TableQuery,
    executionContext,
    NFTTags.module,
    NFTTags.logger
  ) {

  object Service {

    def add(tagName: String, nftId: String): Future[Unit] = create(NFTTag(tagName = tagName, nftId = nftId))

    def add(nftTags: Seq[NFTTag]): Future[Unit] = create(nftTags)

    def getByTagName(tagName: String): Future[Seq[String]] = filter(_.tagName === tagName).map(_.map(_.nftId))

    def getByTagNames(tagNames: Seq[String]): Future[Seq[String]] = filter(_.tagName.inSet(tagNames)).map(_.map(_.nftId))

    def getTagNamesForNFT(nftId: String): Future[Seq[String]] = filter(_.tagName === nftId).map(_.map(_.tagName))

  }
}