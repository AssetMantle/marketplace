function commonDescriptionInputOnKeyPress(fieldId, maximumCharacters) {
    let inputField = $("#" + fieldId);
    if (inputField.val().length > maximumCharacters) {
        inputField.val(inputField.val().substring(0, maximumCharacters));
        return false;
    } else {
        $("#remainingCharacter_" + fieldId).text(maximumCharacters - inputField.val().length + "/" + maximumCharacters);
    }
}
