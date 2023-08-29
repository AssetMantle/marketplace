function resetSellOrderPage() {
    componentResource("tradeSectionContent", jsRoutes.controllers.NFTController.sellOrders());
}

function resetYourOrderPage() {
    componentResource("tradeSectionContent", jsRoutes.controllers.NFTController.yourOrders());
}

function showTradeScreen(screenID) {
    switch (screenID) {
        case "sellOrder":
            $(".contentContainer .contentTitle .title .titleLabel").text("Sell Orders");
            resetSellOrderPage();
            break;
        case "yourOrder":
            $(".contentContainer .contentTitle .title .titleLabel").text("Your Orders");
            resetYourOrderPage();
            break;
    }
}