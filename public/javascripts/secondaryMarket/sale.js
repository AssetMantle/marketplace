function setSecondaryMarketSaleNFTValue(e) {
    let fieldValue = e.target.value;
    let totalOwnedNFT = $("#collectionOwnedNFTs").text();
    $(`#SELL_QUANTITY`).val(fieldValue);
    if (fieldValue > totalOwnedNFT) {
        $("#FORM_CREATE_SECONDARY_MARKET_SUBMIT").addClass("disable");
    }
}

function setSecondaryMarketOwnedNFTs(){
    let maxNFTs = $("#collectionOwnedNFTs").text();
    $("#secondaryMarketSaleNFTNumber").val(maxNFTs);
    $("#SELL_QUANTITY").val(maxNFTs);
}