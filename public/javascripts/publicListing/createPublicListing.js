function onCollectionSelect(collectionId, accountId) {
    let route = jsRoutes.controllers.CollectionController.countForCreatorNotForSell(collectionId, accountId);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $('#collectionOwnedNFTs').html(data);
            },
            400: function (data) {
                console.log(data.responseText);
            },
        }
    });
}

$("#SELECT_COLLECTION_ID_field .custom-option").on("click", function () {
    let collectionId = $(this).data("value");
    let userId = $(".loginUserName").text();
    onCollectionSelect(`${collectionId}`, `${userId}`);
});

function setCreatePublicListingCollectionId(collectionId) {
    let userId = $(".loginUserName").text();
    onCollectionSelect(`${collectionId}`, `${userId}`);
    let selectedCollectionId = $("#SELECT_COLLECTION_ID").val();
    let selectedCollection =  $(`#SELECT_COLLECTION_ID option[value=${selectedCollectionId}]`).text();
    $("#SELECT_COLLECTION_ID").closest("div").find(".custom-select-trigger").text(selectedCollection);
}

function setPublicListingNFTValue(e) {
    let fieldValue = e.target.value;
    let totalOwnedNFT = $("#collectionOwnedNFTs").text();
    $(`#PUBLIC_LISTING_NFT_NUMBER`).val(fieldValue);
    if (fieldValue > totalOwnedNFT) {
        $("#FORM_PUBLIC_LISTING_SUBMIT").addClass("disable");
    }
}

function setOwnedNFTs(){
    let maxNFTs = $("#collectionOwnedNFTs").text();
    $("#publicListingNFTNumber").val(maxNFTs);
    $("#PUBLIC_LISTING_NFT_NUMBER").val(maxNFTs);
}