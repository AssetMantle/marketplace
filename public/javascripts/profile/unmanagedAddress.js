// Hide submit button
$("#formSubmitButton").hide();

// Show/Hide Unmanaged Address & Password Screen
function showHideUnmanagedAddressModalScreen(showScreen, hideScreen) {
    $(hideScreen).hide();
    $("#staticBackdropLabel").text("Send Transaction");
    $("#modalSubtitle").text("Enter your password to send transaction");
    $(".toggleBtn").hide();
    $("#formSubmitButton").show();
    $(showScreen).show();
}

// Enable button on fill
$(function () {
    $('#newUnmanagedAddressScreen').keyup(function () {
        if ($.trim(unmanagedKeyName.value).length && $.trim(unmanagedKeyAddress.value).length) {
            $("#newUnmanagedAddressScreenBtn").removeClass("disable");
        } else {
            $("#newUnmanagedAddressScreenBtn").addClass("disable");
        }
    });

    $("#formSubmitButton .buttonPrimary").addClass("disable");
    $("#newUnmanagedAddressPasswordScreen").keyup(function (){
        if ($.trim(unmanagedMnemonicPassword.value).length) {
            $("#formSubmitButton .buttonPrimary").removeClass("disable");
        } else {
            $("#formSubmitButton .buttonPrimary").addClass("disable");
        }
    });
});