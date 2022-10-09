function switcher(id) {
    let firstTabEl = document.querySelector('#' + id + ' li.active a')
    let firstTab = new bootstrap.Tab(firstTabEl)

    firstTab.show()
}

function changeProfileStateOnSwitcher(accountId, section) {
    checkAndPushState(jsRoutes.controllers.ProfileController.viewProfile(accountId, section).url, (accountId + '/' + section), 'profile');
}

function changeExploreStateOnSwitcher(category) {
    checkAndPushState(jsRoutes.controllers.CollectionController.viewCollections(category).url, category, "collections");
}