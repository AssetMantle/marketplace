window.onbeforeunload = function () {
    if ($("#collectedNFTsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}
document.onload = function () {
    if ($("#collectedNFTsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}

function loadMoreCollections(accountId) {
    const loading = document.querySelector('.loading');
    if ($("#noCollectedCollectionsPerPage").length === 0) {
        let route = jsRoutes.controllers.CollectedController.collectionPerPage(accountId, ($(".collectedCollectionsPerPage").length + 1));
        console.log(route.url)
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            beforeSend: function () {
                loading.classList.add('show');
            },
            complete: function () {
                loading.classList.remove('show');
            },
            statusCode: {
                200: function (data) {
                    $("#collectedCollectionsPerPage").append(data);
                }
            }
        });
    } else {
        $(".collectionPage.collectedCollectionsPerPage:last").css("margin-top", "0px");
    }
}

timeout = 0;

function loadCollectedCollectionOnScroll(accountId) {
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 100)) {
            if ($("#noCollectedCollectionsPerPage").length === 0) {
                loadMoreCollections(accountId);
            }
        }
    }, 300);
}

function loadFirstCollectedCollections(accountId){
    componentResource('collectedCollectionsPerPage', jsRoutes.controllers.CollectedController.collectionPerPage(`${accountId}`, 1));
    if($(document).height() > 1000) {
        setTimeout(() => {
            loadCollectedCollectionOnScroll(`${accountId}`)
        }, 1000);
    }
}