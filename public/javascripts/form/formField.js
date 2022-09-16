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
        $("#epochInt_" + fieldName).val(seconds);
        $("#epochInt_" + fieldName).trigger("keyup");
    });

    $('#' + epochFieldName).on('cancel.daterangepicker', function (ev, picker) {
        $(this).val('');
        $("#epochInt_" + fieldName).val('');
        $("#epochInt_" + fieldName).trigger("keyup");
    });
}

function setCurrentTimeForEpochField(epochFieldName, fieldName) {
    let dateStr = moment().format('MM/DD/YYYY hh:mm A');
    $('#' + epochFieldName).val(dateStr);
    let date = new Date(dateStr);
    const seconds = Math.floor(date.getTime() / 1000);
    $("#epochInt_" + fieldName).val(seconds);
}

function clearEpochField(fieldName, showCurrentTime) {
    if (!showCurrentTime) {
        $("#epoch_" + fieldName).val('');
        $("#" + fieldName).val('');
    } else {
        setCurrentTimeForEpochField("epoch_" + fieldName, fieldName);
    }
}

function validateInputText(name, minimumLength, maximumLength, regex) {
    console.log(name)
    console.log(minimumLength)
    console.log(maximumLength)
    console.log(regex)
}