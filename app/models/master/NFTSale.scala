package models.master

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


case class NFTSale(fileName: String, price: BigDecimal, denom: String, creatorFee: BigDecimal, public: Boolean, marketplace: Boolean, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] with Logging {

  def getFileHash: String = utilities.FileOperations.getFileNameWithoutExtension(fileName)

  def id: String = fileName
}

object NFTSales {

  implicit val module: String = constants.Module.MASTER_NFT_SALE

  implicit val logger: Logger = Logger(this.getClass)

  class NFTSaleTable(tag: Tag) extends Table[NFTSale](tag, "NFTSale") with ModelTable[String] {

    def * = (fileName, price, denom, creatorFee, public, marketplace, startTimeEpoch, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTSale.tupled, NFTSale.unapply)

    def fileName = column[String]("fileName", O.PrimaryKey)

    def price = column[BigDecimal]("price")

    def denom = column[String]("denom")

    def creatorFee = column[BigDecimal]("creatorFee")

    def public = column[Boolean]("public")

    def marketplace = column[Boolean]("marketplace")

    def startTimeEpoch = column[Long]("startTimeEpoch")

    def endTimeEpoch = column[Long]("endTimeEpoch")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = fileName

  }

  val TableQuery = new TableQuery(tag => new NFTSaleTable(tag))
}

@Singleton
class NFTSales @Inject()(
                          protected val databaseConfigProvider: DatabaseConfigProvider
                        )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTSales.NFTSaleTable, NFTSale, String](
    databaseConfigProvider,
    NFTSales.TableQuery,
    executionContext,
    NFTSales.module,
    NFTSales.logger
  ) {

  object Service {

    def add(nftSale: NFTSale): Future[String] = create(nftSale)

    def add(nftSales: Seq[NFTSale]): Future[Unit] = create(nftSales)

    def get(fileName: String): Future[Option[NFTSale]] = getById(fileName)

    def tryGet(fileName: String): Future[NFTSale] = tryGetById(fileName)

    def delete(fileName: String): Future[Int] = deleteById(fileName)

  }
}