package models.masterTransaction

import models.blockchain
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class ExternalAsset(nftId: String, lastOwnerId: String, assetId: String, lastOwnerIdentityId: String, currentOwnerIdentityId: String, collectionId: String, amount: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {

  def id1: String = nftId

  def id2: String = lastOwnerId

}

object ExternalAssets {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_EXTERNAL_ASSET

  implicit val logger: Logger = Logger(this.getClass)

  class ExternalAssetTable(tag: Tag) extends Table[ExternalAsset](tag, "ExternalAsset") with ModelTable2[String, String] {

    def * = (nftId, lastOwnerId, collectionId, assetId, lastOwnerIdentityId, currentOwnerIdentityId, collectionId, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (ExternalAsset.tupled, ExternalAsset.unapply)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def lastOwnerId = column[String]("lastOwnerId", O.PrimaryKey)

    def assetId = column[String]("assetId")

    def lastOwnerIdentityId = column[String]("lastOwnerIdentityId")

    def currentOwnerIdentityId = column[String]("currentOwnerIdentityId")

    def collectionId = column[String]("collectionId")

    def amount = column[Long]("amount")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = nftId

    def id2 = lastOwnerId

  }

  lazy val TableQuery = new TableQuery(tag => new ExternalAssetTable(tag))
}

@Singleton
class ExternalAssets @Inject()(
                                blockchainAssets: blockchain.Assets,
                                blockchainSplits: blockchain.Splits,
                                protected val databaseConfigProvider: DatabaseConfigProvider
                              )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[ExternalAssets.ExternalAssetTable, ExternalAsset, String, String](
    databaseConfigProvider,
    ExternalAssets.TableQuery,
    executionContext,
    ExternalAssets.module,
    ExternalAssets.logger
  ) {


  object Service {

    def insertOrUpdate(nftId: String, lastOwnerId: String, assetId: String, lastOwnerIdentityId: String, currentOwnerIdentityId: String, collectionId: String, amount: Long): Future[Unit] = {
      upsert(ExternalAsset(nftId = nftId, lastOwnerId = lastOwnerId, assetId = assetId, lastOwnerIdentityId = lastOwnerIdentityId, currentOwnerIdentityId = currentOwnerIdentityId, collectionId = collectionId, amount = amount))
    }

    def delete(nftId: String, lastOwnerId: String): Future[Int] = deleteById1AndId2(id1 = nftId, id2 = lastOwnerId)

  }

  object Utility {


  }
}