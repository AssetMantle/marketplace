
// Gas Price
route = jsRoutes.controllers.WalletController.gasTokenPrice();
$.ajax({
    url: route.url,
    type: route.type,
    async: true,
    statusCode: {
        200: function (mntlPrice) {
            for (let i = 1; i < 4; i++) {
                let totalGas = $('#gasCharges' + i).val() * $("#GAS_AMOUNT").val() / microFactor;
                $('#gasChargesPrice' + i).text("$" + (totalGas * mntlPrice).toFixed(5));
            }
        }
    }
});

// Set Available Balance
function setAvailableBalance(targetFieldId, amount) {
    $("#" + targetFieldId).val(amount);
}