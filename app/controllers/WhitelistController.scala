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
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class WhitelistController @Inject()(
                                     messagesControllerComponents: MessagesControllerComponents,
                                     cached: Cached,
                                     withoutLoginActionAsync: WithoutLoginActionAsync,
                                     withLoginAction: WithLoginAction,
                                     withLoginActionAsync: WithLoginActionAsync,
                                     withoutLoginAction: WithoutLoginAction,
                                     masterNFTs: master.NFTs,
                                     masterNFTOwners: master.NFTOwners,
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
          val totalCreated = masterWhitelists.Service.totalWhitelistsByOwner(loginState.username)

          def create(totalCreated: Int) = {
            val errors = Seq(
              if (!loginState.isCreator) Option(constants.Response.NO_COLLECTION_TO_CREATE_WHITELIST) else None,
              if (totalCreated >= constants.Whitelist.MaxAllowed) Option(constants.Response.CANNOT_CREATE_MORE_ALLOWED_WHITELISTS) else None,
            ).flatten
            if (errors.isEmpty) masterWhitelists.Service.addWhitelist(ownerId = loginState.username, name = createData.name, description = createData.description, maxMembers = createData.maxMembers, startEpoch = createData.startEpoch, endEpoch = createData.endEpoch)
            else errors.head.throwFutureBaseException()
          }

          (for {
            totalCreated <- totalCreated
            id <- create(totalCreated)
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

  def deleteForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelist = masterWhitelists.Service.tryGet(id)
      val totalMembers = masterWhitelistMembers.Service.getWhitelistsMemberCount(id)
      (for {
        whitelist <- whitelist
        totalMembers <- totalMembers
      } yield Ok(views.html.profile.whitelist.delete(whitelist = whitelist, totalMembers = totalMembers))
        ).recover {
        case _: BaseException => BadRequest
      }
  }

  def delete(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Delete.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest)
        },
        deleteData => {
          val whitelist = masterWhitelists.Service.tryGet(deleteData.whitelistId)

          def checkAndDelete(whitelist: Whitelist) = {
            val errors = Seq(
              if (whitelist.ownerId != loginState.username) Option(constants.Response.NOT_WHITELIST_CREATOR) else None,
            ).flatten
            if (errors.isEmpty) {
              val deleteMembers = masterWhitelistMembers.Service.deleteAllMembers(whitelist.id)

              def deleteWhitelist() = masterWhitelists.Service.delete(whitelist.id)

              for {
                _ <- deleteMembers
                _ <- deleteWhitelist()
              } yield ()

            } else errors.head.throwFutureBaseException()
          }

          (for {
            whitelist <- whitelist
            _ <- checkAndDelete(whitelist)
          } yield {
            implicit val optionalLoginState: Option[LoginState] = Option(loginState)
            Ok(views.html.profile.viewProfile(accountId = loginState.username, activeTab = constants.View.WHITELIST))
          }
            ).recover {
            case _: BaseException => BadRequest
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

        def collectionOnSales(collectionIds: Seq[String]) = masterCollections.Service.getCollectionNames(collectionIds)

        (for {
          whitelist <- whitelist
          members <- members
          sales <- sales
          collectionOnSales <- collectionOnSales(sales.map(_.collectionId))
        } yield Ok(views.html.profile.whitelist.whitelistDetail(whitelist = whitelist, totalMembers = members, sales = sales, collectionOnSales = collectionOnSales))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def addFromNFTOwnersForm(whitelistId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collections = masterCollections.Service.getByCreator(loginState.username)
      (for {
        collections <- collections
      } yield Ok(views.html.profile.whitelist.addFromNFTOwners(whitelistId = whitelistId, options = collections.toMap))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def addFromNFTOwners(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddFromNFTOwners.form.bindFromRequest().fold(
        formWithErrors => {
          val collections = masterCollections.Service.getByCreator(loginState.username)
          (for {
            collections <- collections
          } yield BadRequest(views.html.profile.whitelist.addFromNFTOwners(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WHITELIST_ID.name, ""), collections.toMap))
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        },
        addFromNFTOwnersData => {
          val allNFTOwners = masterNFTOwners.Service.getOwnersByCollectionId(addFromNFTOwnersData.collectionId)
          val existingMembers = masterWhitelistMembers.Service.getAllMembers(addFromNFTOwnersData.whitelistId)
          val whitelist = masterWhitelists.Service.tryGet(addFromNFTOwnersData.whitelistId)

          def addMembers(add: Seq[String], whitelist: Whitelist) = if (whitelist.ownerId == loginState.username) {
            val index = add.indexWhere(_ == loginState.username)
            if (index == -1) masterWhitelistMembers.Service.add(whitelistId = addFromNFTOwnersData.whitelistId, accountIds = add)
            else masterWhitelistMembers.Service.add(whitelistId = addFromNFTOwnersData.whitelistId, accountIds = add.zipWithIndex.filter(_._2 != index).map(_._1))
          } else constants.Response.NOT_WHITELIST_CREATOR.throwBaseException()

          (for {
            allNFTOwners <- allNFTOwners
            existingMembers <- existingMembers
            whitelist <- whitelist
            _ <- addMembers(allNFTOwners.diff(existingMembers), whitelist)
          } yield PartialContent(views.html.profile.whitelist.membersAddedSuccessfully())
            ).recover {
            case baseException: BaseException => {
              Await.result({
                val collections = masterCollections.Service.getByCreator(loginState.username)
                (for {
                  collections <- collections
                } yield BadRequest(views.html.profile.whitelist.addFromNFTOwners(AddFromNFTOwners.form.withGlobalError(baseException.failure.message), addFromNFTOwnersData.whitelistId, collections.toMap))
                  ).recover {
                  case baseException: BaseException => BadRequest(baseException.failure.message)
                }
              }, Duration.Inf
              )
            }
          }
        }
      )
  }
}