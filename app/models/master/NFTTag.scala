package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


case class NFTTag(hashTag: String, fileName: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] with Logging {

  def getFileHash: String = utilities.FileOperations.getFileNameWithoutExtension(fileName)

  def id1: String = hashTag

  def id2: String = fileName
}

object NFTTags {

  implicit val module: String = constants.Module.MASTER_NFT_TAG

  implicit val logger: Logger = Logger(this.getClass)

  class NFTTagTable(tag: Tag) extends Table[NFTTag](tag, "NFTTag") with ModelTable2[String, String] {

    def * = (hashTag, fileName, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTTag.tupled, NFTTag.unapply)

    def hashTag = column[String]("hashTag", O.PrimaryKey)

    def fileName = column[String]("fileName", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = hashTag

    def id2 = fileName
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

    def add(hashTag: String, fileName: String): Future[Unit] = create(NFTTag(hashTag = hashTag, fileName = fileName))

    def getByHashTag(hashTag: String): Future[Seq[String]] = filter(_.hashTag === hashTag).map(_.map(_.fileName))

    def getByHashTags(hashTags: Seq[String]): Future[Seq[String]] = filter(_.hashTag.inSet(hashTags)).map(_.map(_.fileName))

    def getHashTagsForNFT(fileName: String): Future[Seq[String]] = filter(_.hashTag === fileName).map(_.map(_.hashTag))

    def deleteHashTag(hashTag: String, fileName: String): Future[Int] = delete(id1 = hashTag, id2 = fileName)

  }
}