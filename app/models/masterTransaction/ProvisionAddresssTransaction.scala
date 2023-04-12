package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.ProvisionAddress
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

case class ProvisionAddressTransaction(txHash: String, accountId: String, toAddress: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

object ProvisionAddressTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_PROVISION_ADDRESS_TRANSACTION

  class ProvisionAddressTransactionTable(tag: Tag) extends Table[ProvisionAddressTransaction](tag, "ProvisionAddressTransaction") with ModelTable[String] {

    def * = (txHash, accountId, toAddress, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (ProvisionAddressTransaction.tupled, ProvisionAddressTransaction.unapply)

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

  val TableQuery = new TableQuery(tag => new ProvisionAddressTransactionTable(tag))

}

@Singleton
class ProvisionAddressTransactions @Inject()(
                                              protected val databaseConfigProvider: DatabaseConfigProvider,
                                              blockchainIdentities: blockchain.Identities,
                                              broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                              utilitiesOperations: utilities.Operations,
                                              masterKeys: master.Keys,
                                              getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                              getAccount: queries.blockchain.GetAccount,
                                              getAbciInfo: queries.blockchain.GetABCIInfo,
                                              utilitiesNotification: utilities.Notification,
                                              blockchainTransactionProvisionAddresses: blockchainTransaction.ProvisionAddresses,
                                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[ProvisionAddressTransactions.ProvisionAddressTransactionTable, ProvisionAddressTransaction, String](
    databaseConfigProvider,
    ProvisionAddressTransactions.TableQuery,
    executionContext,
    ProvisionAddressTransactions.module,
    ProvisionAddressTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, accountId: String, toAddress: String): Future[String] = create(ProvisionAddressTransaction(txHash = txHash, accountId = accountId, toAddress = toAddress, status = None))

    def getByTxHash(txHash: String): Future[Seq[ProvisionAddressTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(ProvisionAddressTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(ProvisionAddressTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[ProvisionAddressTransaction]] = filter(_.status.?.isEmpty)

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
          messages = Seq(utilities.BlockchainTransaction.getProvisionMsg(
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
              provisionAddress <- blockchainTransactionProvisionAddresses.Service.add(txHash = txHash, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, accountId = accountId, toAddress = toAddress)
            } yield provisionAddress
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          provisionAddress <- checkAndAdd(unconfirmedTxHashes)
        } yield (provisionAddress, txRawBytes)
      }

      def broadcastTxAndUpdate(provisionAddress: ProvisionAddress, txRawBytes: Array[Byte]) = {
        val broadcastTx = broadcastTxSync.Service.get(provisionAddress.getTxRawAsHexString(txRawBytes))

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionProvisionAddresses.Service.markFailedWithLog(txHashes = Seq(provisionAddress.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionProvisionAddresses.Service.markFailedWithLog(txHashes = Seq(provisionAddress.txHash), log = successResponse.get.result.log)
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
        (provisionAddress, txRawBytes) <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(provisionAddress, txRawBytes)
      } yield provisionAddress
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = ProvisionAddressTransactions.module

      def runner(): Unit = {
        val provisionAddressTxs = Service.getAllPendingStatus

        def checkAndUpdate(provisionAddressTxs: Seq[ProvisionAddressTransaction]) = utilitiesOperations.traverse(provisionAddressTxs) { provisionAddressTx =>
          val transaction = blockchainTransactionProvisionAddresses.Service.tryGet(provisionAddressTx.txHash)

          def onTxComplete(transaction: ProvisionAddress) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(provisionAddressTx.txHash)
              val updateMaster = masterKeys.Service.markAddressProvisioned(accountId = provisionAddressTx.accountId, address = provisionAddressTx.toAddress)
              val sendNotification = utilitiesNotification.send(accountID = provisionAddressTx.accountId, notification = constants.Notification.ADDRESS_PROVISIONED_SUCCESSFULLY, provisionAddressTx.toAddress)("")

              (for {
                _ <- markSuccess
                _ <- updateMaster
                _ <- sendNotification
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(provisionAddressTx.txHash)
              val updateMaster = masterKeys.Service.markAddressProvisionFailed(accountId = provisionAddressTx.accountId, address = provisionAddressTx.toAddress)
              val sendNotification = utilitiesNotification.send(accountID = provisionAddressTx.accountId, notification = constants.Notification.ADDRESS_PROVISIONED_FAILED, provisionAddressTx.toAddress)("")

              (for {
                _ <- markMasterFailed
                _ <- updateMaster
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
          provisionAddressTxs <- provisionAddressTxs
          _ <- checkAndUpdate(provisionAddressTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}