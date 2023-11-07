package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import exceptions.BaseException
import models.{blockchain, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
import views.setting.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SettingController @Inject()(
                                   messagesControllerComponents: MessagesControllerComponents,
                                   cached: Cached,
                                   blockchainBalances: blockchain.Balances,
                                   withoutLoginActionAsync: WithoutLoginActionAsync,
                                   withoutLoginAction: WithoutLoginAction,
                                   masterAccounts: master.Accounts,
                                   masterKeys: master.Keys,
                                   withUsernameToken: WithUsernameToken,
                                   withLoginActionAsync: WithLoginActionAsync,
                                   withLoginAction: WithLoginAction,
                                   masterTransactionUnprovisionAddressTransactions: masterTransaction.UnprovisionAddressTransactions,
                                   masterTransactionProvisionAddressTransactions: masterTransaction.ProvisionAddressTransactions,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.SETTING_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.SettingController.viewSettings()

  def viewSettings(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginAction { implicit loginState =>
      implicit request =>
        Ok(views.html.setting.viewSettings())
    }
  }

  def settings(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val keys = masterKeys.Service.fetchAllForId(loginState.username)
        (for {
          keys <- keys
        } yield Ok(views.html.setting.settings(keys.filter(_.encryptedPrivateKey.nonEmpty).sortBy(_.name) ++ keys.filter(_.encryptedPrivateKey.isEmpty).sortBy(_.name)))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def addNewKey(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.addNewKey())
  }

  def addManagedKeyForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.addManagedKey())
  }

  def addManagedKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddManagedKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.addManagedKey(formWithErrors)))
        },
        addManagedKeyData => {
          val wallet = utilities.Wallet.getWallet(addManagedKeyData.seeds.split(" "))
          val validatePassword = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = loginState.address, password = addManagedKeyData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)

          def validateAndAdd(validated: Boolean, key: master.Key, balance: MicroNumber) = {
            val errors = Seq(
              if (wallet.address != addManagedKeyData.address) Option(constants.Response.INVALID_SEEDS_OR_ADDRESS) else None,
              if (!validated) Option(constants.Response.INVALID_PASSWORD) else None,
              if (balance == MicroNumber.zero) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
            ).flatten

            if (errors.isEmpty) {
              val addToMaster = masterKeys.Service.addManagedKey(
                accountId = loginState.username,
                address = wallet.address,
                hdPath = wallet.hdPath,
                password = addManagedKeyData.password,
                privateKey = wallet.privateKey,
                partialMnemonics = Option(wallet.mnemonics.take(wallet.mnemonics.length - constants.Blockchain.MnemonicShown)),
                name = addManagedKeyData.keyName,
                retryCounter = 0,
                backupUsed = true,
                active = false,
                verified = Option(true)
              )

              def doTx() = masterTransactionProvisionAddressTransactions.Utility.transaction(
                fromAddress = loginState.address,
                accountId = loginState.username,
                toAddress = addManagedKeyData.address,
                gasPrice = constants.Transaction.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, addManagedKeyData.password))
              )

              for {
                _ <- addToMaster
                _ <- doTx()
              } yield ()
            } else errors.head.throwBaseException()
          }

          (for {
            (validatePassword, key) <- validatePassword
            balance <- balance
            _ <- validateAndAdd(validatePassword, key, balance)
          } yield PartialContent(views.html.setting.keyAddedOrUpdatedSuccessfully(address = wallet.address, name = addManagedKeyData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.addManagedKey(AddManagedKey.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def addUnmanagedKeyForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.addUnmanagedKey())
  }

  def addUnmanagedKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddUnmanagedKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.addUnmanagedKey(formWithErrors)))
        },
        addUnmanagedKeyData => {
          val validatePassword = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = loginState.address, password = addUnmanagedKeyData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)

          def validateAndAdd(validated: Boolean, key: master.Key, balance: MicroNumber) = {
            val errors = Seq(
              if (!validated) Option(constants.Response.INVALID_PASSWORD) else None,
              if (balance == MicroNumber.zero) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
            ).flatten

            if (errors.isEmpty) {
              val addToMaster = masterKeys.Service.addUnmanagedKey(
                accountId = loginState.username,
                address = addUnmanagedKeyData.address,
                password = addUnmanagedKeyData.password,
                name = addUnmanagedKeyData.keyName,
                retryCounter = 0,
                backupUsed = true,
                active = false,
                verified = Option(true)
              )

              def doTx() = masterTransactionProvisionAddressTransactions.Utility.transaction(
                fromAddress = loginState.address,
                accountId = loginState.username,
                toAddress = addUnmanagedKeyData.address,
                gasPrice = constants.Transaction.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, addUnmanagedKeyData.password))
              )

              for {
                _ <- addToMaster
                _ <- doTx()
              } yield ()
            } else errors.head.throwBaseException()
          }

          (for {
            (validatePassword, key) <- validatePassword
            balance <- balance
            _ <- validateAndAdd(validatePassword, key, balance)
          } yield PartialContent(views.html.setting.keyAddedOrUpdatedSuccessfully(address = addUnmanagedKeyData.address, name = addUnmanagedKeyData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.addUnmanagedKey(AddUnmanagedKey.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def changeKeyNameForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.changeKeyName(address = address))
  }

  def changeKeyName: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangeKeyName.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.changeKeyName(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.CHANGE_KEY_ADDRESS.name, ""))))
        },
        changeKeyNameData => {
          val update = masterKeys.Service.updateKeyName(accountId = loginState.username, address = changeKeyNameData.address, keyName = changeKeyNameData.keyName)

          (for {
            _ <- update
          } yield PartialContent(views.html.setting.changeKeyNameSuccessfully(address = changeKeyNameData.address, name = changeKeyNameData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.changeKeyName(ChangeKeyName.form.withGlobalError(baseException.failure.message), changeKeyNameData.address))
          }
        }
      )
  }

  def viewMnemonicsForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.viewMnemonics(address = address))
  }

  def viewMnemonics(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ViewMnemonics.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.viewMnemonics(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
        },
        viewMnemonicsData => {
          val validateAndGetKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = viewMnemonicsData.address, password = viewMnemonicsData.password)

          def update(validated: Boolean, key: master.Key) = if (validated) masterKeys.Service.updateKey(key.copy(backupUsed = true)) else constants.Response.INVALID_PASSWORD.throwBaseException()

          (for {
            (validated, key) <- validateAndGetKey
            _ <- update(validated, key)
          } yield if (validated) PartialContent(views.html.setting.seedPhrase(key.partialMnemonics.getOrElse(constants.Response.SEEDS_NOT_FOUND.throwBaseException()))) else constants.Response.INVALID_PASSWORD.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.viewMnemonics(ViewMnemonics.form.withGlobalError(baseException.failure.message), viewMnemonicsData.address))
          }
        }
      )
  }

  def deleteKeyForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.deleteKey(address = address))
  }

  def deleteKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      DeleteKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.deleteKey(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
        },
        deleteKeyData => {
          val validateAndGetTxKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = loginState.address, password = deleteKeyData.password)
          val deleteKey = masterKeys.Service.tryGet(accountId = loginState.username, address = deleteKeyData.address)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)

          def delete(validated: Boolean, txKey: master.Key, balance: MicroNumber, deleteKey: master.Key) = {
            val errors = Seq(
              if (deleteKey.active) Option(constants.Response.CANNOT_DELETE_ACTIVE_KEY) else None,
              if (!validated) Option(constants.Response.INVALID_PASSWORD) else None,
              if (balance == MicroNumber.zero) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (deleteKey.identityIssued.isEmpty) Option(constants.Response.KEY_PROVISION_STATE_UNKNOWN) else None,
            ).flatten

            if (errors.isEmpty) {
              if (deleteKey.identityIssued.getOrElse(true)) {
                val blockchainTransaction = masterTransactionUnprovisionAddressTransactions.Utility.transaction(
                  fromAddress = loginState.address,
                  accountId = loginState.username,
                  toAddress = deleteKeyData.address,
                  gasPrice = constants.Transaction.DefaultGasPrice,
                  ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(txKey.encryptedPrivateKey, deleteKeyData.password))
                )
                for {
                  blockchainTransaction <- blockchainTransaction
                } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
              } else {
                val deleteKey = masterKeys.Service.delete(accountId = loginState.username, address = deleteKeyData.address)
                for {
                  _ <- deleteKey
                } yield PartialContent(views.html.setting.keyDeletedSuccessfully())
              }
            } else errors.head.throwBaseException()
          }

          (for {
            (validated, txKey) <- validateAndGetTxKey
            deleteKey <- deleteKey
            balance <- balance
            result <- delete(validated, txKey, balance, deleteKey)
          } yield result
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.deleteKey(DeleteKey.form.withGlobalError(baseException.failure.message), deleteKeyData.address))
          }
        }
      )
  }

  def changeManagedToUnmanagedForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.changeManagedToUnmanaged(address = address))
  }

  def changeManagedToUnmanaged: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangeManagedToUnmanaged.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.changeManagedToUnmanaged(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.MANAGED_KEY_ADDRESS.name, ""))))
        },
        changeManagedToUnmanagedData => {
          val validate = masterKeys.Service.changeManagedToUnmanaged(accountId = loginState.username, address = changeManagedToUnmanagedData.address, password = changeManagedToUnmanagedData.password)

          (for {
            _ <- validate
          } yield PartialContent(views.html.setting.changeManagedToUnmanaged(address = changeManagedToUnmanagedData.address))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.changeManagedToUnmanaged(ChangeManagedToUnmanaged.form.withGlobalError(baseException.failure.message), changeManagedToUnmanagedData.address))
          }

        }
      )
  }

  def provisionAddressForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.provisionAddress(address = address))
  }

  def provisionAddress(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ProvisionAddress.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.provisionAddress(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
        },
        provisionAddressData => {
          val validateAndGetKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = provisionAddressData.address, password = provisionAddressData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)

          def provision(validated: Boolean, key: master.Key, balance: MicroNumber) = {
            val errors = Seq(
              if (key.identityIssued.getOrElse(true)) Option(constants.Response.ADDRESS_ALREADY_PROVISIONED) else None,
              if (!validated) Option(constants.Response.INVALID_PASSWORD) else None,
              if (balance == MicroNumber.zero) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
            ).flatten

            if (errors.isEmpty) {
              masterTransactionProvisionAddressTransactions.Utility.transaction(
                fromAddress = loginState.address,
                accountId = loginState.username,
                toAddress = provisionAddressData.address,
                gasPrice = constants.Transaction.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, provisionAddressData.password))
              )
            } else errors.head.throwBaseException()
          }

          (for {
            (validated, key) <- validateAndGetKey
            balance <- balance
            blockchainTransaction <- provision(validated, key, balance)
          } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.provisionAddress(ProvisionAddress.form.withGlobalError(baseException.failure.message), provisionAddressData.address))
          }
        }
      )
  }

}