let totalActiveContainer = 0;

function addProperty() {
    totalActiveContainer = $(".singlePropertyContainer.active").length;
    $("#COLLECTION_PROPERTY_" + totalActiveContainer).show();
    $("#COLLECTION_PROPERTY_" + totalActiveContainer).addClass("active");

    if (totalActiveContainer == 17) {
        $("#addMorePropertyButton").hide();
    } else {
        $("#addMorePropertyButton").show();
    }

    if(totalActiveContainer >= 0){
        $(".propertyModalButton .form-secondary-button").addClass("active");
    }else{
        $(".propertyModalButton .form-secondary-button").removeClass("active");
    }

    for(let i=0; i<totalActiveContainer; i++){
        let elementNameValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_NAME").val();
        let elementTypeValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text();
        let elementFixedValue = $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_FIXED_VALUE").val();

        let elementMutabilityValue = $("#COLLECTION_PROPERTIES_"+ i +"_COLLECTION_PROPERTY_MUTABLE input[name='COLLECTION_PROPERTIES["+ i +"].COLLECTION_PROPERTY_MUTABLE']:checked").val();
        let elementVisibilityValue = $("#COLLECTION_PROPERTIES_"+ i +"_COLLECTION_PROPERTY_META input[name='COLLECTION_PROPERTIES["+ i +"].COLLECTION_PROPERTY_META']:checked").val();
        let elementRequirementValue = $("#COLLECTION_PROPERTIES_"+ i +"_COLLECTION_PROPERTY_REQUIRED input[name='COLLECTION_PROPERTIES["+ i +"].COLLECTION_PROPERTY_REQUIRED']:checked").val();

        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardName").text(elementNameValue);
        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardType").text(elementTypeValue);
        $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardValue").text(elementFixedValue);
        (elementMutabilityValue === "MUTABLE") ? $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconMutability").addClass("active") : $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconMutability").removeClass("active");
        (elementVisibilityValue === "META") ? $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconVisibility").addClass("active") : $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconVisibility").removeClass("active");
        (elementRequirementValue === "REQUIRED") ? $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconRequirement").addClass("active") : $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyIconRequirement").removeClass("active");

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

    if(totalActiveContainer > 1){
        $(".propertyModalButton .form-secondary-button").addClass("active");
    }else{
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

    if (totalActiveContainer == 16) {
        $("#addMorePropertyButton").hide();
    } else {
        $("#addMorePropertyButton").show();
    }



    // Clear deleted property field
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_NAME").val("");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_TYPE").val("String");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text("String");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_FIXED_VALUE").val("");
    $("#COLLECTION_PROPERTIES_"+ containerId +"_COLLECTION_PROPERTY_MUTABLE input:radio[name='COLLECTION_PROPERTIES["+ containerId +"].COLLECTION_PROPERTY_MUTABLE']:first").prop('checked', true);
    $("#COLLECTION_PROPERTIES_"+ containerId +"_COLLECTION_PROPERTY_META input:radio[name='COLLECTION_PROPERTIES["+ containerId +"].COLLECTION_PROPERTY_META']:first").prop('checked', true);
    $("#COLLECTION_PROPERTIES_"+ containerId +"_COLLECTION_PROPERTY_REQUIRED input:radio[name='COLLECTION_PROPERTIES["+ containerId +"].COLLECTION_PROPERTY_REQUIRED']:first").prop('checked', true);

    for (let i = containerId; i < 17; i++) {
        let nextElementNameValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_NAME").val();
        let nextElementTypeValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").val();
        let nextElementCustomSelectTypeValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text();
        let nextElementFixedValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_FIXED_VALUE").val();
        let nextElementMutabilityValue = $("#COLLECTION_PROPERTIES_"+ (i + 1) +"_COLLECTION_PROPERTY_MUTABLE input[name='COLLECTION_PROPERTIES["+ (i + 1) +"].COLLECTION_PROPERTY_MUTABLE']:checked").val();
        let nextElementVisibilityValue = $("#COLLECTION_PROPERTIES_"+ (i + 1) +"_COLLECTION_PROPERTY_META input[name='COLLECTION_PROPERTIES["+ (i + 1) +"].COLLECTION_PROPERTY_META']:checked").val();
        let nextElementRequirementValue = $("#COLLECTION_PROPERTIES_"+ (i + 1) +"_COLLECTION_PROPERTY_REQUIRED input[name='COLLECTION_PROPERTIES["+ (i + 1) +"].COLLECTION_PROPERTY_REQUIRED']:checked").val();

        if (nextElementNameValue !== "") {
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_NAME").val(nextElementNameValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").val(nextElementTypeValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text(nextElementCustomSelectTypeValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_FIXED_VALUE").val(nextElementFixedValue);
            $("#COLLECTION_PROPERTIES_"+ i +"_COLLECTION_PROPERTY_MUTABLE input:radio[name='COLLECTION_PROPERTIES["+ i +"].COLLECTION_PROPERTY_MUTABLE'][value=" + nextElementMutabilityValue + "]").prop('checked', true);
            $("#COLLECTION_PROPERTIES_"+ i +"_COLLECTION_PROPERTY_META input:radio[name='COLLECTION_PROPERTIES["+ i +"].COLLECTION_PROPERTY_META'][value=" + nextElementVisibilityValue + "]").prop('checked', true);
            $("#COLLECTION_PROPERTIES_"+ i +"_COLLECTION_PROPERTY_REQUIRED input:radio[name='COLLECTION_PROPERTIES["+ i +"].COLLECTION_PROPERTY_REQUIRED'][value=" + nextElementRequirementValue + "]").prop('checked', true);

            $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardName").text(nextElementNameValue);
            $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardType").text(nextElementCustomSelectTypeValue);
            $("#COLLECTION_PROPERTY_" + i + " .propertyCardView .propertyCardValue").text(nextElementFixedValue);


        }
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_NAME").val("");
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").val("String");
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text("String");
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_FIXED_VALUE").val("");
        $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_FIXED_VALUE").closest(".optionInputField").show();
        $("#COLLECTION_PROPERTIES_"+ (i + 1) +"_COLLECTION_PROPERTY_MUTABLE input:radio[name='COLLECTION_PROPERTIES["+ (i + 1) +"].COLLECTION_PROPERTY_MUTABLE']:first").prop('checked', true);
        $("#COLLECTION_PROPERTIES_"+ (i + 1) +"_COLLECTION_PROPERTY_META input:radio[name='COLLECTION_PROPERTIES["+ (i + 1) +"].COLLECTION_PROPERTY_META']:first").prop('checked', true);
        $("#COLLECTION_PROPERTIES_"+ (i + 1) +"_COLLECTION_PROPERTY_REQUIRED input:radio[name='COLLECTION_PROPERTIES["+ (i + 1) +"].COLLECTION_PROPERTY_REQUIRED']:first").prop('checked', true);

        if(totalActiveContainer === 1){
            $("#COLLECTION_PROPERTY_" + i + " .propertyFormView .propertyAdvancedDetail .advancedDetailOption").hide();
            $("#COLLECTION_PROPERTY_" + i + " .propertyFormView .propertyAdvancedDetail .dropdownIcon").removeClass("active");
        }else{
            $("#COLLECTION_PROPERTY_" + (i + 1) + " .propertyFormView .propertyAdvancedDetail .advancedDetailOption").hide();
            $("#COLLECTION_PROPERTY_" + (i + 1) + " .propertyFormView .propertyAdvancedDetail .dropdownIcon").removeClass("active");
        }

        if(nextElementRequirementValue === "REQUIRED"){
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_FIXED_VALUE").closest(".optionInputField").hide();
        }else{
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_FIXED_VALUE").closest(".optionInputField").show();
        }

    }
    for(let j=0; j<totalActiveContainer-2; j++){
        $("#COLLECTION_PROPERTY_" + j + " .propertyFormView").hide();
        $("#COLLECTION_PROPERTY_" + j + " .propertyFormView").removeClass("active");
        $("#COLLECTION_PROPERTY_" + j + " .propertyCardView").show();
        $("#COLLECTION_PROPERTY_" + j + " .propertyCardView").addClass("active");
    }
}

function updateProperty(containerId){
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyFormView").show();
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyFormView").addClass("active");
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyCardView").hide();
    $("#COLLECTION_PROPERTY_" + containerId + " .propertyCardView").removeClass("active");
}

$(".requirementOption input[type='radio']").change(function (){
   let parentElement = $(this).closest(".requirementOption");
   if(this.value === "REQUIRED"){
       $(parentElement).find(".optionInputField").hide();
       $(parentElement).find(".optionInputField input").val("");
   }else{
       $(parentElement).find(".optionInputField").show();
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
function hideRadioGroupLabel(){
    $(".commonRadioGroup dl dt").text("");
}

function saveToDraft(){
    $("#SAVE_COLLECTION_DRAFT").attr("checked","checked");
    $("#FORM_DEFINE_COLLECTION_PROPERTIES_SUBMIT").click();
}

$("#formSubmitButton").prepend(`
                                <div class="d-flex gap-3 align-items-center me-3 propertyModalButton">
                                    <div class="tertiaryButton" onclick="">
                                        Skip
                                    </div>
                                    <button class="form-secondary-button active" type="button" onclick="saveToDraft()">Save Draft</button>
                                </div>
                                `);