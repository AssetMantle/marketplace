package models.masterTransaction

import models.blockchain
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class ExternalAsset(nftId: String, lastOwnerId: String, assetId: String, currentOwnerIdentityId: String, collectionId: String, amount: BigInt, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize: ExternalAssets.ExternalAssetSerialized = ExternalAssets.ExternalAssetSerialized(
    nftId = this.nftId,
    lastOwnerId = this.lastOwnerId,
    assetId = this.assetId,
    currentOwnerIdentityId = this.currentOwnerIdentityId,
    collectionId = this.collectionId,
    amount = BigDecimal(this.amount),
    createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )

}

object ExternalAssets {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_EXTERNAL_ASSET

  implicit val logger: Logger = Logger(this.getClass)

  case class ExternalAssetSerialized(nftId: String, lastOwnerId: String, assetId: String, currentOwnerIdentityId: String, collectionId: String, amount: BigDecimal, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] {

    def id1: String = nftId

    def id2: String = lastOwnerId

    def deserialze: ExternalAsset = ExternalAsset(
      nftId = this.nftId,
      lastOwnerId = this.lastOwnerId,
      assetId = this.assetId,
      currentOwnerIdentityId = this.currentOwnerIdentityId,
      collectionId = this.collectionId,
      amount = this.amount.toBigInt,
      createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  class ExternalAssetTable(tag: Tag) extends Table[ExternalAssetSerialized](tag, "ExternalAsset") with ModelTable2[String, String] {

    def * = (nftId, lastOwnerId, assetId, currentOwnerIdentityId, collectionId, amount, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (ExternalAssetSerialized.tupled, ExternalAssetSerialized.unapply)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def lastOwnerId = column[String]("lastOwnerId", O.PrimaryKey)

    def assetId = column[String]("assetId")

    def currentOwnerIdentityId = column[String]("currentOwnerIdentityId")

    def collectionId = column[String]("collectionId")

    def amount = column[BigDecimal]("amount")

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
  extends GenericDaoImpl2[ExternalAssets.ExternalAssetTable, ExternalAssets.ExternalAssetSerialized, String, String](
    databaseConfigProvider,
    ExternalAssets.TableQuery,
    executionContext,
    ExternalAssets.module,
    ExternalAssets.logger
  ) {


  object Service {

    def insertOrUpdate(nftId: String, lastOwnerId: String, assetId: String, currentOwnerIdentityId: String, collectionId: String, amount: BigInt): Future[Unit] = {
      upsert(ExternalAsset(nftId = nftId, lastOwnerId = lastOwnerId, assetId = assetId, currentOwnerIdentityId = currentOwnerIdentityId, collectionId = collectionId, amount = amount).serialize)
    }

    def delete(nftId: String, lastOwnerId: String): Future[Int] = deleteById1AndId2(id1 = nftId, id2 = lastOwnerId)

  }
}