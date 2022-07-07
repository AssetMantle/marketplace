function loadMoreCollections() {
    if ($(".noCollection").length === 0) {
        let route = jsRoutes.controllers.CollectionController.collectionsPerPage($(".collectionPage").length + 1);
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            // global: showSpinner('recentActivity'),
            // beforeSend: function () {
            //     loadingSpinner.show();
            // },
            // complete: function () {
            //     loadingSpinner.hide();
            // },
            statusCode: {
                200: function (data) {
                    const loadMore = $(".collectionsPerPage:last");
                    loadMore.append(data);
                }
            }
        });
    }
}

window.addEventListener('scroll', () => {
    if (window.scrollY + window.innerHeight >= document.documentElement.scrollHeight) {
        loadMoreCollections();
    }
}, {
    passive: true
});