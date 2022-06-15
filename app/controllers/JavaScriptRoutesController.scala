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
        routes.javascript.AccountController.createWalletForm,
        routes.javascript.AccountController.createWalletSeedPhraseForm,
        routes.javascript.AccountController.createWalletSuccessForm,
        routes.javascript.AccountController.createWalletErrorForm,
        routes.javascript.AccountController.signInForm,
        routes.javascript.AccountController.forgotPasswordUsernameForm,
        routes.javascript.AccountController.forgotPasswordSeedPhraseForm,
        routes.javascript.AccountController.forgotPasswordNewPasswordForm,
        routes.javascript.AccountController.forgotPasswordSuccessForm,
        routes.javascript.AccountController.signUpBootstrapForm,
      )
    ).as("text/javascript")

  }
}
