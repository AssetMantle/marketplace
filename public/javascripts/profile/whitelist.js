// Date Range Picker
$("#picker").daterangepicker({
    locale: {
        format: 'DD/MM/YYYY'
    }
});

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

// Tooltip
$(function () {
    $('[data-toggle="tooltip"]').tooltip();
})