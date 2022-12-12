package models.history

import models.Trait._
import models.{blockchain, master}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class BTBuyAssetWithoutMint(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: MicroNumber, nftId: String, saleId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends HistoryLogging with BlockchainTransaction {

  def serialize(): BTBuyAssetWithoutMints.BTBuyAssetWithoutMintSerialized = BTBuyAssetWithoutMints.BTBuyAssetWithoutMintSerialized(sellerAccountId = this.sellerAccountId, buyerAccountId = this.buyerAccountId, txHash = this.txHash, txRawBytes = this.txRawBytes, nftId = this.nftId, saleId = this.saleId, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = this.amount.toBigDecimal, broadcasted = this.broadcasted, status = this.status, memo = this.memo, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch, deletedBy = this.deletedBy, deletedOnMillisEpoch = this.deletedOnMillisEpoch)
}

object BTBuyAssetWithoutMints {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.HISTORY_BLOCKCHAIN_TRANSACTION_BUY_ASSET_WITHOUT_MINT

  case class BTBuyAssetWithoutMintSerialized(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: BigDecimal, nftId: String, saleId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long], deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends Entity3[String, String, String] {
    def deserialize: BTBuyAssetWithoutMint = BTBuyAssetWithoutMint(buyerAccountId = this.buyerAccountId, sellerAccountId = this.sellerAccountId, txHash = txHash, txRawBytes = this.txRawBytes, nftId = this.nftId, saleId = this.saleId, fromAddress = fromAddress, toAddress = toAddress, amount = MicroNumber(amount), broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch, deletedBy = this.deletedBy, deletedOnMillisEpoch = this.deletedOnMillisEpoch)

    def id1: String = buyerAccountId

    def id2: String = sellerAccountId

    def id3: String = txHash
  }

  class BTBuyAssetWithoutMintTable(tag: Tag) extends Table[BTBuyAssetWithoutMintSerialized](tag, "BlockchainTransactionBuyAssetWithoutMint") with ModelTable3[String, String, String] {

    def * = (buyerAccountId, sellerAccountId, txHash, txRawBytes, fromAddress, toAddress, amount, nftId, saleId, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?, deletedBy.?, deletedOnMillisEpoch.?) <> (BTBuyAssetWithoutMintSerialized.tupled, BTBuyAssetWithoutMintSerialized.unapply)

    def buyerAccountId = column[String]("buyerAccountId", O.PrimaryKey)

    def sellerAccountId = column[String]("sellerAccountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[BigDecimal]("amount")

    def nftId = column[String]("nftId")

    def saleId = column[String]("saleId")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def deletedBy = column[String]("deletedBy")

    def deletedOnMillisEpoch = column[Long]("deletedOnMillisEpoch")

    def id1 = buyerAccountId

    def id2 = sellerAccountId

    def id3 = txHash
  }

  val TableQuery = new TableQuery(tag => new BTBuyAssetWithoutMintTable(tag))

}

@Singleton
class BTBuyAssetWithoutMints @Inject()(
                                        protected val databaseConfigProvider: DatabaseConfigProvider,
                                        blockchainAccounts: blockchain.Accounts,
                                        blockchainTransactions: blockchain.Transactions,
                                        masterNFTOwners: master.NFTOwners,
                                        masterNFTs: master.NFTs,
                                        broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                        utilitiesOperations: utilities.Operations,
                                        getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                        getAccount: queries.blockchain.GetAccount,
                                        utilitiesTransactionComplete: utilities.TransactionComplete
                                      )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl3[BTBuyAssetWithoutMints.BTBuyAssetWithoutMintTable, BTBuyAssetWithoutMints.BTBuyAssetWithoutMintSerialized, String, String, String](
    databaseConfigProvider,
    BTBuyAssetWithoutMints.TableQuery,
    executionContext,
    BTBuyAssetWithoutMints.module,
    BTBuyAssetWithoutMints.logger
  ) {

  object Service {

    def add(btBuyAssetWithoutMints: Seq[BTBuyAssetWithoutMint]): Future[Unit] = create(btBuyAssetWithoutMints.map(_.serialize()))

  }

}