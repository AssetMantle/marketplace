totalActiveContainer = 0;
totalNumberOfProperties = $("#totalProperties").text();

function addProperty() {
    totalActiveContainer = $(".singlePropertyContainer.active").length;
    $("#COLLECTION_PROPERTY_" + totalActiveContainer).show();
    $("#COLLECTION_PROPERTY_" + totalActiveContainer).addClass("active");

    if (totalActiveContainer === (totalNumberOfProperties - 1)) {
        $("#addMorePropertyButton").hide();
    } else {
        $("#addMorePropertyButton").show();
    }

    if (totalActiveContainer >= 0) {
        $(".propertyModalButton .form-secondary-button").addClass("active");
    } else {
        $(".propertyModalButton .form-secondary-button").removeClass("active");
    }

    for (let i = 0; i < totalActiveContainer; i++) {
        let elementNameValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_NAME").val();
        let elementTypeValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text();
        let elementFixedValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_DEFAULT_VALUE").val();

        let elementMutabilityValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_MUTABLE").is(":checked");
        let elementVisibilityValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_HIDE").is(":checked");
        let elementRequirementValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_SET_DEFAULT_VALUE").is(":checked");

        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardName").text(elementNameValue);
        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardType").text(elementTypeValue);
        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardValue").text(elementFixedValue);
        (elementMutabilityValue) ? $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconMutability").addClass("active") : $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconMutability").removeClass("active");
        (elementVisibilityValue) ? $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconVisibility").addClass("active") : $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconVisibility").removeClass("active");
        (elementRequirementValue) ? $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconRequirement").addClass("active") : $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconRequirement").removeClass("active");

        $("#COLLECTION_PROPERTY_" + i + " .propertyFormView").hide();
        $("#COLLECTION_PROPERTY_" + i + " .propertyFormView").removeClass("active");
        $("#COLLECTION_PROPERTY_" + i + " .propertyFormView .propertyAdvancedDetail .dropdownIcon").removeClass("active");
        $("#COLLECTION_PROPERTY_" + i + " .propertyFormView .propertyAdvancedDetail .advancedDetailOption").hide();
        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView").show();
        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView").addClass("active");
    }
}

function removeProperty(containerId) {
    totalActiveContainer = $(".singlePropertyContainer.active").length;

    if (totalActiveContainer > 1) {
        $(".propertyModalButton .form-secondary-button").addClass("active");
    } else {
        $(".propertyModalButton .form-secondary-button").removeClass("active");
    }

    // Show Form view form 2nd last property
    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 2) + " .propertyFormView").show();
    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 2) + " .propertyFormView").addClass("active");
    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 2) + " .propertyCardView").hide();
    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 2) + " .propertyCardView").removeClass("active");

    // Hide last property
    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 1)).hide();
    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 1)).removeClass("active");

    if (totalActiveContainer > totalNumberOfProperties) {
        $("#addMorePropertyButton").hide();
    } else {
        $("#addMorePropertyButton").show();
    }

    // Clear deleted property field
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_NAME").val("");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_TYPE option(1)").prop("selected",true);
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text("String");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_DEFAULT_VALUE").val("");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_MUTABLE").prop('checked', false);
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_HIDE").prop('checked', false);
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_SET_DEFAULT_VALUE").prop('checked', false);
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyTypeField").val("");
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyType").removeClass("active");
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyType.string").addClass("active");

    for (let i = containerId; i < totalNumberOfProperties-1; i++) {
        let nextElementNameValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_NAME").val();
        let nextElementTypeValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").val();
        let nextElementCustomSelectTypeValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text();
        let nextElementFixedValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_DEFAULT_VALUE").val();
        let nextElementMutabilityValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_MUTABLE").is(":checked");
        let nextElementVisibilityValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_HIDE").is(":checked");
        let nextElementRequirementValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_SET_DEFAULT_VALUE").is(":checked");

        if (nextElementNameValue !== "") {
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_NAME").val(nextElementNameValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").val(nextElementTypeValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text(nextElementCustomSelectTypeValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_DEFAULT_VALUE").val(nextElementFixedValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_MUTABLE").prop('checked', nextElementMutabilityValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_HIDE").prop('checked', nextElementVisibilityValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_SET_DEFAULT_VALUE").prop('checked', nextElementRequirementValue);

            $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardName").text(nextElementNameValue);
            $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardType").text(nextElementCustomSelectTypeValue);
            $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardValue").text(nextElementFixedValue);

            if (nextElementTypeValue === "BOOLEAN") {
                $("#COLLECTION_PROPERTY_" + i + " .propertyType").removeClass("active");
                $("#COLLECTION_PROPERTY_" + i + " .propertyType.boolean").addClass("active");
                $("#COLLECTION_PROPERTY_" + i + " .dropdown .currentSelected").text(nextElementFixedValue.slice(0, 1).toUpperCase() + nextElementFixedValue.slice(1));
            } else if (nextElementTypeValue === "DECIMAL") {
                $("#COLLECTION_PROPERTY_" + i + " .propertyType").removeClass("active");
                $("#COLLECTION_PROPERTY_" + i + " .propertyType.number").addClass("active");
                $("#COLLECTION_PROPERTY_" + i + " .propertyTypeField").val(nextElementFixedValue);
            } else {
                $("#COLLECTION_PROPERTY_" + i + " .propertyType").removeClass("active");
                $("#COLLECTION_PROPERTY_" + i + " .propertyType.string").addClass("active");
                $("#COLLECTION_PROPERTY_" + i + " .propertyTypeField").val(nextElementFixedValue);
            }

        }
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_NAME").val("");
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE option(1)").prop("selected",true);
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text("String");
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_DEFAULT_VALUE").val("");
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_DEFAULT_VALUE").closest(".optionInputField").show();
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_MUTABLE").prop('checked', false);
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_HIDE").prop('checked', false);
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_SET_DEFAULT_VALUE").prop('checked', false);

        $("#COLLECTION_PROPERTY_" + (i + 1) + " .propertyType").removeClass("active");
        $("#COLLECTION_PROPERTY_" + (i + 1) + " .propertyType.string").addClass("active");
        $("#COLLECTION_PROPERTY_" + (i + 1) + " .propertyTypeField").val("");

        if (totalActiveContainer === 1) {
            $("#COLLECTION_PROPERTY_" + i + " .propertyFormView .propertyAdvancedDetail .advancedDetailOption").hide();
            $("#COLLECTION_PROPERTY_" + i + " .propertyFormView .propertyAdvancedDetail .dropdownIcon").removeClass("active");
        } else {
            $("#COLLECTION_PROPERTY_" + (i + 1) + " .propertyFormView .propertyAdvancedDetail .advancedDetailOption").hide();
            $("#COLLECTION_PROPERTY_" + (i + 1) + " .propertyFormView .propertyAdvancedDetail .dropdownIcon").removeClass("active");
        }

        if (nextElementRequirementValue) {
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_DEFAULT_VALUE").closest(".optionInputField").show();
        } else {
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_DEFAULT_VALUE").closest(".optionInputField").hide();
        }

    }
    for (let j = 0; j < totalActiveContainer - 2; j++) {
        $("#COLLECTION_PROPERTY_" + j + " .propertyFormView").hide();
        $("#COLLECTION_PROPERTY_" + j + " .propertyFormView").removeClass("active");
        $("#COLLECTION_PROPERTY_" + j + " .propertyCardView").show();
        $("#COLLECTION_PROPERTY_" + j + " .propertyCardView").addClass("active");
    }
}

function updateProperty(containerId) {
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyFormView").show();
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyFormView").addClass("active");
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyCardView").hide();
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyCardView").removeClass("active");
}

$(".requirementOption .options input[type='checkbox']").change(function () {
    let parentElement = $(this).closest(".requirementOption");
    if ($(this).is(":checked")) {
        $(parentElement).find(".optionInputField").show();
    } else {
        $(parentElement).find(".optionInputField").hide();
        $(parentElement).find(".optionInputField input").val("");
    }
});

function openPropertyDetail(element) {
    $(element).toggleClass("active");
    $(element).closest(".singleProperty").find(".propertyDetail").slideToggle();
}

function openPropertyAdvancedOption(element) {
    $(element).toggleClass("active");
    $(element).closest(".propertyAdvancedDetail").find(".advancedDetailOption").slideToggle();
}

hideRadioGroupLabel();

function hideRadioGroupLabel() {
    $(".commonRadioGroup dl dt").text("");
}

function saveToDraft() {
    $("#SAVE_COLLECTION_DRAFT").attr("checked", "checked");
    $("#FORM_DEFINE_COLLECTION_PROPERTIES_SUBMIT").click();
}

function submitButton() {
    $("#FORM_DEFINE_COLLECTION_PROPERTIES_SUBMIT").click();
}

function hideSubmitButton() {
    $("#formSubmitButton").hide();
}

function setBooleanValue(valueFieldIndex, fieldValue) {
    $(`#COLLECTION_PROPERTIES_${valueFieldIndex}_COLLECTION_PROPERTY_DEFAULT_VALUE`).val(fieldValue);
}

function setNonBooleanValue(valueFieldIndex, e) {
    let fieldValue = e.target.value;
    $(`#COLLECTION_PROPERTIES_${valueFieldIndex}_COLLECTION_PROPERTY_DEFAULT_VALUE`).val(fieldValue);
}

$(".custom-select .custom-options .custom-option").on("click", function () {
    let selectedType = $(this).text();
    let parentContainer = $(this).closest(".singlePropertyContainer.active");

    parentContainer.find('.fixedValueField').val("");

    if (selectedType === "String") {
        parentContainer.find(".propertyType").removeClass("active");
        parentContainer.find(".propertyType.string").addClass("active");
    } else if (selectedType === "Number") {
        parentContainer.find(".propertyType").removeClass("active");
        parentContainer.find(".propertyType.number").addClass("active");
    } else if (selectedType === "Boolean") {
        parentContainer.find(".propertyType").removeClass("active");
        parentContainer.find(".propertyType.boolean").addClass("active");
    }
});

$("select.filledSelect").each((index,element)=>{
    let selectedFieldValue = $(element).val();
    if (selectedFieldValue === "DECIMAL"){
        selectedFieldValue = "NUMBER";
    }
    let valueToShow = selectedFieldValue.substr(0,1).toUpperCase()+selectedFieldValue.substr(1).toLowerCase();
    let selectFieldIndex = $(element).attr("data-index");
    $("#COLLECTION_PROPERTIES_" + selectFieldIndex + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text(valueToShow);
});

$(".selectedBooleanType").each((index,element)=>{
    let selectedFieldValue = $(element).text();
    let valueToShow = selectedFieldValue.substr(0,1).toUpperCase()+selectedFieldValue.substr(1).toLowerCase();
    $(element).text(valueToShow);
});

function setDefinePropertyBackButton(collectionId){
    $("#modalBackButton").attr("onclick", `getForm(jsRoutes.controllers.CollectionController.uploadCollectionDraftFilesForm('${collectionId}'))`);
}

flag1 = 0;
flag2 = 0;
$(document).ready(function () {
    $('#FORM_CREATE_COLLECTION_SUBMIT').addClass("disable");
});

$("#COLLECTION_NAME").on("keyup",function(){
    if($("#COLLECTION_NAME").val() !== ""){
        flag1 = 1;
    }else{
        flag1 = 0;
    }
    activeButton();
});

$("#COLLECTION_DESCRIPTION").on("keyup",function(){
    if($("#COLLECTION_DESCRIPTION").val() !== ""){
        flag2 = 1;
    }else{
        flag2 = 0;
    }
    activeButton();
});

function activeButton() {
    let termsCondition = document.getElementById("termsCondition");
    if(flag1 !== 0 && flag2 !== 0 && termsCondition.checked === true){
        $("#FORM_CREATE_COLLECTION_SUBMIT").removeClass("disable");
    } else {
        $("#FORM_CREATE_COLLECTION_SUBMIT").addClass("disable");
    }
}

function updateExplorerContainer(creatorId){
    if($(".createdCollectionsPerPage").length){
        $('.createdCollectionsPerPage').contents(':not(.createCollectionContainer)').remove();
        loadFirstCreatedCollections(creatorId);
    }
}