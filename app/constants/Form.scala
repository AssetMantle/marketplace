package constants

import controllers.routes
import play.api.mvc.Call
import play.api.routing.JavaScriptReverseRoute

class Form(name: String, val get: JavaScriptReverseRoute, val post: Call) {
  val title: String = Seq("FORM", name, "TITLE").mkString(".")
  val subTitle: String = Seq("FORM", name, "SUBTITLE").mkString(".")
  val submit: String = Seq("FORM", name, "SUBMIT").mkString(".")
  val button: String = Seq("FORM", name, "BUTTON").mkString(".")
}

object Form {

  //AccountController
  val SIGN_UP = new Form("SIGN_UP", routes.javascript.AccountController.signUpForm, routes.AccountController.signUp())
  val VERIFY_WALLET_MNEMONICS = new Form("VERIFY_WALLET_MNEMONICS", routes.javascript.AccountController.verifyWalletMnemonicsForm, routes.AccountController.verifyWalletMnemonics())
  val SIGN_IN = new Form("SIGN_IN", routes.javascript.AccountController.signInForm, routes.AccountController.signIn())
  val FORGET_PASSWORD = new Form("FORGET_PASSWORD", routes.javascript.AccountController.forgetPasswordForm, routes.AccountController.forgetPassword())
  val CHANGE_PASSWORD = new Form("CHANGE_PASSWORD", routes.javascript.AccountController.changePasswordForm, routes.AccountController.changePassword())
  val SIGN_OUT = new Form("SIGN_OUT", routes.javascript.AccountController.signOutForm, routes.AccountController.signOut())

  val ADD_MANAGED_KEY = new Form("ADD_MANAGED_KEY", routes.javascript.ProfileController.addManagedKeyForm, routes.ProfileController.addManagedKey())
  val ADD_UNMANAGED_KEY = new Form("ADD_UNMANAGED_KEY", routes.javascript.ProfileController.addUnmanagedKeyForm, routes.ProfileController.addUnmanagedKey())
  val CHANGE_KEY_NAME = new Form("CHANGE_KEY_NAME", routes.javascript.ProfileController.changeKeyNameForm, routes.ProfileController.changeKeyName())
  val VIEW_MNEMONICS = new Form("VIEW_MNEMONICS", routes.javascript.ProfileController.viewMnemonicsForm, routes.ProfileController.viewMnemonics())
  val REMOVE_ACCOUNT = new Form("REMOVE_ACCOUNT", routes.javascript.ProfileController.deleteAccountForm, routes.ProfileController.deleteAccount())
}
