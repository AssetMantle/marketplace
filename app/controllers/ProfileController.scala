package controllers

import controllers.actions._
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.notification.companion.MarkNotificationsRead

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProfileController @Inject()(
                                   messagesControllerComponents: MessagesControllerComponents,
                                   cached: Cached,
                                   withoutLoginActionAsync: WithoutLoginActionAsync,
                                   withLoginAction: WithLoginAction,
                                   withLoginActionAsync: WithLoginActionAsync,
                                   withoutLoginAction: WithoutLoginAction,
                                   masterAccounts: master.Accounts,
                                   masterKeys: master.Keys,
                                   masterWhitelists: master.Whitelists,
                                   masterCollections: master.Collections,
                                   masterWhitelistMembers: master.WhitelistMembers,
                                   masterTransactionNotifications: masterTransaction.Notifications,
                                   masterNFTOwners: master.NFTOwners,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.PROFILE_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def viewDefaultProfile(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      implicit val optionalLoginState: Option[LoginState] = Option(loginState)
      Future(Ok(views.html.profile.viewProfile(loginState.username, constants.View.CREATED)))
  }

  def viewProfile(accountId: String, activeTab: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.profile.viewProfile(accountId = accountId, activeTab = activeTab)))
  }

  def profile(accountId: String, activeTab: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.profile.profile(accountId = accountId, activeTab = activeTab)))
  }

  def notificationPopup(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.notification.commonNotificationPopup()))
    }
  }

  def loadMoreNotifications(pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val notifications = if (pageNumber < 1) constants.Response.INVALID_PAGE_NUMBER.throwFutureBaseException()
        else masterTransactionNotifications.Service.get(loginState.username, pageNumber)
        (for {
          notifications <- notifications
        } yield Ok(views.html.notification.notificationPerPage(notifications, pageNumber))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def markNotificationsRead(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      MarkNotificationsRead.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest)
        },
        markNotificationsReadData => {
          val update = if (markNotificationsReadData.markAllRead) masterTransactionNotifications.Service.markAllRead(loginState.username)
          else masterTransactionNotifications.Service.markNotificationRead(notificationId = markNotificationsReadData.notificationId.getOrElse(""), accountId = loginState.username)
          (for {
            unread <- update
          } yield Ok(unread.toString)
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def countUnreadNotification(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val unread = masterTransactionNotifications.Service.getNumberOfUnread(loginState.username)
      (for {
        unread <- unread
      } yield Ok(unread.toString)
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def profileInfoCard(accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val key = masterKeys.Service.tryGetActive(accountId)
        val account = masterAccounts.Service.tryGet(accountId)
        (for {
          key <- key
          account <- account
        } yield Ok(views.html.profile.profileInfoCard(accountId = accountId, address = key.address, createdOn = account.createdOn.fold(0L)(_.getTime)))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def profileActivityCard(accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.profileActivityCard(accountId)))
    }
  }


  def profileAnalysisCard(accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val countOwnedNFTs = masterNFTOwners.Service.countOwnedNFTs(accountId)
        val countCreated = masterCollections.Service.countCreated(accountId)
        (for {
          countOwnedNFTs <- countOwnedNFTs
          countCreated <- countCreated
        } yield Ok(views.html.profile.profileAnalysis(totalCollected = countOwnedNFTs, totalCollectionCreated = countCreated))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }
}
