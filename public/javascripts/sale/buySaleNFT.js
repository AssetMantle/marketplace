$("#formSubmitButton").hide();

function showSubmitButton() {
    $("#launchpadNextButton").hide();
    $("#formSubmitButton").show();
    $(".quantity").text($("#SALE_BUY_NFT_NUMBER").val());
}

$("#PASSWORD").on("keyup",function(){
    if($("#PASSWORD").val() !== ""){
        $("#launchpadNextButton").removeClass("disable");
    }else{
        $("#launchpadNextButton").addClass("disable");
    }
});

function computeInvoice(){
    let quantity = parseInt($("#SALE_BUY_NFT_NUMBER").val());
    let listedUnitCost = parseFloat($(".listedUnitCost").text().replace(",",""));
    let listedAmount = listedUnitCost * quantity;
    let BondingUnitCost = parseFloat($(".BondingUnitCost").text().replace(",",""));
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