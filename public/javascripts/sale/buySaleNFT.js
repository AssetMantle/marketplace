$("#formSubmitButton").hide();

function showSubmitButton() {
    $("#launchpadNextButton").hide();
    $("#formSubmitButton").show();
    $(".launchpadBuyQuantity").text($("#SALE_BUY_NFT_NUMBER").val());
}

$("#PASSWORD").on("keyup",function(){
    if($("#PASSWORD").val() !== ""){
        $("#launchpadNextButton").removeClass("disable");
    }else{
        $("#launchpadNextButton").addClass("disable");
    }
});