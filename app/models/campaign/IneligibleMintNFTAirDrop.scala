package models.campaign

import models.campaign.IneligibleMintNFTAirDrops.IneligibleMintNFTAirDropTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class IneligibleMintNFTAirDrop(nftID: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = nftID

}

object IneligibleMintNFTAirDrops {

  class IneligibleMintNFTAirDropTable(tag: Tag) extends Table[IneligibleMintNFTAirDrop](tag, "IneligibleMintNFTAirDrop") with ModelTable[String] {

    def * = (nftID, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (IneligibleMintNFTAirDrop.tupled, IneligibleMintNFTAirDrop.unapply)

    def nftID = column[String]("nftID", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = nftID

  }

}

@Singleton
class IneligibleMintNFTAirDrops @Inject()(
                                           protected val dbConfigProvider: DatabaseConfigProvider,
                                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[IneligibleMintNFTAirDropTable, IneligibleMintNFTAirDrop, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.CAMPAIGN_INELIGIBLE_MINT_NFT_AIRDROP

  val tableQuery = new TableQuery(tag => new IneligibleMintNFTAirDropTable(tag))

  object Service {

    def getIneligibles(nftIDs: Seq[String]): Future[Seq[String]] = filter(_.nftID.inSet(nftIDs)).map(_.map(_.nftID))
  }


}