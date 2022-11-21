package controllers

import controllers.actions._
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._

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
                                   masterWhitelists: master.Whitelists,
                                   masterCollections: master.Collections,
                                   masterWhitelistMembers: master.WhitelistMembers,
                                   masterTransactionNotifications: masterTransaction.Notifications,
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

  def markNotificationRead(notificationId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val markRead = masterTransactionNotifications.Service.markNotificationRead(notificationId = notificationId, accountId = loginState.username)
      (for {
        unread <- markRead
      } yield Ok(unread.toString)
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
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

  def markAllNotificationRead(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val mark = masterTransactionNotifications.Service.markAllRead(loginState.username)
      (for {
        _ <- mark
      } yield Ok
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

}
