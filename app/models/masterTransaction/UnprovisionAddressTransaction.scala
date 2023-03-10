package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.UnprovisionAddress
import models.traits._
import models.{blockchain, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class UnprovisionAddressTransaction(txHash: String, accountId: String, toAddress: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

object UnprovisionAddressTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_UNPROVISION_ADDRESS_TRANSACTION


  class UnprovisionAddressTransactionTable(tag: Tag) extends Table[UnprovisionAddressTransaction](tag, "UnprovisionAddressTransaction") with ModelTable[String] {

    def * = (txHash, accountId, toAddress, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (UnprovisionAddressTransaction.tupled, UnprovisionAddressTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def toAddress = column[String]("toAddress")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new UnprovisionAddressTransactionTable(tag))

}

@Singleton
class UnprovisionAddressTransactions @Inject()(
                                                protected val databaseConfigProvider: DatabaseConfigProvider,
                                                blockchainAccounts: blockchain.Accounts,
                                                blockchainBlocks: blockchain.Blocks,
                                                blockchainTransactions: blockchain.Transactions,
                                                blockchainTransactionUnprovisionAddresses: blockchainTransaction.UnprovisionAddresses,
                                                broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                                utilitiesOperations: utilities.Operations,
                                                getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                                getAccount: queries.blockchain.GetAccount,
                                                getAbciInfo: queries.blockchain.GetABCIInfo,
                                                masterKeys: master.Keys,
                                                utilitiesNotification: utilities.Notification,
                                              )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[UnprovisionAddressTransactions.UnprovisionAddressTransactionTable, UnprovisionAddressTransaction, String](
    databaseConfigProvider,
    UnprovisionAddressTransactions.TableQuery,
    executionContext,
    UnprovisionAddressTransactions.module,
    UnprovisionAddressTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, accountId: String, toAddress: String): Future[String] = create(UnprovisionAddressTransaction(txHash = txHash, accountId = accountId, toAddress = toAddress, status = None))

    def getByTxHash(txHash: String): Future[Seq[UnprovisionAddressTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(UnprovisionAddressTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(UnprovisionAddressTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[UnprovisionAddressTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)

    def getWithStatusTrueOrPending(accountIds: Seq[String]): Future[Seq[String]] = filter(x => x.accountId.inSet(accountIds) && (x.status || x.status.?.isEmpty)).map(_.map(_.accountId))

  }

  object Utility {

    def transaction(fromAddress: String, accountId: String, toAddress: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount).recover {
        case exception: Exception => models.blockchain.Account(address = fromAddress, accountType = None, accountNumber = 0, sequence = 0, publicKey = None)
      }
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getUnprovisionMsg(
            fromAddress = fromAddress,
            fromID = utilities.Identity.getMantlePlaceIdentityID(accountId),
            toAddress = toAddress
          )),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultProvisionGasLimit),
          gasLimit = constants.Blockchain.DefaultProvisionGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              unprovisionAddress <- blockchainTransactionUnprovisionAddresses.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, accountId = accountId, toAddress = toAddress)
            } yield unprovisionAddress
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          unprovisionAddress <- checkAndAdd(unconfirmedTxHashes)
        } yield unprovisionAddress
      }

      def broadcastTxAndUpdate(unprovisionAddress: UnprovisionAddress) = {
        val broadcastTx = broadcastTxSync.Service.get(unprovisionAddress.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionUnprovisionAddresses.Service.markFailedWithLog(txHashes = Seq(unprovisionAddress.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionUnprovisionAddresses.Service.markFailedWithLog(txHashes = Seq(unprovisionAddress.txHash), log = successResponse.get.result.log)
        else Future(0)

        for {
          (successResponse, errorResponse) <- broadcastTx
          _ <- update(successResponse, errorResponse)
        } yield ()
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        unprovisionAddress <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(unprovisionAddress)
      } yield unprovisionAddress
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = UnprovisionAddressTransactions.module

      def runner(): Unit = {
        val unprovisionAddressTxs = Service.getAllPendingStatus

        def checkAndUpdate(unprovisionAddressTxs: Seq[UnprovisionAddressTransaction]) = utilitiesOperations.traverse(unprovisionAddressTxs) { unprovisionAddressTx =>
          val transaction = blockchainTransactionUnprovisionAddresses.Service.tryGet(unprovisionAddressTx.txHash)

          def onTxComplete(transaction: UnprovisionAddress) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(unprovisionAddressTx.txHash)
              val deletKey = masterKeys.Service.delete(accountId = unprovisionAddressTx.accountId, address = unprovisionAddressTx.toAddress)
              val sendNotification = utilitiesNotification.send(accountID = unprovisionAddressTx.accountId, notification = constants.Notification.ADDRESS_UNPROVISIONED_SUCCESSFULLY, unprovisionAddressTx.toAddress)("")

              (for {
                _ <- markSuccess
                _ <- deletKey
                _ <- sendNotification
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(unprovisionAddressTx.txHash)
              val sendNotification = utilitiesNotification.send(accountID = unprovisionAddressTx.accountId, notification = constants.Notification.ADDRESS_UNPROVISIONED_FAILED, unprovisionAddressTx.toAddress)("")

              (for {
                _ <- markMasterFailed
                _ <- sendNotification
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()

        }

        val forComplete = (for {
          unprovisionAddressTxs <- unprovisionAddressTxs
          _ <- checkAndUpdate(unprovisionAddressTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}