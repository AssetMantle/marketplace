function viewCollections(section) {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionsList(section));
    $('#leftContent').html('');
}

function viewCollection(collectionId) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionNFTs(collectionId));
}

function viewWishListCollection(lastPart) {
    let accountId = lastPart.split("/")[0];
    let collectionId = lastPart.split("/")[2];
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.WishlistController.collectionNFTs(accountId, collectionId));
}

function viewNFT(nftId) {
    componentResource('leftContent', jsRoutes.controllers.NFTController.info(nftId));
    componentResource('centerContent', jsRoutes.controllers.NFTController.details(nftId));
}

function viewSetting() {
    componentResource('centerContent', jsRoutes.controllers.SettingController.settings());
    $('#leftContent').html('');
}

function viewProfile(accountId) {
    componentResource('centerContent', jsRoutes.controllers.ProfileController.profile(accountId));
    $('#leftContent').html('');
}