$("#formSubmitButton").hide();

function showSubmitButton() {
    $("#secondaryMarketNextButton").hide();
    $("#formSubmitButton").show();
}

$("#PASSWORD").on("keyup", function () {
    if ($("#PASSWORD").val() !== "") {
        $("#secondaryMarketNextButton").removeClass("disable");
    } else {
        $("#secondaryMarketNextButton").addClass("disable");
    }
});

function computeInvoice() {
    let quantity = parseInt($("#quantity").text());
    let listedUnitCost = parseFloat($(".listedUnitCost").text().replace(",", ""));
    let listedAmount = listedUnitCost * quantity;
    let commissionRate = parseFloat($(".commissionRate").text());
    let commissionAmount = parseFloat((listedAmount * commissionRate) / 100);
    let royaltyFees = parseFloat($(".royaltyFees").text());
    let royaltyAmount = parseFloat((listedAmount * royaltyFees) / 100);
    let subTotal = listedAmount + commissionAmount + royaltyAmount;
    let discount = $(".discount").text();
    let invoiceTotal = subTotal - discount;
    $(".listedAmount").text(listedAmount);
    $(".commissionAmount").text(commissionAmount);
    $(".royaltyAmount").text(royaltyAmount);
    $(".subTotal").text(subTotal);
    $(".invoiceTotal").text(invoiceTotal);
}