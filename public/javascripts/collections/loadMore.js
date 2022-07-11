window.onbeforeunload = function () {
    window.scrollTo(0, 0);
}
document.onload = function () {
    window.scrollTo(0, 0);
}

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
                    const loadMore = $(".collectionsPerPage");
                    loadMore.append(data);
                }
            }
        });
    } else {
        console.log("NO COLLECTION LEFT")
        $(".collectionPage:last").css("margin-top", "0px");
    }
}

function getDocHeight() {
    let D = document;
    return Math.max(
        D.body.scrollHeight, D.documentElement.scrollHeight,
        D.body.offsetHeight, D.documentElement.offsetHeight,
        D.body.clientHeight, D.documentElement.clientHeight
    );
}

collectionPageTimeout = 0;
window.addEventListener('scroll', () => {
    clearTimeout(collectionPageTimeout);
    collectionPageTimeout = setTimeout(function () {
        if ($(window).scrollTop() + $(window).height() >= (getDocHeight() - 10)) {
            loadMoreCollections();
        }
    }, 100);
}, {
    passive: true
});



