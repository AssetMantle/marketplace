package constants

import controllers.routes
import play.api.mvc.Call
import play.api.routing.JavaScriptReverseRoute

class Form(name: String, subHeading: String, val get: JavaScriptReverseRoute, val post: Call) {
  val title: String = Seq("FORM", name, "TITLE").mkString(".")
  val subTitle: String = Seq("FORM", subHeading, "SUBTITLE").mkString(".")
  val submit: String = Seq("FORM", name, "SUBMIT").mkString(".")
}

object Form {

  //AccountController
  val SIGN_UP = new Form("SIGN_UP", "SIGN_UP", routes.javascript.AccountController.signUpForm, routes.AccountController.signUp)
  val CREATE_WALLET = new Form("CREATE_WALLET", "CREATE_WALLET", routes.javascript.AccountController.createWalletForm, routes.AccountController.createWalletForm)
  val CREATE_WALLET_SEED_PHRASE = new Form("CREATE_WALLET_SEED_PHRASE", "CREATE_WALLET_SEED_PHRASE", routes.javascript.AccountController.createWalletSeedPhraseForm, routes.AccountController.createWalletSeedPhrase)
  val CREATE_WALLET_SUCCESS = new Form("CREATE_WALLET_SUCCESS", "CREATE_WALLET_SUCCESS", routes.javascript.AccountController.createWalletSuccessForm, routes.AccountController.createWalletSuccessForm)
  val CREATE_WALLET_ERROR = new Form("CREATE_WALLET_ERROR", "CREATE_WALLET_ERROR", routes.javascript.AccountController.createWalletErrorForm, routes.AccountController.createWalletErrorForm)
  val SIGN_IN = new Form("SIGN_IN", "SIGN_IN", routes.javascript.AccountController.signInForm, routes.AccountController.signIn)
  val FORGOT_PASSWORD_USERNAME = new Form("FORGOT_PASSWORD_USERNAME", "FORGOT_PASSWORD_USERNAME", routes.javascript.AccountController.forgotPasswordUsernameForm, routes.AccountController.forgotPasswordUsername)
  val FORGOT_PASSWORD_SEED_PHRASE = new Form("FORGOT_PASSWORD_SEED_PHRASE", "FORGOT_PASSWORD_SEED_PHRASE", routes.javascript.AccountController.forgotPasswordSeedPhraseForm, routes.AccountController.forgotPasswordSeedPhrase)
  val FORGOT_PASSWORD_NEW_PASSWORD = new Form("FORGOT_PASSWORD_NEW_PASSWORD", "FORGOT_PASSWORD_NEW_PASSWORD", routes.javascript.AccountController.forgotPasswordNewPasswordForm, routes.AccountController.forgotPasswordNewPassword)
  val FORGOT_PASSWORD_SUCCESS = new Form("FORGOT_PASSWORD_SUCCESS", "FORGOT_PASSWORD_SUCCESS", routes.javascript.AccountController.forgotPasswordSuccessForm, routes.AccountController.forgotPasswordSuccessForm)
  val SIGN_UP_BOOTSTRAP = new Form("SIGN_UP_BOOTSTRAP", "SIGN_UP_BOOTSTRAP", routes.javascript.AccountController.signUpBootstrapForm, routes.AccountController.signUpBootstrap)
}
