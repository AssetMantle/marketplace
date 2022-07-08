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
                    console.log(data);
                    const loadMore = $(".collectionsPerPage");
                    loadMore.append(data);
                }
            }
        });
    } else {
        console.log("NO COLLECTION LEFT")
        $(".collectionPage:last").css("margin-top","0px");
    }
}

function getDocHeight() {
    var D = document;
    return Math.max(
        D.body.scrollHeight, D.documentElement.scrollHeight,
        D.body.offsetHeight, D.documentElement.offsetHeight,
        D.body.clientHeight, D.documentElement.clientHeight
    );
}

var timeout;
window.addEventListener('scroll', () => {
    clearTimeout(timeout);
    timeout = setTimeout(function() {
        if($(window).scrollTop() + $(window).height() >= (getDocHeight() - 10)) {
            loadMoreCollections();
        }
    }, 50);
}, {
    passive: true
});


