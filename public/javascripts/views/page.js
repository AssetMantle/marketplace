function viewCollections() {
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionsList());
    $('#leftContent').html('');
}

function viewCollection(collectionId) {
    componentResource('leftContent', jsRoutes.controllers.CollectionController.info(collectionId));
    componentResource('centerContent', jsRoutes.controllers.CollectionController.collectionNFTs(collectionId));
}