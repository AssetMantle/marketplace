function hideSubmitButton() {
    $("#formSubmitButton").hide();
}

function saveToDraftNft(){
    $("#SAVE_NFT_DRAFT").attr("checked","checked");
    $("#FORM_NFT_SET_PROPERTIES_SUBMIT").click();
}

function submitButtonNft() {
    $("#FORM_NFT_SET_PROPERTIES_SUBMIT").click();
}

function setBooleanValue(valueFieldIndex, fieldValue){
    $(`#NFT_PROPERTIES_${valueFieldIndex}_NFT_PROPERTY_VALUE`).val(fieldValue);
}

function setNonBooleanValue(valueFieldIndex, e){
    let fieldValue = e.target.value;
    $(`#NFT_PROPERTIES_${valueFieldIndex}_NFT_PROPERTY_VALUE`).val(fieldValue);
}

function setPropertyBackButton(collectionId, fileName){
    $("#modalBackButton").attr("onclick", `getForm(jsRoutes.controllers.NFTController.tagsForm('${collectionId}', '${fileName}'))`);
}