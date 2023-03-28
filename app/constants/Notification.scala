package constants

import constants.Notification._
import controllers.routes
import play.api.routing.JavaScriptReverseRoute

case class Notification(name: String, sendEmail: Boolean, sendPushNotification: Boolean, sendSMS: Boolean, route: Option[JavaScriptReverseRoute] = None) {

  def email: Option[Email] = if (sendEmail) Option(Email(name)) else None

  def pushNotification: Option[PushNotification] = if (sendPushNotification) Option(PushNotification(name)) else None

  def sms: Option[SMS] = if (sendSMS) Option(SMS(name)) else None

}

object Notification {

  case class SMS(name: String) {
    def message: String = Seq("SMS", name, "MESSAGE").mkString(".")
  }

  case class PushNotification(name: String) {
    def title: String = Seq("PUSH_NOTIFICATION", name, "TITLE").mkString(".")

    def message: String = Seq("PUSH_NOTIFICATION", name, "MESSAGE").mkString(".")
  }

  case class Email(name: String) {
    def subject: String = Seq("EMAIL", name, "SUBJECT").mkString(".")

    def message: String = Seq("EMAIL", name, "MESSAGE").mkString(".")
  }

  val LOGIN: Notification = Notification("LOGIN", sendEmail = false, sendPushNotification = true, sendSMS = false)
  val LOG_OUT: Notification = Notification("LOG_OUT", sendEmail = false, sendPushNotification = true, sendSMS = false)

  val COLLECTION_CREATED: Notification = Notification("COLLECTION_CREATED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.CollectionController.viewCollection))
  val NFT_CREATED: Notification = Notification("NFT_CREATED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))
  val NFT_GIFTED: Notification = Notification("NFT_GIFTED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))

  val SALE_ON_WHITELIST: Notification = Notification("SALE_ON_WHITELIST", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.ProfileController.viewProfile))
  val SELLER_BUY_NFT_SUCCESSFUL_FROM_SALE: Notification = Notification("SELLER_BUY_NFT_SUCCESSFUL_FROM_SALE", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val BUYER_BUY_NFT_SUCCESSFUL_FROM_SALE: Notification = Notification("BUYER_BUY_NFT_SUCCESSFUL_FROM_SALE", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.ProfileController.viewProfile))
  val BUYER_BUY_NFT_FAILED: Notification = Notification("BUYER_BUY_NFT_FAILED", sendEmail = false, sendPushNotification = true, sendSMS = false)

  val PUBLIC_LISTING_ON_COLLECTION: Notification = Notification("PUBLIC_LISTING_ON_COLLECTION", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.ProfileController.viewProfile))
  val SELLER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING: Notification = Notification("SELLER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val BUYER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING: Notification = Notification("BUYER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.ProfileController.viewProfile))

  val NFT_MINT_SUCCESSFUL: Notification = Notification("NFT_MINT_SUCCESSFUL", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))
  val NFT_MINT_FAILED: Notification = Notification("NFT_MINT_FAILED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))

  val SECONDARY_MARKET_CREATION_SUCCESSFUL: Notification = Notification("SECONDARY_MARKET_CREATION_SUCCESSFUL", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))
  val SECONDARY_MARKET_CREATION_FAILED: Notification = Notification("SECONDARY_MARKET_CREATION_FAILED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))
  val CANCEL_ORDER_SUCCESSFUL: Notification = Notification("CANCEL_ORDER_SUCCESSFUL", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val CANCEL_ORDER_FAILED: Notification = Notification("CANCEL_ORDER_FAILED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val SELLER_TAKE_ORDER_SUCCESSFUL: Notification = Notification("SELLER_TAKE_ORDER_SUCCESSFUL", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val BUYER_TAKE_ORDER_SUCCESSFUL: Notification = Notification("BUYER_TAKE_ORDER_SUCCESSFUL", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.ProfileController.viewProfile))
  val BUYER_TAKE_ORDER_FAILED: Notification = Notification("BUYER_TAKE_ORDER_FAILED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)

  val ADDRESS_PROVISIONED_SUCCESSFULLY: Notification = Notification("ADDRESS_PROVISIONED_SUCCESSFULLY", sendEmail = false, sendPushNotification = false, sendSMS = false, route = Option(routes.javascript.SettingController.viewSettings))
  val ADDRESS_PROVISIONED_FAILED: Notification = Notification("ADDRESS_PROVISIONED_FAILED", sendEmail = false, sendPushNotification = false, sendSMS = false, route = Option(routes.javascript.SettingController.viewSettings))
  val ADDRESS_UNPROVISIONED_SUCCESSFULLY: Notification = Notification("ADDRESS_UNPROVISIONED_SUCCESSFULLY", sendEmail = false, sendPushNotification = false, sendSMS = false, route = Option(routes.javascript.SettingController.viewSettings))
  val ADDRESS_UNPROVISIONED_FAILED: Notification = Notification("ADDRESS_UNPROVISIONED_FAILED", sendEmail = false, sendPushNotification = false, sendSMS = false, route = Option(routes.javascript.SettingController.viewSettings))

  val UNWRAPPED_TOKEN_SUCCESSFULLY: Notification = Notification("UNWRAPPED_TOKEN_SUCCESSFULLY", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val UNWRAPPED_TOKEN_FAILED: Notification = Notification("UNWRAPPED_TOKEN_FAILED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)

  val SEND_COIN_FROM_ACCOUNT: Notification = Notification("SEND_COIN_FROM_ACCOUNT", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val SEND_COIN_TO_ACCOUNT: Notification = Notification("SEND_COIN_TO_ACCOUNT", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val IBC_RECEIVED_TOKEN: Notification = Notification("IBC_RECEIVED_TOKEN", sendEmail = false, sendPushNotification = true, sendSMS = false, route = None)
  val NFT_MINTING_FEE_TRANSACTION_SUCCESSFUL: Notification = Notification("NFT_MINTING_FEE_TRANSACTION_SUCCESSFUL", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))
  val NFT_MINTING_FEE_TRANSACTION_FAILED: Notification = Notification("NFT_MINTING_FEE_TRANSACTION_FAILED", sendEmail = false, sendPushNotification = true, sendSMS = false, route = Option(routes.javascript.NFTController.viewNFT))
}
