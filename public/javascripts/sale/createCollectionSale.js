function onCollectionSelect(collectionId, accountId) {
    let route = jsRoutes.controllers.CollectionController.countAccountNFTsNotOnSale(collectionId, accountId);
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

$("#SELECT_COLLECTION_ID_field .custom-option").on("click", function() {
    let collectionId = $(this).data("value");
    let userId = $(".userName").text();
    componentResource('collectionOwnedNFTs', jsRoutes.controllers.CollectionController.countAccountNFTs(`${collectionId}`,`${userId}`))
});

function setSaleNFTValue(e){
    let fieldValue = e.target.value;
    let totalOwnedNFT = $("#collectionOwnedNFTs").text();
    $(`#SALE_NFT_NUMBER`).val(fieldValue);
    if(fieldValue > totalOwnedNFT){
        $("#FORM_COLLECTION_SALE_SUBMIT").addClass("disable");
    }
}