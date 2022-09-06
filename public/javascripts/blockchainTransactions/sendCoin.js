// Set Average Gas fees as Default
$('#GAS_PRICE option:eq(2)').prop('selected', true);

// Gas Price
const microFactor = 1000000;
let route = jsRoutes.controllers.BlockchainTransactionController.gasTokenPrice();
$.ajax({
    url: route.url,
    type: route.type,
    async: true,
    statusCode: {
        200: function (data) {
            const mntlPrice = data
            let gasElementIndex = 0;
            $("#GAS_PRICE option").each(function () {
                if ($(this).val()) {
                    let totalGas = $(this).val() * $("#GAS_AMOUNT").val() / microFactor;
                    $(".gasCharges").eq(gasElementIndex).text(totalGas + " MNTL");
                    $(".gasChargesPrice").eq(gasElementIndex).text((totalGas * mntlPrice).toFixed(5) + "$");
                    gasElementIndex++;
                }
            });
        }
    }
});
// Gas Toggle Button
function setOption(element, optionIndex) {
    $(".toggleOption").removeClass("active");
    $(element).addClass("active");
    $('#GAS_PRICE option:eq(' + optionIndex + ')').prop('selected', true);
}
