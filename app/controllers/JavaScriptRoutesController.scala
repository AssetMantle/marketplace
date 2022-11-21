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
        routes.javascript.AccountController.signInWithCallbackForm,
        routes.javascript.AccountController.verifyWalletMnemonicsForm,
        routes.javascript.AccountController.checkUsernameAvailable,
        routes.javascript.AccountController.signOutForm,
        routes.javascript.AccountController.forgetPasswordForm,
        routes.javascript.AccountController.changePasswordForm,
        routes.javascript.AccountController.changeActiveKey,

        routes.javascript.CollectionController.viewCollections,
        routes.javascript.CollectionController.viewCollection,
        routes.javascript.CollectionController.collectionsSection,
        routes.javascript.CollectionController.collectionList,
        routes.javascript.CollectionController.collectionsPerPage,
        routes.javascript.CollectionController.collectionNFTs,
        routes.javascript.CollectionController.collectionNFTsPerPage,
        routes.javascript.CollectionController.info,
        routes.javascript.CollectionController.analysis,
        routes.javascript.CollectionController.createdSection,
        routes.javascript.CollectionController.createdCollectionPerPage,
        routes.javascript.CollectionController.createForm,
        routes.javascript.CollectionController.editForm,
        routes.javascript.CollectionController.uploadCollectionDraftFilesForm,
        routes.javascript.CollectionController.uploadCollectionDraftFileForm,
        routes.javascript.CollectionController.storeCollectionDraftFile,
        routes.javascript.CollectionController.uploadCollectionDraftFile,
        routes.javascript.CollectionController.uploadCollectionFilesForm,
        routes.javascript.CollectionController.uploadCollectionFileForm,
        routes.javascript.CollectionController.storeCollectionFile,
        routes.javascript.CollectionController.uploadCollectionFile,
        routes.javascript.CollectionController.definePropertiesForm,
        routes.javascript.CollectionController.deleteDraftForm,
        routes.javascript.CollectionController.deleteDraft,
        routes.javascript.CollectionController.countAccountNFTsNotOnSale,

        routes.javascript.WishlistController.wishlistSection,
        routes.javascript.WishlistController.collectionPerPage,
        routes.javascript.WishlistController.viewCollectionNFTs,
        routes.javascript.WishlistController.collectionNFTs,
        routes.javascript.WishlistController.collectionNFTsPerPage,
        routes.javascript.WishlistController.add,
        routes.javascript.WishlistController.delete,

        routes.javascript.NFTController.viewNFT,
        routes.javascript.NFTController.info,
        routes.javascript.NFTController.details,
        routes.javascript.NFTController.likesCounter,
        routes.javascript.NFTController.selectCollection,
        routes.javascript.NFTController.uploadNFTFileForm,
        routes.javascript.NFTController.storeNFTFile,
        routes.javascript.NFTController.uploadNFTFile,
        routes.javascript.NFTController.basicDetailsForm,
        routes.javascript.NFTController.setPropertiesForm,
        routes.javascript.NFTController.tagsForm,
        routes.javascript.NFTController.deleteDraftForm,
        routes.javascript.NFTController.deleteDraft,
        routes.javascript.NFTController.collectionInfo,
        routes.javascript.NFTController.ownedSection,
        routes.javascript.NFTController.ownedNFTsPerPage,

        routes.javascript.SettingController.viewSettings,
        routes.javascript.SettingController.settings,
        routes.javascript.SettingController.walletPopup,
        routes.javascript.SettingController.walletPopupKeys,
        routes.javascript.SettingController.addNewKey,
        routes.javascript.SettingController.addManagedKeyForm,
        routes.javascript.SettingController.addUnmanagedKeyForm,
        routes.javascript.SettingController.changeKeyNameForm,
        routes.javascript.SettingController.viewMnemonicsForm,
        routes.javascript.SettingController.deleteKeyForm,
        routes.javascript.SettingController.walletBalance,


        //        routes.javascript.ProfileController.viewOffers,
        //        routes.javascript.ProfileController.offers,

        routes.javascript.BlockchainTransactionController.gasTokenPrice,
        routes.javascript.BlockchainTransactionController.sendCoinForm,

        routes.javascript.ProfileController.viewProfile,
        routes.javascript.ProfileController.profile,
        routes.javascript.ProfileController.notificationPopup,
        routes.javascript.ProfileController.loadMoreNotifications,

        routes.javascript.SaleController.createCollectionSaleForm,

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
        routes.javascript.WhitelistController.listMembers,
        routes.javascript.WhitelistController.deleteMember,
        routes.javascript.WhitelistController.detail,

      )
    ).as("text/javascript")

  }
}
