function viewCollections() {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionsList());
    $('#leftContent').html('');
}

function viewCollection(collectionId) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionNFTs(collectionId));
}

function viewNFT(nftId) {
    componentResource('leftContent', jsRoutes.controllers.NFTController.info(nftId));
    componentResource('centerContent', jsRoutes.controllers.NFTController.details(nftId));
}

function viewProfile() {
    componentResource('centerContent', jsRoutes.controllers.ProfileController.settings());
    $('#leftContent').html('');
}

function viewWishListCollection() {
    componentResource('centerContent', jsRoutes.controllers.ProfileController.wishListNFTs());
    $('#leftContent').html('');
}
