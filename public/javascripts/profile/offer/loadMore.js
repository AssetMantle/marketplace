window.onbeforeunload = function () {
    if ($("#wishlistCollectionsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}
document.onload = function () {
    if ($("#wishlistCollectionsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}

function showOfferScreen(screenID) {
    switch (screenID) {
        case "Made":
            $(".contentContainer .contentTitle .title .titleLabel").text("Made");
            $(".contentContainer .contentTitle .titleMenu").show();
            break;
        case "Received":
            $(".contentContainer .contentTitle .title .titleLabel").text("Received");
            $(".contentContainer .contentTitle .titleMenu").hide();
            break;
    }
}