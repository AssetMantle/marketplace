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