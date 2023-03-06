function viewCollections() {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionsSection());
    checkAndPushState(jsRoutes.controllers.CollectionController.viewCollections().url, "", "collections");
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewEarlyAccess() {
    componentResource('centerContent', jsRoutes.controllers.PublicListingController.collectionsSection());
    checkAndPushState(jsRoutes.controllers.PublicListingController.viewCollections().url, "", "earlyAccess");
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewLaunchpad() {
    componentResource('centerContent', jsRoutes.controllers.SaleController.collectionsSection());
    checkAndPushState(jsRoutes.controllers.SaleController.viewCollections().url, "", "launchpad");
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewSecondaryMarketCollections() {
    componentResource('centerContent', jsRoutes.controllers.SecondaryMarketController.collectionsSection());
    checkAndPushState(jsRoutes.controllers.SecondaryMarketController.viewCollections().url, "", "secondaryMarket");
    $('#leftContent').html('');
    $('#rightContent').html('');
}

function viewCollection(collectionId) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionNFTs(collectionId));
    componentResource('rightContent', jsRoutes.controllers.CollectionController.topRightCard(collectionId));
    checkAndPushState(jsRoutes.controllers.CollectionController.viewCollection(collectionId).url, collectionId, "collection");
}

function viewLaunchpadCollection(collectionId) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.SaleController.collectionNFTs(collectionId));
    componentResource('rightContent', jsRoutes.controllers.SaleController.collectionTopRightCard(collectionId));
    checkAndPushState(jsRoutes.controllers.SaleController.viewCollection(collectionId).url, collectionId, "launchpadCollection");
}


function viewEarlyAccessCollection(collectionId) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.PublicListingController.collectionNFTs(collectionId));
    componentResource('rightContent', jsRoutes.controllers.PublicListingController.collectionTopRightCard(collectionId));
    checkAndPushState(jsRoutes.controllers.PublicListingController.viewCollection(collectionId).url, collectionId, "earlyAccessCollection");
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
    checkAndPushState(jsRoutes.controllers.NFTController.viewNFT(nftId).url, nftId, "nft");
}

function viewSetting() {
    componentResource('centerContent', jsRoutes.controllers.SettingController.settings());
    checkAndPushState(jsRoutes.controllers.SettingController.viewSettings().url, '', "setting");
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

function changeProfileStateOnSwitcher(accountId, section) {
    checkAndPushState(jsRoutes.controllers.ProfileController.viewProfile(accountId, section).url, (accountId + '/' + section), 'profile');
}