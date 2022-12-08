// Set Average Gas fees as Default
$('#GAS_PRICE option:eq(2)').prop('selected', true);

// Gas Price
route = jsRoutes.controllers.BlockchainTransactionController.gasTokenPrice();
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

// Gas Toggle Button
function setOption(element, value) {
    $(".toggleOption").removeClass("active");
    $(element).addClass("active");
    $('##GAS_PRICE').val(value);
}

// Set Available Balance
function setAvailableBalance(targetFieldId, amount) {
    $("#" + targetFieldId).val(amount);
}