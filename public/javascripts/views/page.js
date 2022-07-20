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

function viewSettings() {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.settingsPage());
    $('#leftContent').html('');
}