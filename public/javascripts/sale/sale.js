function saleOptions() {
    let myModal = new bootstrap.Modal($("#commonModal"), {});
    let data = $("#saleOptionModal").html();
    $("#commonModal").addClass('active');
    $("#modal-content").html(data);
    myModal.show();
}

function setNextButton(addressType, collectionId){
    $("#nextBtn").removeClass("disable");
    if(addressType === "public"){
        $("#nextBtn").attr("onclick",`getForm(jsRoutes.controllers.PublicListingController.createPublicListingForm('${collectionId}'))`)
    }else{
        $("#nextBtn").attr("onclick",`getForm(jsRoutes.controllers.SaleController.createCollectionSaleForm(undefined, '${collectionId}'))`)
    }
}