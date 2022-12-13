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
                                     masterCollections: master.Collections,
                                     masterWhitelists: master.Whitelists,
                                     masterWhitelistMembers: master.WhitelistMembers,
                                     masterSales: master.Sales,
                                   )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.WHITELIST_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def whitelistSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelistsMade = masterWhitelists.Service.totalWhitelistsByOwner(loginState.username)

        (for {
          whitelistsMade <- whitelistsMade
        } yield Ok(views.html.profile.whitelist.whitelistSection(whitelistsMade = whitelistsMade))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def createdWhitelists(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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
          val create = if (loginState.isCreator) masterWhitelists.Service.addWhitelist(ownerId = loginState.username, name = createData.name, description = createData.description, maxMembers = createData.maxMembers, startEpoch = createData.startEpoch, endEpoch = createData.endEpoch)
          else constants.Response.NO_COLLECTION_TO_CREATE_WHITELIST.throwFutureBaseException()

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

  def joinedWhitelists(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def whitelistTotalMembers(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def acceptInviteDetails(whitelistId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def acceptInvite(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AcceptOrLeave.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(formWithErrors.errors.map(_.message).mkString(" ")))
        },
        acceptData => {
          val whitelist = masterWhitelists.Service.tryGet(acceptData.whitelistId)

          def checkAndAdd(whitelist: Whitelist) = masterWhitelistMembers.Service.checkAndAdd(whitelist = whitelist, accountId = loginState.username)

          (for {
            whitelist <- whitelist
            _ <- checkAndAdd(whitelist)
          } yield PartialContent(views.html.profile.whitelist.acceptInviteSuccessful(whitelist))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.JOIN_WHITELIST_FAILED, subHeading = baseException.failure.message))
          }
        }
      )

  }

  def leaveWhitelistDetails(whitelistId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def leaveWhitelist(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AcceptOrLeave.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(formWithErrors.errors.map(_.message).mkString(" ")))
        },
        leaveData => {
          val whitelist = masterWhitelists.Service.tryGet(leaveData.whitelistId)
          val deleteMember = masterWhitelistMembers.Service.delete(whitelistId = leaveData.whitelistId, accountId = loginState.username)

          (for {
            whitelist <- whitelist
            _ <- deleteMember
          } yield PartialContent(views.html.profile.whitelist.leaveWhitelistSuccessful(whitelist))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.LEAVE_WHITELIST_FAILED, subHeading = baseException.failure.message))
          }
        }
      )
  }

  def listMembers(whitelistId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelist = masterWhitelists.Service.tryGet(whitelistId)
        val members = masterWhitelistMembers.Service.getAllForWhitelist(whitelistId)

        (for {
          whitelist <- whitelist
          members <- members
        } yield if (whitelist.ownerId == loginState.username) Ok(views.html.profile.whitelist.listMembers(whitelist = whitelist, members = members))
        else constants.Response.NOT_WHITELIST_CREATOR.throwBaseException()
          ).recover {
          case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.LEAVE_WHITELIST_FAILED, subHeading = baseException.failure.message))
        }
    }
  }

  def deleteMember(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      RemoveMember.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest)
        },
        deleteMemberData => {
          val whitelist = masterWhitelists.Service.tryGet(deleteMemberData.whitelistId)

          def deleteMember(whitelist: Whitelist) = if (whitelist.ownerId == loginState.username) masterWhitelistMembers.Service.delete(whitelistId = deleteMemberData.whitelistId, accountId = deleteMemberData.username)
          else constants.Response.NOT_WHITELIST_CREATOR.throwFutureBaseException()

          (for {
            whitelist <- whitelist
            _ <- deleteMember(whitelist)
          } yield Ok
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def detail(whitelistId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val whitelist = masterWhitelists.Service.tryGet(whitelistId)
        val members = masterWhitelistMembers.Service.getWhitelistsMemberCount(whitelistId)
        val sales = masterSales.Service.getByWhitelistId(whitelistId)

        (for {
          whitelist <- whitelist
          members <- members
          sales <- sales
        } yield Ok(views.html.profile.whitelist.whitelistDetail(whitelist = whitelist, totalMembers = members, sales = sales))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }
}