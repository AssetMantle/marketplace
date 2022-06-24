package controllers

import play.api.http.MimeTypes
import play.api.mvc._
import play.api.routing._
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}

@Singleton
class JavaScriptRoutesController @Inject()(messagesControllerComponents: MessagesControllerComponents)(implicit configuration: Configuration) extends AbstractController(messagesControllerComponents) {

  private implicit val logger: Logger = Logger(this.getClass)

  def javascriptRoutes: Action[AnyContent] = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Assets.versioned,

        routes.javascript.AccountController.signUpForm,
        routes.javascript.AccountController.walletMnemonicsForm,
        routes.javascript.AccountController.checkUsernameAvailable,
        routes.javascript.AccountController.signInForm,

        routes.javascript.CollectionController.all,

        routes.javascript.NFTController.collectionAllNFT,
        routes.javascript.NFTController.get,
      )
    ).as("text/javascript")

  }
}
