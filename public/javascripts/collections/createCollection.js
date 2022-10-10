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
}

function removeProperty(containerId) {
    totalActiveContainer = $(".singlePropertyContainer.active").length;

    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 1)).hide();
    $("#COLLECTION_PROPERTY_" + (totalActiveContainer - 1)).removeClass("active");

    if (totalActiveContainer == 16) {
        $("#addMorePropertyButton").hide();
    } else {
        $("#addMorePropertyButton").show();
    }

    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_NAME").val("");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_TYPE").val("String");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text("String");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_FIXED_VALUE").val("");
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_REQUIRED").prop("checked", false);
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_MUTABLE").prop("checked", false);
    $("#COLLECTION_PROPERTIES_" + containerId + "_COLLECTION_PROPERTY_HIDE_VALUE").prop("checked", false);

    for (let i = containerId; i < 17; i++) {
        let nextElementNameValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_NAME").val();
        let nextElementTypeValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").val();
        let nextElementCustomSelectTypeValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text();
        let nextElementFixedValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_FIXED_VALUE").val();
        let nextElementRequiredValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_REQUIRED").is(':checked');
        let nextElementMutableValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_MUTABLE").is(':checked');
        let nextElementHideValue = $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_HIDE_VALUE").is(':checked');

        if (nextElementNameValue !== "") {
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_NAME").val(nextElementNameValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").val(nextElementTypeValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text(nextElementCustomSelectTypeValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_FIXED_VALUE").val(nextElementFixedValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_REQUIRED").prop("checked", nextElementRequiredValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_MUTABLE").prop("checked", nextElementMutableValue);
            $("#COLLECTION_PROPERTIES_" + i + "_COLLECTION_PROPERTY_HIDE_VALUE").prop("checked", nextElementHideValue);

            $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_NAME").val("");
            $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").val("String");
            $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_TYPE").closest("div").find(".custom-select-trigger").text("String");
            $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_FIXED_VALUE").val("");
            $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_REQUIRED").prop("checked", false);
            $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_MUTABLE").prop("checked", false);
            $("#COLLECTION_PROPERTIES_" + (i + 1) + "_COLLECTION_PROPERTY_HIDE_VALUE").prop("checked", false);
        }
    }
}

$(".checkbox-menu").on("change", "input[type='checkbox']", function () {
    $(this).closest("li").toggleClass("active", this.checked);
});

$('.dropdown-menu li').click(function (e) {
    e.stopPropagation();
});