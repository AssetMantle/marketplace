package controllers

import controllers.actions.{WithLoginActionAsync, WithoutLoginAction, WithoutLoginActionAsync}
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CollectionController @Inject()(
                                      messagesControllerComponents: MessagesControllerComponents,
                                      cached: Cached,
                                      withoutLoginActionAsync: WithoutLoginActionAsync,
                                      withLoginActionAsync: WithLoginActionAsync,
                                      withoutLoginAction: WithoutLoginAction,
                                      masterAccounts: master.Accounts,
                                      masterWallets: master.Wallets,
                                      masterCollections: master.Collections,
                                    )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.COLLECTION_CONTROLLER


  def all(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val allCollections = masterCollections.Service.fetchAll()
      (for {
        allCollections <- allCollections
      } yield Ok(views.html.collection.all(allCollections))
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

}