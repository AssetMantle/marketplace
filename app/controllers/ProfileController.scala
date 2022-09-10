package controllers

import controllers.actions._
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, _}
import play.api.mvc.{AbstractController, MessagesControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import views.profile.whitelist.companion._

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
                                   masterWhitelistMembers: master.WhitelistMembers,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.PROFILE_CONTROLLER

  def viewPersonalProfile(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginAction { implicit loginState =>
      implicit request =>
        Ok(views.html.profile.viewPersonalProfile())
    }
  }

  def personalProfile(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginAction { implicit loginState =>
      implicit request =>
        Ok(views.html.profile.personalProfile())
    }
  }

  def createdWhitelists(pageNmber: Int): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelists = masterWhitelists.Service.getByOwner(loginState.username, pageNmber)
        (for {
          whitelists <- whitelists
        } yield Ok(views.html.profile.whitelist.createdWhitelists(whitelists))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def createWhitelistForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.whitelist.create())
  }

  def createWhitelist(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Create.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.whitelist.create(formWithErrors)))
        },
        createData => {
          val create = masterWhitelists.Service.addWhitelist(ownerId = loginState.username, name = createData.name, description = createData.description, maxMembers = createData.maxMembers, startEpoch = createData.startEpoch, endEpoch = createData.endEpoch)

          (for {
            id <- create
          } yield PartialContent(views.html.profile.whitelist.whitelistSuccessful(whitelistId = id))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.whitelist.create(Create.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def editWhitelistForm(whitelistId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelist = masterWhitelists.Service.tryGet(whitelistId)
      for {
        whitelist <- whitelist
      } yield Ok(views.html.profile.whitelist.edit(Edit.form.fill(Edit.Data(whitelistId = whitelistId, name = whitelist.name, description = whitelist.description, maxMembers = whitelist.maxMembers, startEpoch = whitelist.startEpoch, endEpoch = whitelist.endEpoch))))
  }

  def editWhitelist(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Edit.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.whitelist.edit(formWithErrors)))
        },
        editData => {
          val edit = masterWhitelists.Service.edit(id = editData.whitelistId, name = editData.name, description = editData.description, maxMembers = editData.maxMembers, startEpoch = editData.startEpoch, endEpoch = editData.endEpoch)

          (for {
            _ <- edit
          } yield PartialContent(views.html.profile.whitelist.whitelistSuccessful(whitelistId = editData.whitelistId, edited = true))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.whitelist.edit(Edit.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def joinedWhitelists(pageNumber: Int): EssentialAction = cached.apply(req => req.path + "/" + pageNumber.toString + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelistIds = masterWhitelistMembers.Service.getAllForMember(loginState.username, pageNumber = pageNumber, perPage = constants.CommonConfig.Pagination.WhitelistPerPage)

        def whitelists(whitelistIds: Seq[String]) = masterWhitelists.Service.getByIds(whitelistIds)

        (for {
          whitelistIds <- whitelistIds
          whitelists <- whitelists(whitelistIds)
        } yield Ok(views.html.profile.whitelist.joinedWhitelists(whitelists))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def acceptInviteDetails(whitelistInviteId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelist = masterWhitelists.Service.tryGet(whitelistInviteId)

      for {
        whitelist <- whitelist
      } yield Ok(views.html.profile.whitelist.acceptInviteDetails(whitelist = whitelist))
  }


  def acceptInvite(whitelistId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkInviteValid = masterWhitelists.Service.checkInviteValid(whitelistId)

      def checkAndAdd(inviteValid: Boolean) = if (inviteValid) {
        masterWhitelistMembers.Service.add(whitelistId = whitelistId, accountId = loginState.username)
      } else constants.Response.WHITELIST_INVITATION_EXPIRED.throwFutureBaseException()

      (for {
        inviteValid <- checkInviteValid
        _ <- checkAndAdd(inviteValid)
      } yield PartialContent(views.html.profile.whitelist.acceptInviteSuccessful())
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

}
