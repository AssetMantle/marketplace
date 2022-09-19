currentSection = 'art';

function changeSectionHighlight(section) {
    $("#sectionMenu").find(".active").removeClass("active");
    $('#' + section).addClass("active");
}

function setActive(section) {
    currentSection = section;
}

function changeActive(section) {
    currentSection = section;
    switch (currentSection) {
        case 'art':
            componentResource('collectionsPerPage', jsRoutes.controllers.CollectionController.collectionsPerPage(1));
            checkAndPushState(jsRoutes.controllers.CollectionController.viewCollections('art').url, 'art', "collections");
            $(".loadMoreElement").addClass("show");
            showLoadMoreButton();
            break;
        default:
            componentResource('collectionsPerPage', jsRoutes.controllers.CollectionController.collectionsPerPage(1));
            checkAndPushState(jsRoutes.controllers.CollectionController.viewCollections('art').url, 'art', "collections");
            break;
    }
    changeSectionHighlight(currentSection);
}

function checkNumberOfCollections(){
    if ($(".collectionsPerPage .singleCollection").length <= 5) {
        $("#loadMoreBtnContainer").addClass("hide");
    }
}