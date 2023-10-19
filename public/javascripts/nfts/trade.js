function resetSellOrderPage(nftId) {
    componentResource("tradeSectionContent", jsRoutes.controllers.NFTController.sellOrders(nftId, 1));
}

function resetYourOrderPage(nftId) {
    componentResource("tradeSectionContent", jsRoutes.controllers.NFTController.yourOrders(nftId, 1));
}

function showSellOrders(nftId) {
    $(".contentContainer .contentTitle .title .titleLabel").text("Sell Orders");
    resetSellOrderPage(nftId);
}

function showYourOrders(nftId) {
    $(".contentContainer .contentTitle .title .titleLabel").text("Your Orders");
    resetYourOrderPage(nftId);
}