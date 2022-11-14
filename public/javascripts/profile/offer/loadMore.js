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