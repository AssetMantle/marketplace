package utilities

import com.google.protobuf.{Any => protoBufAny}
import constants.Response.Failure
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchain.Account
import models.blockchainTransaction.{AdminTransaction, AdminTransactions, UserTransaction, UserTransactions}
import models.traits.BlockchainTransaction
import org.bitcoinj.core.ECKey
import play.api.Logger
import queries.blockchain.{GetABCIInfo, GetAccount, GetUnconfirmedTxs}
import queries.responses.blockchain.UnconfirmedTxsResponse
import transactions.blockchain.BroadcastTxSync
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Transaction @Inject()(
                             broadcastTxSync: BroadcastTxSync,
                             getUnconfirmedTxs: GetUnconfirmedTxs,
                             getAccount: GetAccount,
                             getAbciInfo: GetABCIInfo,
                             utilitiesOperations: utilities.Operations,
                             userTransactions: UserTransactions,
                             adminTransactions: AdminTransactions,
                           )(private implicit val executionContext: ExecutionContext) {

  def getLatestHeightAccountAndUnconfirmedTxs(address: String): Future[(Int, Account, UnconfirmedTxsResponse.Response)] = {
    // TODO
    // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
    val abciInfo = getAbciInfo.Service.get
    val bcAccount = getAccount.Service.get(address).map(_.account.toSerializableAccount).recover {
      case _: Exception => models.blockchain.Account(address = address, accountType = None, accountNumber = 0, sequence = 0, publicKey = None)
    }
    val unconfirmedTxs = getUnconfirmedTxs.Service.get()

    for {
      abciInfo <- abciInfo
      bcAccount <- bcAccount
      unconfirmedTxs <- unconfirmedTxs
    } yield (abciInfo.result.response.last_block_height.toInt, bcAccount, unconfirmedTxs)
  }

  private def getTx(latestBlockHeight: Int, messages: Seq[protoBufAny], gasPrice: BigDecimal, gasLimit: Int, bcAccount: models.blockchain.Account, ecKey: ECKey): (Int, Array[Byte], String) = {
    val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
    val finalGasLimit = gasLimit * messages.length
    val (txRawBytes, _) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
      messages = messages,
      fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = finalGasLimit),
      gasLimit = finalGasLimit,
      account = bcAccount,
      ecKey = ecKey,
      timeoutHeight = timeoutHeight)
    val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)
    (timeoutHeight, txRawBytes, txHash)
  }

  def doUserTx(messages: Seq[protoBufAny], gasPrice: BigDecimal, accountId: String, fromAddress: String, ecKey: ECKey, masterTxFunction: String => Future[String])(implicit module: String, logger: Logger, txUtil: TxUtil): Future[(UserTransaction, String)] = {
    val latestHeightAccountUnconfirmedTxs = getLatestHeightAccountAndUnconfirmedTxs(fromAddress)

    def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
      val (timeoutHeight, txRawBytes, txHash) = getTx(latestBlockHeight = latestBlockHeight, messages = messages, gasPrice = gasPrice, gasLimit = txUtil.gasLimit, bcAccount = bcAccount, ecKey = ecKey)

      if (!unconfirmedTxHashes.contains(txHash)) {
        val userTx = userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = accountId, fromAddress = fromAddress, timeoutHeight = timeoutHeight, txType = txUtil.txType)

        def addToMasterTransaction(txHash: String) = masterTxFunction(txHash)

        //        def broadcastTxAndUpdate(userTransaction: UserTransaction, txRawBytes: Array[Byte]) = userTransactions.Utility.broadcastTxAndUpdate(userTransaction, txRawBytes)
        def doBroadcastTxAndUpdate(userTransaction: UserTransaction, txRawBytes: Array[Byte]) = broadcastTxAndUpdate(userTransaction, txRawBytes, userTransactions.Service.markFailedWithLog).map(_.asInstanceOf[UserTransaction])

        for {
          userTx <- userTx
          masterTxValue <- addToMasterTransaction(txHash)
          updatedUserTransaction <- doBroadcastTxAndUpdate(userTx, txRawBytes)
        } yield (updatedUserTransaction, masterTxValue)
      } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
    }

    (for {
      (latestHeight, bcAccount, unconfirmedTxs) <- latestHeightAccountUnconfirmedTxs
      (updatedUserTransaction, masterTxValue) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
    } yield (updatedUserTransaction, masterTxValue)
      ).recover {
      case baseException: BaseException => baseException.notifyLog()
        throw baseException
      case exception: Exception => logger.error(exception.getLocalizedMessage)
        val failure = Failure(exception.getLocalizedMessage)
        failure.getBaseException(exception).notifyLog()
        failure.throwBaseException(exception)
    }
  }

  def doAdminTx(messages: Seq[protoBufAny], wallet: Wallet, masterTxFunction: String => Future[String])(implicit module: String, logger: Logger, txUtil: TxUtil): Future[(AdminTransaction, String)] = {
    val latestHeightAccountUnconfirmedTxs = getLatestHeightAccountAndUnconfirmedTxs(wallet.address)

    def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
      val (timeoutHeight, txRawBytes, txHash) = getTx(latestBlockHeight = latestBlockHeight, messages = messages, gasPrice = constants.Transaction.AdminTxGasPrice, gasLimit = txUtil.gasLimit, bcAccount = bcAccount, ecKey = wallet.getECKey)

      if (!unconfirmedTxHashes.contains(txHash)) {
        val adminTx = adminTransactions.Service.addWithNoneStatus(txHash = txHash, fromAddress = wallet.address, timeoutHeight = timeoutHeight, txType = txUtil.txType)

        def addToMasterTransaction(txHash: String) = masterTxFunction(txHash)

        //        def broadcastTxAndUpdate(adminTx: AdminTransaction, txRawBytes: Array[Byte]) = adminTransactions.Utility.broadcastTxAndUpdate(adminTx, txRawBytes)

        def doBroadcastTxAndUpdate(adminTx: AdminTransaction, txRawBytes: Array[Byte]) = broadcastTxAndUpdate(adminTx, txRawBytes, adminTransactions.Service.markFailedWithLog).map(_.asInstanceOf[AdminTransaction])

        for {
          adminTx <- adminTx
          masterTxValue <- addToMasterTransaction(txHash)
          updatedUserTransaction <- doBroadcastTxAndUpdate(adminTx, txRawBytes)
        } yield (updatedUserTransaction, masterTxValue)
      } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
    }

    (for {
      (latestHeight, bcAccount, unconfirmedTxs) <- latestHeightAccountUnconfirmedTxs
      (updatedAdminTransaction, masterTxValue) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
    } yield (updatedAdminTransaction, masterTxValue)
      ).recover {
      case baseException: BaseException => baseException.notifyLog()
        throw baseException
      case exception: Exception => logger.error(exception.getLocalizedMessage)
        val failure = Failure(exception.getLocalizedMessage)
        failure.getBaseException(exception).notifyLog()
        failure.throwBaseException(exception)
    }
  }

  def broadcastTxAndUpdate[T <: BlockchainTransaction[T]](bcTx: T, txRawBytes: Array[Byte], markFailedWithLog: (String, String) => Future[Int]): Future[T] = {

    val broadcastTx = broadcastTxSync.Service.get(bcTx.getTxRawAsHexString(txRawBytes))

    def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = {
      val log = if (errorResponse.nonEmpty) Option(errorResponse.get.error.data)
      else if (successResponse.nonEmpty && successResponse.get.result.code != 0) Option(successResponse.get.result.log)
      else None

      val updateTx = if (log.nonEmpty) markFailedWithLog(bcTx.txHash, log.get) else Future(0)
      for {
        _ <- updateTx
      } yield bcTx.withUpdatedLog(log)
    }

    for {
      (successResponse, errorResponse) <- broadcastTx
      updatedUserTransaction <- update(successResponse, errorResponse)
    } yield updatedUserTransaction
  }

}
