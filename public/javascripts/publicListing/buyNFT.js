$("#formSubmitButton").hide();

function showSubmitButton() {
    $("#publicListingNextButton").hide();
    $("#formSubmitButton").show();
    $(".publicListingBuyQuantity").text($("#PUBLIC_LISTING_BUY_NFT_NUMBER").val());
}

$("#PASSWORD").on("keyup",function(){
    if($("#PASSWORD").val() !== ""){
        $("#publicListingNextButton").removeClass("disable");
    }else{
        $("#publicListingNextButton").addClass("disable");
    }
});