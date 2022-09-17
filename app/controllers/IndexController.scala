package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.Starter

import javax.inject._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class IndexController @Inject()(
                                 messagesControllerComponents: MessagesControllerComponents,
                                 cached: Cached,
                                 withoutLoginActionAsync: WithoutLoginActionAsync,
                                 withoutLoginAction: WithoutLoginAction,
                                 withUsernameToken: WithUsernameToken,
                                 starter: Starter,
                                 blockchainAccounts: models.blockchain.Accounts
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.INDEX_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections("art")

  def index: EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        optionalLoginState match {
          case Some(loginState) =>
            implicit val loginStateImplicit: LoginState = loginState
            Future(Ok(views.html.collection.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)))
          case None => Future(Ok(views.html.index()))
        }
    }
  }

  //    starter.start()
}
