function checkNoCollection() {
    if ($(".singleCollectedCollection").length === 0) {
        $('#noCollectedCollectionsPerPage').removeClass("hidden");
    }
}

function collectedCollectionCardInfo(collectionId, accountID) {
    let route = jsRoutes.controllers.CollectedController.collectionCardInfo(collectionId, accountID);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                let totalData = data.split("|");
                $('#counCollectionNFTs_' + collectionId).html(totalData[0]);
                $('#collectionNFTsPrice_' + collectionId).html(totalData[1]);
            },
            400: function (data) {
                console.log(data.responseText)
            }
        }
    });
}