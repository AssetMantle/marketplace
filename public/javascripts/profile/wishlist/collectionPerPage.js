$(document).ready(function () {
    if ($(".wishlistCollectionsPerPage .singleCollection").length <= 5) {
        $("#loadMoreBtnContainer").addClass("hide");
    }
});

function checkNoCollection() {
    if ($(".singleCollection").length === 0) {
        $("#loadMoreBtnContainer").addClass("hide");
        $('#noCollection').removeClass("hidden");
    }
}