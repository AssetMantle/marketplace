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
  val MIGRATE_WALLET_TO_KEY = new Form("MIGRATE_WALLET_TO_KEY", routes.javascript.AccountController.migrateWalletToKeyForm, routes.AccountController.migrateWalletToKey())

  val ADD_MANAGED_KEY = new Form("ADD_MANAGED_KEY", routes.javascript.ProfileController.addManagedKeyForm, routes.ProfileController.addManagedKey())
  val ADD_UNMANAGED_KEY = new Form("ADD_UNMANAGED_KEY", routes.javascript.ProfileController.addUnmanagedKeyForm, routes.ProfileController.addUnmanagedKey())
  val CHANGE_KEY_NAME = new Form("CHANGE_KEY_NAME", routes.javascript.ProfileController.changeKeyNameForm, routes.ProfileController.changeKeyName())

  val VIEW_MNEMONICS = new Form("VIEW_MNEMONICS", routes.javascript.ProfileController.viewMnemonicsForm, routes.ProfileController.viewMnemonics())
  val DELETE_KEY = new Form("DELETE_KEY", routes.javascript.ProfileController.deleteKeyForm, routes.ProfileController.deleteKey())

  val CHANGE_MANAGED_TO_UNMANAGED = new Form("CHANGE_MANAGED_TO_UNMANAGED", routes.javascript.ProfileController.deleteKeyForm, routes.ProfileController.deleteKey())

  val SEND_COIN = new Form("SEND_COIN", routes.javascript.BlockchainTransactionController.sendCoinForm, routes.BlockchainTransactionController.sendCoin())

  val CREATE_WHITE_LIST = new Form("CREATE_WHITE_LIST", routes.javascript.WhiteListController.createWhiteListForm, routes.WhiteListController.createWhiteList())
  val WHITE_LIST_INVITE = new Form("WHITE_LIST_INVITE", routes.javascript.WhiteListController.whiteListInviteForm, routes.WhiteListController.whiteListInvite())

}
