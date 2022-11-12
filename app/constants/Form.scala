package constants

import controllers.routes
import play.api.mvc.Call
import play.api.routing.JavaScriptReverseRoute

case class Form(name: String, get: JavaScriptReverseRoute, post: Call) {
  val title: String = Seq("FORM", name, "TITLE").mkString(".")
  val subTitle: String = Seq("FORM", name, "SUBTITLE").mkString(".")
  val submit: String = Seq("FORM", name, "SUBMIT").mkString(".")
  val button: String = Seq("FORM", name, "BUTTON").mkString(".")
}

object Form {

  //AccountController
  val SIGN_UP: Form = Form("SIGN_UP", routes.javascript.AccountController.signUpForm, routes.AccountController.signUp())
  val VERIFY_WALLET_MNEMONICS: Form = Form("VERIFY_WALLET_MNEMONICS", routes.javascript.AccountController.verifyWalletMnemonicsForm, routes.AccountController.verifyWalletMnemonics())
  val SIGN_IN_WITH_CALLBACK: Form = Form("SIGN_IN_WITH_CALLBACK", routes.javascript.AccountController.signInWithCallbackForm, routes.AccountController.signInWithCallback())
  val FORGET_PASSWORD: Form = Form("FORGET_PASSWORD", routes.javascript.AccountController.forgetPasswordForm, routes.AccountController.forgetPassword())
  val CHANGE_PASSWORD: Form = Form("CHANGE_PASSWORD", routes.javascript.AccountController.changePasswordForm, routes.AccountController.changePassword())
  val SIGN_OUT: Form = Form("SIGN_OUT", routes.javascript.AccountController.signOutForm, routes.AccountController.signOut())
  val MIGRATE_WALLET_TO_KEY: Form = Form("MIGRATE_WALLET_TO_KEY", routes.javascript.AccountController.migrateWalletToKeyForm, routes.AccountController.migrateWalletToKey())

  val SEND_COIN: Form = Form("SEND_COIN", routes.javascript.BlockchainTransactionController.sendCoinForm, routes.BlockchainTransactionController.sendCoin())

  val CREATE_COLLECTION: Form = Form("CREATE_COLLECTION", routes.javascript.CollectionController.createForm, routes.CollectionController.create())
  val EDIT_COLLECTION: Form = Form("EDIT_COLLECTION", routes.javascript.CollectionController.editForm, routes.CollectionController.edit())
  val DEFINE_COLLECTION_PROPERTIES: Form = Form("DEFINE_COLLECTION_PROPERTIES", routes.javascript.CollectionController.definePropertiesForm, routes.CollectionController.defineProperties())

  val ADD_MANAGED_KEY: Form = Form("ADD_MANAGED_KEY", routes.javascript.SettingController.addManagedKeyForm, routes.SettingController.addManagedKey())
  val ADD_UNMANAGED_KEY: Form = Form("ADD_UNMANAGED_KEY", routes.javascript.SettingController.addUnmanagedKeyForm, routes.SettingController.addUnmanagedKey())
  val CHANGE_KEY_NAME: Form = Form("CHANGE_KEY_NAME", routes.javascript.SettingController.changeKeyNameForm, routes.SettingController.changeKeyName())
  val VIEW_MNEMONICS: Form = Form("VIEW_MNEMONICS", routes.javascript.SettingController.viewMnemonicsForm, routes.SettingController.viewMnemonics())
  val DELETE_KEY: Form = Form("DELETE_KEY", routes.javascript.SettingController.deleteKeyForm, routes.SettingController.deleteKey())
  val CHANGE_MANAGED_TO_UNMANAGED: Form = Form("CHANGE_MANAGED_TO_UNMANAGED", routes.javascript.SettingController.deleteKeyForm, routes.SettingController.deleteKey())

  val CREATE_WHITELIST: Form = Form("CREATE_WHITELIST", routes.javascript.WhitelistController.createWhitelistForm, routes.WhitelistController.createWhitelist())
  val EDIT_WHITELIST: Form = Form("EDIT_WHITELIST", routes.javascript.WhitelistController.editWhitelistForm, routes.WhitelistController.editWhitelist())
  val ACCEPT_WHITELIST_INVITE: Form = Form("ACCEPT_WHITELIST_INVITE", JavaScriptReverseRoute("", ""), routes.WhitelistController.acceptInvite())
  val LEAVE_WHITELIST: Form = Form("LEAVE_WHITELIST", JavaScriptReverseRoute("", ""), routes.WhitelistController.leaveWhitelist())

  val NFT_BASIC_DETAIL: Form = Form("NFT_BASIC_DETAIL", routes.javascript.NFTController.basicDetailsForm, routes.NFTController.basicDetails())
  val NFT_TAGS: Form = Form("NFT_TAGS", routes.javascript.NFTController.tagsForm, routes.NFTController.tags())
  val NFT_SET_PROPERTIES: Form = Form("NFT_SET_PROPERTIES", routes.javascript.NFTController.setPropertiesForm, routes.NFTController.setProperties())

  val NFT_WHITELIST_SALE: Form = Form("NFT_WHITELIST_SALE", routes.javascript.SaleController.createWhitelistSaleForm, routes.SaleController.createWhitelistSale())
  val NFT_WHITELIST_SALE_ACCEPT_OFFER: Form = Form("NFT_WHITELIST_SALE_ACCEPT_OFFER", routes.javascript.SaleController.acceptWhitelistSaleOfferForm, routes.SaleController.acceptWhitelistSaleOffer())

}
