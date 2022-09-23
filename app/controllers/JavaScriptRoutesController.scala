package controllers

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

        routes.javascript.IndexController.index,
        routes.javascript.IndexController.pageNotFound,

        routes.javascript.AccountController.signUpForm,
        routes.javascript.AccountController.signInWithCallbackForm,
        routes.javascript.AccountController.verifyWalletMnemonicsForm,
        routes.javascript.AccountController.checkUsernameAvailable,
        routes.javascript.AccountController.signOutForm,
        routes.javascript.AccountController.forgetPasswordForm,
        routes.javascript.AccountController.changePasswordForm,
        routes.javascript.AccountController.changeActiveKey,

        routes.javascript.CollectionController.viewCollections,
        routes.javascript.CollectionController.viewCollection,
        routes.javascript.CollectionController.collectionsList,
        routes.javascript.CollectionController.collectionsPerPage,
        routes.javascript.CollectionController.collectionFile,
        routes.javascript.CollectionController.collectionNFTs,
        routes.javascript.CollectionController.collectionNFTsPerPage,
        routes.javascript.CollectionController.info,

        routes.javascript.WishlistController.wishlistSection,
        routes.javascript.WishlistController.collectionPerPage,
        routes.javascript.WishlistController.viewCollectionNFTs,
        routes.javascript.WishlistController.collectionNFTs,
        routes.javascript.WishlistController.collectionNFTsPerPage,

        routes.javascript.NFTController.viewNFT,
        routes.javascript.NFTController.info,
        routes.javascript.NFTController.details,
        routes.javascript.NFTController.file,
        routes.javascript.NFTController.addToWishList,
        routes.javascript.NFTController.deleteFromWishList,
        routes.javascript.NFTController.likesCounter,

        routes.javascript.SettingController.viewSettings,
        routes.javascript.SettingController.settings,
        routes.javascript.SettingController.walletPopup,
        routes.javascript.SettingController.addNewKey,
        routes.javascript.SettingController.addManagedKeyForm,
        routes.javascript.SettingController.addUnmanagedKeyForm,
        routes.javascript.SettingController.changeKeyNameForm,
        routes.javascript.SettingController.viewMnemonicsForm,
        routes.javascript.SettingController.deleteKeyForm,
        routes.javascript.SettingController.walletBalance,
        routes.javascript.SettingController.viewWishList,
        routes.javascript.SettingController.wishList,
        routes.javascript.SettingController.wishListNFTs,

        //        routes.javascript.ProfileController.viewOffers,
        //        routes.javascript.ProfileController.offers,

        routes.javascript.BlockchainTransactionController.gasTokenPrice,
        routes.javascript.BlockchainTransactionController.sendCoinForm,

        routes.javascript.ProfileController.viewProfile,
        routes.javascript.ProfileController.profile,

        routes.javascript.WhitelistController.whitelistSection,
        routes.javascript.WhitelistController.createdWhitelists,
        routes.javascript.WhitelistController.createdWhitelistsPerPage,
        routes.javascript.WhitelistController.whitelistTotalMembers,
        routes.javascript.WhitelistController.joinedWhitelists,
        routes.javascript.WhitelistController.joinedWhitelistsPerPage,
        routes.javascript.WhitelistController.createWhitelistForm,
        routes.javascript.WhitelistController.editWhitelistForm,
        routes.javascript.WhitelistController.acceptInviteDetails,
        routes.javascript.WhitelistController.acceptInvite,
        routes.javascript.WhitelistController.leaveWhitelistDetails,
        routes.javascript.WhitelistController.leaveWhitelist,

      )
    ).as("text/javascript")

  }
}
