function checkNoCollection() {
    if ($(".singleCreatedCollection").length === 0) {
        $('#noCreatedCollectionsPerPage').removeClass("hidden");
    }
}