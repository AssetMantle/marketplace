function viewCollections(section) {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionsSection(section));
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewCollection(collectionId, showPublicListing = true) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionNFTs(collectionId));
    componentResource('rightContent', jsRoutes.controllers.CollectionController.topRightCard(collectionId, showPublicListing));
    if (showPublicListing) {
        checkAndPushState(jsRoutes.controllers.CollectionController.viewCollection(collectionId, showPublicListing).url, collectionId, "collectionForPublicListing");
    } else {
        checkAndPushState(jsRoutes.controllers.CollectionController.viewCollection(collectionId, showPublicListing).url, collectionId, "collectionForWhitelistSale");
    }
}

function viewWishListCollection(lastPart) {
    let accountId = lastPart.split("/")[0];
    let collectionId = lastPart.split("/")[2];
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.WishlistController.collectionNFTs(accountId, collectionId));
}

function viewCreatedCollection(lastPart) {
    let accountId = lastPart.split("/")[0];
    let collectionId = lastPart.split("/")[2];
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionNFTs(accountId, collectionId));
}

function viewNFT(nftId) {
    componentResource('leftContent', jsRoutes.controllers.NFTController.detailViewLeftCards(nftId));
    componentResource('centerContent', jsRoutes.controllers.NFTController.details(nftId));
    componentResource('rightContent', jsRoutes.controllers.NFTController.detailViewRightCards(nftId));
}

function viewSetting() {
    componentResource('centerContent', jsRoutes.controllers.SettingController.settings());
    $('#leftContent').html('');
}

function viewPublicListedCollections() {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.publicListedCollectionsSection());
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewProfile(lastPart) {
    let accountId = lastPart.split("/")[0];
    let activeTab = lastPart.split("/")[1];
    componentResource('leftContent', jsRoutes.controllers.ProfileController.profileInfoCard(accountId));
    componentResource('centerContent', jsRoutes.controllers.ProfileController.profile(accountId, activeTab));
    componentResource('rightContent', jsRoutes.controllers.ProfileController.profileAnalysisCard(accountId));
}