function onCollectionSelect(collectionId, accountId) {
    let route = jsRoutes.controllers.CollectionController.countCreatorNFTsNotOnSale(collectionId, accountId);
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

function setCreateSaleCollectionId(collectionId) {
    let userId = $(".loginUserName").text();
    onCollectionSelect(`${collectionId}`, `${userId}`);
}

function setSaleNFTValue(e) {
    let fieldValue = e.target.value;
    let totalOwnedNFT = $("#collectionOwnedNFTs").text();
    $(`#SALE_NFT_NUMBER`).val(fieldValue);
    if (fieldValue > totalOwnedNFT) {
        $("#FORM_COLLECTION_SALE_SUBMIT").addClass("disable");
    }
}

if ($("#SELECT_WHITELIST_ID").val() !== "") {
    let whitelistId = $("#SELECT_WHITELIST_ID").val();
    let selectedWhitelist =  $(`#SELECT_WHITELIST_ID option[value=${whitelistId}]`).text();
    console.log(selectedWhitelist);
    $("#SELECT_WHITELIST_ID").closest("div").find(".custom-select-trigger").text(selectedWhitelist);
}

function setOwnedNFTs(){
    let maxNFTs = $("#collectionOwnedNFTs").text();
    $("#saleNFTNumber").val(maxNFTs);
    $("#SALE_NFT_NUMBER").val(maxNFTs);
}