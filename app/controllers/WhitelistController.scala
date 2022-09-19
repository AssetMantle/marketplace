package controllers

import controllers.actions._
import exceptions.BaseException
import models.master
import models.master.Whitelist
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.profile.whitelist.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhitelistController @Inject()(
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

  private implicit val module: String = constants.Module.WHITELIST_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def whitelistSection(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.whitelist.whitelistSection()))
    }
  }

  def createdWhitelists(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelistsMade = masterWhitelists.Service.totalWhitelistsByOwner(loginState.username)
        (for {
          whitelistsMade <- whitelistsMade
        } yield Ok(views.html.profile.whitelist.createdWhitelists(whitelistsMade))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def createdWhitelistsPerPage(pageNumber: Int): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelists = masterWhitelists.Service.getByOwner(loginState.username, pageNumber)
      (for {
        whitelists <- whitelists
      } yield Ok(views.html.profile.whitelist.createdWhitelistsPerPage(whitelists, (pageNumber - 1) * constants.CommonConfig.Pagination.WhitelistPerPage))
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
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

  def joinedWhitelists(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalWhitelistsJoined = masterWhitelistMembers.Service.totalJoined(loginState.username)

        (for {
          totalWhitelistsJoined <- totalWhitelistsJoined
        } yield Ok(views.html.profile.whitelist.joinedWhitelists(totalWhitelistsJoined))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def joinedWhitelistsPerPage(pageNumber: Int): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelistIds = masterWhitelistMembers.Service.getAllForMember(loginState.username, pageNumber = pageNumber, perPage = constants.CommonConfig.Pagination.WhitelistPerPage)

      def whitelists(whitelistIds: Seq[String]) = masterWhitelists.Service.getByIds(whitelistIds)

      (for {
        whitelistIds <- whitelistIds
        whitelists <- whitelists(whitelistIds)
      } yield Ok(views.html.profile.whitelist.joinedWhitelistsPerPage(whitelists.sortBy(_.startEpoch), (pageNumber - 1) * constants.CommonConfig.Pagination.WhitelistPerPage))
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

  def whitelistTotalMembers(id: String): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelistsMemberCount = masterWhitelistMembers.Service.getWhitelistsMemberCount(id)

        (for {
          whitelistsMemberCount <- whitelistsMemberCount
        } yield Ok(whitelistsMemberCount.toString)
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def viewAcceptInviteDetails(whitelistId: String): Action[AnyContent] = withLoginAction { implicit loginState =>
    implicit request =>
      Ok(views.html.profile.whitelist.viewAcceptInviteDetails(whitelistId = whitelistId))
  }

  def acceptInviteDetails(whitelistId: String): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelist = masterWhitelists.Service.tryGet(whitelistId)
        val totalMembers = masterWhitelistMembers.Service.getWhitelistsMemberCount(whitelistId)

        (for {
          whitelist <- whitelist
          totalMembers <- totalMembers
        } yield Ok(views.html.profile.whitelist.acceptInviteDetails(whitelist = whitelist, maxReached = totalMembers >= whitelist.maxMembers))
          ).recover {
          case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.JOIN_WHITELIST_FAILED, subHeading = baseException.failure.message))
        }
    }
  }

  def acceptInvite(whitelistId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelist = masterWhitelists.Service.tryGet(whitelistId)

      def checkAndAdd(whitelist: Whitelist) = masterWhitelistMembers.Service.checkAndAdd(whitelist = whitelist, accountId = loginState.username)

      (for {
        whitelist <- whitelist
        _ <- checkAndAdd(whitelist)
      } yield Ok(views.html.profile.whitelist.acceptInviteSuccessful(whitelist))
        ).recover {
        case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.JOIN_WHITELIST_FAILED, subHeading = baseException.failure.message))
      }
  }

  def leaveWhitelistDetails(whitelistId: String): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelist = masterWhitelists.Service.tryGet(whitelistId)

        (for {
          whitelist <- whitelist
        } yield Ok(views.html.profile.whitelist.leaveWhitelistDetails(whitelist = whitelist))
          ).recover {
          case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.LEAVE_WHITELIST_FAILED, subHeading = baseException.failure.message))
        }
    }
  }

  def leaveWhitelist(whitelistId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelist = masterWhitelists.Service.tryGet(whitelistId)
      val deleteMember = masterWhitelistMembers.Service.deleteMember(whitelistId = whitelistId, accountId = loginState.username)

      (for {
        whitelist <- whitelist
        _ <- deleteMember
      } yield Ok(views.html.profile.whitelist.leaveWhitelistSuccessful(whitelist))
        ).recover {
        case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.LEAVE_WHITELIST_FAILED, subHeading = baseException.failure.message))
      }
  }

}