package controllers

import play.api.mvc._
import play.api.routing._
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}

@Singleton
class JavaScriptRoutesController @Inject()(messagesControllerComponents: MessagesControllerComponents)(implicit configuration: Configuration) extends AbstractController(messagesControllerComponents) {

  implicit val logger: Logger = Logger(this.getClass)

  def javascriptRoutes: Action[AnyContent] = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Assets.versioned,
        routes.javascript.PublicResourceController.versioned,

        routes.javascript.IndexController.index,

        routes.javascript.AccountController.signUpForm,
        routes.javascript.AccountController.signInWithCallbackForm,
        routes.javascript.AccountController.verifyWalletMnemonicsForm,
        routes.javascript.AccountController.checkUsernameAvailable,
        routes.javascript.AccountController.signOutForm,
        routes.javascript.AccountController.forgetPasswordForm,
        routes.javascript.AccountController.changePasswordForm,
        routes.javascript.AccountController.changeActiveKeyForm,

        routes.javascript.CollectionController.viewCollections,
        routes.javascript.CollectionController.viewCollection,
        routes.javascript.CollectionController.collectionsSection,
        routes.javascript.CollectionController.collectionList,
        routes.javascript.CollectionController.collectionsPerPage,
        routes.javascript.CollectionController.collectionNFTs,
        routes.javascript.CollectionController.collectionNFTsPerPage,
        routes.javascript.CollectionController.info,
        routes.javascript.CollectionController.topRightCard,
        routes.javascript.CollectionController.commonCardInfo,
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
        routes.javascript.CollectionController.countForCreatorNotForSell,
        routes.javascript.CollectionController.genesisTypeForm,

        routes.javascript.CollectedController.collectedSection,
        routes.javascript.CollectedController.collectionPerPage,
        routes.javascript.CollectedController.viewCollectionNFTs,
        routes.javascript.CollectedController.collectionNFTs,
        routes.javascript.CollectedController.collectionNFTsPerPage,
        routes.javascript.CollectedController.commonCardInfo,
        routes.javascript.CollectedController.topRightCard,

        routes.javascript.WishlistController.wishlistSection,
        routes.javascript.WishlistController.collectionPerPage,
        routes.javascript.WishlistController.viewCollectionNFTs,
        routes.javascript.WishlistController.collectionNFTs,
        routes.javascript.WishlistController.collectionNFTsPerPage,
        routes.javascript.WishlistController.add,
        routes.javascript.WishlistController.delete,

        routes.javascript.NFTController.viewNFT,
        routes.javascript.NFTController.details,
        routes.javascript.NFTController.marketListings,
        routes.javascript.NFTController.detailViewLeftCards,
        routes.javascript.NFTController.detailViewRightCards,
        routes.javascript.NFTController.info,
        routes.javascript.NFTController.collectionInfo,
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
        routes.javascript.NFTController.price,
        routes.javascript.NFTController.mintForm,
        routes.javascript.NFTController.transferForm,

        routes.javascript.SettingController.viewSettings,
        routes.javascript.SettingController.settings,
        routes.javascript.SettingController.addNewKey,
        routes.javascript.SettingController.addManagedKeyForm,
        routes.javascript.SettingController.addUnmanagedKeyForm,
        routes.javascript.SettingController.changeKeyNameForm,
        routes.javascript.SettingController.viewMnemonicsForm,
        routes.javascript.SettingController.deleteKeyForm,
        routes.javascript.SettingController.walletBalance,
        routes.javascript.SettingController.provisionAddressForm,

        //        routes.javascript.ProfileController.viewOffers,
        //        routes.javascript.ProfileController.offers,

        routes.javascript.ProfileController.viewProfile,
        routes.javascript.ProfileController.profile,
        routes.javascript.ProfileController.notificationPopup,
        routes.javascript.ProfileController.loadMoreNotifications,
        routes.javascript.ProfileController.markNotificationsRead,
        routes.javascript.ProfileController.countUnreadNotification,
        routes.javascript.ProfileController.profileInfoCard,
        routes.javascript.ProfileController.profileActivityCard,
        routes.javascript.ProfileController.profileAnalysisCard,
        routes.javascript.ProfileController.transactionsSection,
        routes.javascript.ProfileController.transactionsPerPage,

        routes.javascript.PublicListingController.viewCollections,
        routes.javascript.PublicListingController.collectionsSection,
        routes.javascript.PublicListingController.collectionsPerPage,
        routes.javascript.PublicListingController.viewCollection,
        routes.javascript.PublicListingController.collectionNFTs,
        routes.javascript.PublicListingController.collectionTopRightCard,
        routes.javascript.PublicListingController.createPublicListingForm,
        routes.javascript.PublicListingController.buyNFTForm,
        routes.javascript.PublicListingController.editForm,

        routes.javascript.SaleController.viewCollections,
        routes.javascript.SaleController.collectionsSection,
        routes.javascript.SaleController.collectionsPerPage,
        routes.javascript.SaleController.viewCollection,
        routes.javascript.SaleController.collectionNFTs,
        routes.javascript.SaleController.collectionTopRightCard,
        routes.javascript.SaleController.createCollectionSaleForm,
        routes.javascript.SaleController.buySaleNFTForm,

        routes.javascript.SecondaryMarketController.viewCollections,
        routes.javascript.SecondaryMarketController.viewCollection,
        routes.javascript.SecondaryMarketController.collectionNFTs,
        routes.javascript.SecondaryMarketController.collectionNFTsPerPage,
        routes.javascript.SecondaryMarketController.collectionTopRightCard,
        routes.javascript.SecondaryMarketController.collectionsSection,
        routes.javascript.SecondaryMarketController.collectionsPerPage,
        routes.javascript.SecondaryMarketController.createForm,
        routes.javascript.SecondaryMarketController.cancelForm,
        routes.javascript.SecondaryMarketController.buyForm,

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
        routes.javascript.WhitelistController.addFromNFTOwnersForm,
        routes.javascript.WhitelistController.deleteForm,

        routes.javascript.WalletController.walletPopup,
        routes.javascript.WalletController.wrappedTokenBalance,
        routes.javascript.WalletController.walletPopupKeys,
        routes.javascript.WalletController.unwrapTokenForm,
        routes.javascript.WalletController.sendCoinForm,
        routes.javascript.WalletController.gasTokenPrice,
        routes.javascript.WalletController.balance,
        routes.javascript.WalletController.wrapCoinForm,

      )
    ).as("text/javascript")

  }
}
