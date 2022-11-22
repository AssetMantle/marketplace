salePrice = $("#salePrice").text();
saleMntlPrice = salePrice / microFactor;
$("#nft-mantle-price").text(saleMntlPrice);

route = jsRoutes.controllers.BlockchainTransactionController.gasTokenPrice();
$.ajax({
    url: route.url,
    type: route.type,
    async: true,
    statusCode: {
        200: function (data) {
            currentMntlPrice = data;
            $("#nft-dollar-price").text((saleMntlPrice * currentMntlPrice).toFixed(5) + "$");
        }
    }
});