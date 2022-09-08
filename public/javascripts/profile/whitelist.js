// Date Range Picker
$("#picker").daterangepicker({
    locale: {
        format: 'DD/MM/YYYY'
    }
});

// Check Whitelist Name Exist or Not
function checkWhitelistNameAvailable(source) {
    const whitelistName = $(source).val();
    if (whitelistName.length > 0 && whitelistName === "test") {
        $("#whitelistNameAvailableError").show(300);
        $("#whitelistName").css("border-color", "var(--error)");
    }else{
        $("#whitelistNameAvailableError").hide(300);
        $("#whitelistName:focus").css("border-color","var(--dark)");
    }
}

