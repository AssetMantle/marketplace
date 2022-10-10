function checkNoCollection() {
    if ($(".singleWishlistCollection").length === 0) {
        $('#noWishlistCollectionsPerPage').removeClass("hidden");
    }
}