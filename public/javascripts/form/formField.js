function commonDescriptionInputOnKeyPress(fieldId, maximumCharacters) {
    let inputField = $("#" + fieldId);
    if (inputField.val().length > maximumCharacters) {
        inputField.val(inputField.val().substring(0, maximumCharacters));
        return false;
    } else {
        $("#remainingCharacter_" + fieldId).text(maximumCharacters - inputField.val().length + "/" + maximumCharacters);
    }
}

function commonInputEpochTime(epochFieldName, fieldName) {
    $('#' + epochFieldName).daterangepicker({
        timePicker: true,
        singleDatePicker: true,
        startDate: moment().startOf('minute'),
        drops: 'up',
        locale: {
            format: 'MM/DD/YYYY hh:mm A',
            cancelLabel: 'Clear',
        }
    });

    $('#' + epochFieldName).on('apply.daterangepicker', function (ev, picker) {
        $(this).val(picker.startDate.format('MM/DD/YYYY hh:mm A'));
        let dateStr = picker.startDate.format('MM/DD/YYYY hh:mm A');
        let date = new Date(dateStr);
        const seconds = Math.floor(date.getTime() / 1000);
        $("#" + fieldName).val(seconds);
    });

    $('#' + epochFieldName).on('cancel.daterangepicker', function (ev, picker) {
        $(this).val('');
        $("#" + fieldName).val('');
    });
}

function setCurrentTimeForEpochField(epochFieldName, fieldName) {
    console.log("setCurrentTimeForEpochField")
    console.log(epochFieldName)
    console.log(fieldName)
    let dateStr = $('#' + epochFieldName).val();
    let date = new Date(dateStr);
    const seconds = Math.floor(date.getTime() / 1000);
    $("#" + fieldName).val(seconds);
}

function removeCurrentTimeForEpochField(fieldName) {
    console.log("removeCurrentTimeForEpochField")
    console.log(fieldName)
    $("#" + fieldName).val('');
}