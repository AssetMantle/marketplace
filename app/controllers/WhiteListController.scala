package controllers

import controllers.actions.{WithLoginActionAsync, WithoutLoginAction, WithoutLoginActionAsync}
import exceptions.BaseException
import models.{master, masterTransaction}
import views.whiteList.companion._
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, _}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhiteListController @Inject()(
                                     messagesControllerComponents: MessagesControllerComponents,
                                     cached: Cached,
                                     withoutLoginActionAsync: WithoutLoginActionAsync,
                                     withLoginActionAsync: WithLoginActionAsync,
                                     withoutLoginAction: WithoutLoginAction,
                                     masterAccounts: master.Accounts,
                                     masterWhiteLists: master.WhiteLists,
                                     masterWhiteListMembers: master.WhiteListMembers,
                                     masterTransactionWhiteListInvites: masterTransaction.WhiteListInvites,
                                   )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.WHITE_LIST_CONTROLLER

  def createWhiteListForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.whiteList.create())
  }

  def createWhiteList(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Create.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.whiteList.create(formWithErrors)))
        },
        createData => {
          val create = masterWhiteLists.Service.addWhiteList(ownerId = loginState.username, name = createData.name, description = createData.description, maxMembers = createData.maxMembers)

          (for {
            id <- create
          } yield PartialContent(views.html.whiteList.invite(whiteListId = id))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.whiteList.create(Create.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def whiteListInviteForm(whiteListId: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.whiteList.invite(whiteListId = whiteListId))
  }

  def whiteListInvite(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Invite.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.whiteList.invite(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WHITE_LIST_ID.name, ""))))
        },
        inviteData => {
          val create = masterTransactionWhiteListInvites.Service.add(whiteListId = inviteData.whiteListId, startEpoch = inviteData.startEpoch, endEpoch = inviteData.endEpoch)

          (for {
            inviteId <- create
          } yield PartialContent(views.html.whiteList.inviteCreationSuccessful(inviteId = inviteId))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.whiteList.invite(Invite.form.withGlobalError(baseException.failure.message), inviteData.whiteListId))
          }
        }
      )
  }

  def acceptInviteDetails(whiteListInviteId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val invite = masterTransactionWhiteListInvites.Service.tryGet(whiteListInviteId)

      for {
        invite <- invite
        whiteList <- masterWhiteLists.Service.tryGet(invite.whiteListId)
      } yield Ok(views.html.whiteList.acceptInviteDetails(whiteList = whiteList, whiteListInvite = invite))
  }


  def acceptInvite(whiteListId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkInviteValid = masterTransactionWhiteListInvites.Service.checkInviteValid(whiteListId)

      def checkAndAdd(inviteValid: Boolean) = if (inviteValid) {
        masterWhiteListMembers.Service.add(whiteListId = whiteListId, accountId = loginState.username)
      } else constants.Response.WHITE_LIST_INVITATION_EXPIRED.throwFutureBaseException()

      (for {
        inviteValid <- checkInviteValid
        _ <- checkAndAdd(inviteValid)
      } yield PartialContent(views.html.whiteList.acceptInviteSuccessful())
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }


}