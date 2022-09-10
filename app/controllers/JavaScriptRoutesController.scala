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

        routes.javascript.AccountController.signUpForm,
        routes.javascript.AccountController.verifyWalletMnemonicsForm,
        routes.javascript.AccountController.checkUsernameAvailable,
        routes.javascript.AccountController.signInForm,
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
        routes.javascript.CollectionController.wishListCollectionPerPage,
        routes.javascript.CollectionController.wishListCollectionNFTs,
        routes.javascript.CollectionController.wishListCollectionNFTsPerPage,

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

        routes.javascript.ProfileController.viewPersonalProfile,
        routes.javascript.ProfileController.personalProfile,
        routes.javascript.ProfileController.createdWhitelists,
        routes.javascript.ProfileController.joinedWhitelists,
        routes.javascript.ProfileController.createWhitelistForm,
        routes.javascript.ProfileController.editWhitelistForm,
        routes.javascript.ProfileController.acceptInviteDetails,
        routes.javascript.ProfileController.acceptInvite,

      )
    ).as("text/javascript")

  }
}
