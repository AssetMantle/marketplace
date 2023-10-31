$("#formSubmitButton").hide();

function showSubmitButton() {
    $("#publicListingNextButton").hide();
    $("#formSubmitButton").show();
    $(".quantity").text($("#PUBLIC_LISTING_BUY_NFT_NUMBER").val());
    if ($("#MINT_NFT").is(":checked")) {
        $("#bondingAmountContainer").show();
    }
}

$("#PASSWORD").on("keyup", function () {
    if ($("#PASSWORD").val() !== "") {
        $("#publicListingNextButton").removeClass("disable");
    } else {
        $("#publicListingNextButton").addClass("disable");
    }
});

function computeInvoice() {
    let quantity = parseInt($("#PUBLIC_LISTING_BUY_NFT_NUMBER").val());
    let listedUnitCost = parseFloat($(".listedUnitCost").text().replace(",", ""));
    let listedAmount = listedUnitCost * quantity;
    let BondingUnitCost = 0.0;
    if ($("#MINT_NFT").is(":checked")) {
        BondingUnitCost = parseFloat($(".BondingUnitCost").text().replace(",", ""));
    }
    let BondingAmount = BondingUnitCost * quantity;
    let commissionRate = parseFloat($(".commissionRate").text());
    let commissionAmount = parseFloat((listedAmount * commissionRate) / 100);
    let subTotal = listedAmount + BondingAmount + commissionAmount;
    let discount = $(".discount").text();
    let invoiceTotal = subTotal - discount;
    $(".listedAmount").text(listedAmount);
    $(".BondingAmount").text(BondingAmount);
    $(".commissionAmount").text(commissionAmount);
    $(".subTotal").text(subTotal);
    $(".invoiceTotal").text(invoiceTotal);
}