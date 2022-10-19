function saveToDraft(){
    $("#SAVE_NFT_DRAFT").attr("checked","checked");
    $("#FORM_NFT_SET_PROPERTIES_SUBMIT").click();
}

$("#FORM_NFT_SET_PROPERTIES_SUBMIT").parent().prepend(`
                                <div class="d-flex gap-3 align-items-center me-3 propertyModalButton">
                                    <button class="form-secondary-button active" type="button" onclick="saveToDraft()">Save Draft</button>
                                </div>
                                `);