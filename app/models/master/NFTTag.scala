package models.master

import models.master.NFTTags.NFTTagTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


case class NFTTag(tagName: String, nftId: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] with Logging {
  def id1: String = tagName

  def id2: String = nftId
}

private[master] object NFTTags {

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
}

@Singleton
class NFTTags @Inject()(
                         protected val dbConfigProvider: DatabaseConfigProvider,
                       )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[NFTTags.NFTTagTable, NFTTag, String, String]() {

  implicit val module: String = constants.Module.MASTER_NFT_TAG

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new NFTTagTable(tag))

  object Service {

    def add(tagName: String, nftId: String): Future[String] = create(NFTTag(tagName = tagName, nftId = nftId)).map(_.nftId)

    def add(nftTags: Seq[NFTTag]): Future[Int] = create(nftTags)

    def getByTagName(tagName: String): Future[Seq[String]] = filter(_.tagName === tagName).map(_.map(_.nftId))

    def getByTagNames(tagNames: Seq[String]): Future[Seq[String]] = filter(_.tagName.inSet(tagNames)).map(_.map(_.nftId))

    def getTagNamesForNFT(nftId: String): Future[Seq[String]] = filter(_.tagName === nftId).map(_.map(_.tagName))

    def deleteByNFTIds(nftIDs: Seq[String]): Future[Int] = filterAndDelete(_.nftId.inSet(nftIDs))

    def deleteByNFTId(nftID: String): Future[Int] = filterAndDelete(_.nftId === nftID)

    def deleteByTagName(tagName: String): Future[Int] = filterAndDelete(_.tagName === tagName)

  }
}