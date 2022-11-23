route = jsRoutes.controllers.BlockchainTransactionController.gasTokenPrice();
$.ajax({
    url: route.url,
    type: route.type,
    async: true,
    statusCode: {
        200: function (data) {
            let salePrice = $("#nftMantlePrice").text();
            let currentMntlPrice = data;
            $("#nftDollarPrice").text((salePrice * currentMntlPrice).toFixed(5) + "$");
        }
    }
});