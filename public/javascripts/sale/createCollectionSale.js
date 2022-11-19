function onCollectionSelect(collectionId, accountId) {
    let route = jsRoutes.controllers.CollectionController.countAccountNFTs(collectionId, accountId);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $('#collectionOwnedNFTs').html(data);
            },
            400: function () {
            },
        }
    });
}