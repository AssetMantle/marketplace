// Set Average Gas fees as Default
$('#GAS_PRICE option:eq(2)').prop('selected', true);

// Gas Price
var mntlPrice = 0.0;
getMNTLPrice().then(response => {
    mntlPrice = response;
    elementIndex = 0;
    const factor = 1000000;
    $("#GAS_PRICE option").each(function () {
        if ($(this).val()) {
            let totalGas = $(this).val() * $("#GAS_AMOUNT").val() / factor;
            $(".gasCharges").eq(elementIndex).text(totalGas + " MNTL");
            $(".gasChargesPrice").eq(elementIndex).text((totalGas * mntlPrice).toFixed(5) + "$");
            elementIndex++;
        }
    });
});

// Gas Toggle Button
function setOption(element, optionIndex) {
    $(".toggleOption").removeClass("active");
    $(element).addClass("active");
    $('#GAS_PRICE option:eq(' + optionIndex + ')').prop('selected', true);
}
