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
        case 'wishListCollections':
            componentResource('collectionsPerPage', jsRoutes.controllers.CollectionController.wishListCollectionPerPage(1));
            checkAndPushState(jsRoutes.controllers.CollectionController.viewCollections('wishListCollections').url, 'wishListCollections', "collections");
            $(".loadMoreElement").addClass("show");
            showLoadMoreButton();
            break;
        case 'whitelist':
            componentResource('collectionsPerPage', jsRoutes.controllers.WhitelistController.whitelist());
            checkAndPushState(jsRoutes.controllers.WhitelistController.whitelist().url, '', "whitelist");
            $(".loadMoreElement").removeClass("show");
            break;
        default:
            componentResource('collectionsPerPage', jsRoutes.controllers.CollectionController.collectionsPerPage(1));
            checkAndPushState(jsRoutes.controllers.CollectionController.viewCollections('art').url, 'art', "collections");
            break;
    }
    changeSectionHighlight(currentSection);
}