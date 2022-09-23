function showFilterOptions(filterId) {
    $("#" + filterId + " .optionTitle i").toggleClass("active");
    $("#" + filterId + " .options").slideToggle();
}