package models.masterTransaction

import models.blockchain
import models.masterTransaction.ExternalAssets.ExternalAssetTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class ExternalAsset(assetId: String, currentOwnerIdentityId: String, nftId: String, lastOwnerId: String, collectionId: String, amount: BigInt, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize: ExternalAssets.ExternalAssetSerialized = ExternalAssets.ExternalAssetSerialized(
    assetId = this.assetId,
    currentOwnerIdentityId = this.currentOwnerIdentityId,
    nftId = this.nftId,
    lastOwnerId = this.lastOwnerId,
    collectionId = this.collectionId,
    amount = BigDecimal(this.amount),
    createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )

}

private[masterTransaction] object ExternalAssets {

  case class ExternalAssetSerialized(assetId: String, currentOwnerIdentityId: String, nftId: String, lastOwnerId: String, collectionId: String, amount: BigDecimal, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] {

    def id1: String = assetId

    def id2: String = currentOwnerIdentityId

    def deserialize()(implicit module: String, logger: Logger): ExternalAsset = ExternalAsset(
      assetId = this.assetId,
      currentOwnerIdentityId = this.currentOwnerIdentityId,
      nftId = this.nftId,
      lastOwnerId = this.lastOwnerId,
      collectionId = this.collectionId,
      amount = this.amount.toBigInt,
      createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  class ExternalAssetTable(tag: Tag) extends Table[ExternalAssetSerialized](tag, "ExternalAsset") with ModelTable2[String, String] {

    def * = (assetId, currentOwnerIdentityId, nftId, lastOwnerId, collectionId, amount, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (ExternalAssetSerialized.tupled, ExternalAssetSerialized.unapply)

    def assetId = column[String]("assetId", O.PrimaryKey)

    def currentOwnerIdentityId = column[String]("currentOwnerIdentityId", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def lastOwnerId = column[String]("lastOwnerId")

    def collectionId = column[String]("collectionId")

    def amount = column[BigDecimal]("amount")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = assetId

    def id2 = currentOwnerIdentityId

  }
}

@Singleton
class ExternalAssets @Inject()(
                                blockchainAssets: blockchain.Assets,
                                blockchainSplits: blockchain.Splits,
                                protected val dbConfigProvider: DatabaseConfigProvider,
                              )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[ExternalAssets.ExternalAssetTable, ExternalAssets.ExternalAssetSerialized, String, String]() {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_EXTERNAL_ASSET

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new ExternalAssetTable(tag))

  object Service {

    def addOrUpdate(assetId: String, currentOwnerIdentityId: String, nftId: String, lastOwnerId: String, collectionId: String, amount: BigInt): Future[Unit] = {
      val externalAsset = getById(id1 = assetId, id2 = currentOwnerIdentityId)

      def checkAndUpdate(externalAsset: Option[ExternalAsset]) = if (externalAsset.isDefined) {
        updateById1AndId2(externalAsset.get.copy(amount = externalAsset.get.amount + amount).serialize)
      } else {
        upsert(ExternalAsset(assetId = assetId, currentOwnerIdentityId = currentOwnerIdentityId, nftId = nftId, lastOwnerId = lastOwnerId, collectionId = collectionId, amount = amount).serialize)
      }

      for {
        externalAsset <- externalAsset
        _ <- checkAndUpdate(externalAsset.map(_.deserialize))
      } yield ()
    }

    def delete(assetId: String, currentOwnerIdentityId: String, amount: BigInt): Future[Unit] = {
      val externalAsset = getById(id1 = assetId, id2 = currentOwnerIdentityId)

      def checkAndUpdate(externalAsset: Option[ExternalAsset]) = if (externalAsset.isDefined) {
        if (externalAsset.get.amount == amount) deleteById1AndId2(id1 = assetId, id2 = currentOwnerIdentityId)
        else updateById1AndId2(externalAsset.get.copy(amount = externalAsset.get.amount - amount).serialize)
      } else Future(0)

      for {
        externalAsset <- externalAsset
        _ <- checkAndUpdate(externalAsset.map(_.deserialize))
      } yield ()
    }

  }
}