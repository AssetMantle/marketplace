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

function showWhitelistScreen(screenID){
    if(screenID === "created"){
        $(".contentContainer .contentTitle .title .titleLabel").text("Created");
        $(".contentContainer .contentTitle .titleMenu").show();
        componentResource("personalProfileContent", jsRoutes.controllers.ProfileController.createdWhitelists(1));
    }else if(screenID === "joined"){
        $(".contentContainer .contentTitle .title .titleLabel").text("Joined");
        $(".contentContainer .contentTitle .titleMenu").hide();
        componentResource("personalProfileContent", jsRoutes.controllers.ProfileController.joinedWhitelists(1));
    }
}

// Convert Epoch to Date and Time
$(".tableFieldEndEpoch.epochValue, .tableFieldStartEpoch.epochValue").each(function () {
    let seconds = $(this).text();
    let dt = eval(seconds * 1000);
    let date = new Date(dt);
    let epochDate = date.toLocaleDateString();
    let epochTime = date.toLocaleTimeString();
    $(this).html(`${epochDate} &nbsp;${epochTime}`)
});

// Tooltip
$(function () {
    $('[data-toggle="tooltip"]').tooltip();
})