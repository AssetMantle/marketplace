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
  val SIGN_OUT = new Form("SIGN_OUT", routes.javascript.AccountController.signOutForm, routes.AccountController.signOut())

  val MANAGED_ADDRESS = new Form("MANAGED_ADDRESS", routes.javascript.ProfileController.managedAddressForm, routes.ProfileController.managedAddress())
  val UNMANAGED_ADDRESS = new Form("UNMANAGED_ADDRESS", routes.javascript.ProfileController.unmanagedAddressForm, routes.ProfileController.unmanagedAddress())
  val CHANGE_WALLET_NAME = new Form("CHANGE_WALLET_NAME", routes.javascript.ProfileController.changeWalletNameForm, routes.ProfileController.changeWalletName())
  val ADD_CUSTODIAL_KEY = new Form("ADD_CUSTODIAL_KEY", routes.javascript.AccountController.addCustodialKeyForm, routes.AccountController.addCustodialKey())
  val REMOVE_CUSTODIAL_KEY = new Form("REMOVE_CUSTODIAL_KEY", routes.javascript.AccountController.removeCustodialKeyForm, routes.AccountController.removeCustodialKey())
}
