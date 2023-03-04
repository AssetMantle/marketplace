function viewCollections(section) {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionsSection(section));
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewCollection(collectionId, status) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionNFTs(collectionId));
    componentResource('rightContent', jsRoutes.controllers.CollectionController.topRightCard(collectionId, status));
    if (status === 1) {
        checkAndPushState(jsRoutes.controllers.CollectionController.viewCollection(collectionId, 1).url, collectionId, "collectionForPublicListing");
    }
    if (status === 2) {
        checkAndPushState(jsRoutes.controllers.CollectionController.viewCollection(collectionId, 2).url, collectionId, "collectionForWhitelistSale");
    }
}

function viewMarketCollection(collectionId) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.SecondaryMarketController.collectionNFTs(collectionId));
    componentResource('rightContent', jsRoutes.controllers.SecondaryMarketController.collectionTopRightCard(collectionId));
    checkAndPushState(jsRoutes.controllers.SecondaryMarketController.viewCollection(collectionId).url, collectionId, "marketCollection");
}

function viewCollectedCollection(lastPart) {
    let accountId = lastPart.split("/")[0];
    let collectionId = lastPart.split("/")[2];
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectedController.collectionNFTs(accountId, collectionId));
    componentResource('rightContent', jsRoutes.controllers.CollectedController.topRightCard(collectionId, accountId));
}


function viewWishListCollection(lastPart) {
    let accountId = lastPart.split("/")[0];
    let collectionId = lastPart.split("/")[2];
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.WishlistController.collectionNFTs(accountId, collectionId));
    $('#rightContent').html('');
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
    componentResource('centerContent', jsRoutes.controllers.PublicListingController.publicListedCollectionsSection());
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewWhitelistSaleCollections() {
    componentResource('centerContent', jsRoutes.controllers.SaleController.whitelistSaleCollectionsSection());
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewSecondaryMarketCollections() {
    componentResource('centerContent', jsRoutes.controllers.SecondaryMarketController.collectionsSection());
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