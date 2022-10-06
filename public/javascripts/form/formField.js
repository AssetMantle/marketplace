function commonDescriptionInputOnKeyPress(fieldId, maximumCharacters) {
    let inputField = $("#" + fieldId);
    if (inputField.val().length > maximumCharacters) {
        inputField.val(inputField.val().substring(0, maximumCharacters));
        return false;
    } else {
        $("#remainingCharacter_" + fieldId).text(maximumCharacters - inputField.val().length + "/" + maximumCharacters);
    }
}

function commonInputEpochTime(epochFieldName, fieldName, showCurrentTime) {
    $('#' + epochFieldName).daterangepicker({
        autoUpdateInput: showCurrentTime,
        timePicker: true,
        singleDatePicker: true,
        startDate: moment().startOf('minute'),
        drops: 'up',
        locale: {
            format: 'MM/DD/YYYY hh:mm A',
            cancelLabel: 'Clear',
            applyLabel: 'Ok'
        }
    });

    $('#' + epochFieldName).on('apply.daterangepicker', function (ev, picker) {
        $(this).val(picker.startDate.format('MM/DD/YYYY hh:mm A'));
        let dateStr = picker.startDate.format('MM/DD/YYYY hh:mm A');
        let date = new Date(dateStr);
        const seconds = Math.floor(date.getTime() / 1000);
        $("#" + fieldName).val(seconds);
        $("#" + fieldName).trigger("keyup");
    });

    $('#' + epochFieldName).on('cancel.daterangepicker', function (ev, picker) {
        $(this).val('');
        $("#" + fieldName).val('');
        $("#" + fieldName).trigger("keyup");
    });
}

function setCurrentTimeForEpochField(epochFieldName, fieldName) {
    let dateStr = moment().format('MM/DD/YYYY hh:mm A');
    $('#' + epochFieldName).val(dateStr);
    let date = new Date(dateStr);
    const seconds = Math.floor(date.getTime() / 1000);
    $("#" + fieldName).val(seconds);
}

function clearEpochField(fieldName, showCurrentTime) {
    if (!showCurrentTime) {
        $("#epoch_" + fieldName).val('');
        $("#" + fieldName).val('');
    } else {
        setCurrentTimeForEpochField("epoch_" + fieldName, fieldName);
    }
}

function copyFieldsValue(sourceField, destinationField) {
    let sourceValue = $('#' + sourceField).val();
    if (sourceValue != "" && !isNaN(sourceValue)) {
        let dateTime = moment.unix(sourceValue).format('MM/DD/YYYY hh:mm A');
        $('#' + destinationField).val(dateTime);
    }
}

// Custom Select
function customSelect(containerID){
    $("#"+containerID+" .custom-select").each(function() {
        let classes = $(this).attr("class");
        let template =  '<div class="' + classes + '">';
        template += '<span class="custom-select-trigger">' + $(this).attr("placeholder") + '</span>';
        template += '<div class="custom-options">';
        $(this).find("option").each(function() {
            template += '<span class="custom-option ' + $(this).attr("class") + '" data-value="' + $(this).attr("value") + '">' + $(this).html() + '</span>';
        });
        template += '</div></div>';

        $(this).wrap('<div class="custom-select-wrapper"></div>');
        $(this).hide();
        $(this).after(template);
    });
    $("#"+containerID+" .custom-select-trigger").on("click", function() {
        $('html').one('click',function() {
            $(".custom-select").removeClass("opened");
        });

        $("div.custom-select").each(function() {
            if($(this).closest('.commonSelect').find("div").attr("id") !== containerID){
                $(this).removeClass("opened");
            }
        });

        let element = $("#"+containerID).find("div.custom-select.sources");
        element.toggleClass("opened");

        event.stopPropagation();
    });
    $("#"+containerID+" .custom-option").on("click", function() {
        $(this).parents(".custom-select-wrapper").find("select").val($(this).data("value"));
        $(this).parents(".custom-options").find(".custom-option").removeClass("selection");
        $(this).addClass("selection");
        $(this).parents(".custom-select").removeClass("opened");
        $(this).parents(".custom-select").find(".custom-select-trigger").text($(this).text());
    });
}
