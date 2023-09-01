function checkNoCollection() {
    if ($(".singleListedCollection").length === 0) {
        $('#noListedCollectionsPerPage').removeClass("hidden");
    }
}

function listedCollectionCardInfo(collectionId, accountID) {
    let route = jsRoutes.controllers.SecondaryMarketController.listedCollectionCardInfo(collectionId, accountID);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                let totalData = data.split("|");
                $('#counCollectionNFTs_' + collectionId).html(totalData[0]);
            },
            400: function (data) {
                console.log(data.responseText)
            }
        }
    });
}