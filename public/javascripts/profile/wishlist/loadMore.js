window.onbeforeunload = function () {
    if ($("#wishlistCollectionsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}
document.onload = function () {
    if ($("#wishlistCollectionsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}

function loadMoreCollections(accountId) {
    const loading = document.querySelector('.loading');
    if ($("#noWishlistCollectionsPerPage").length === 0) {
        let route = jsRoutes.controllers.WishlistController.collectionPerPage(accountId, ($(".wishlistCollectionsPerPage").length + 1));
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
                    $("#wishlistCollectionsPerPage").append(data);
                }
            }
        });
    } else {
        $(".collectionPage.wishlistCollectionsPerPage:last").css("margin-top", "0px");
    }
}

timeout = 0;

function loadWishlistCollectionOnScroll(accountId) {
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 100)) {
            if ($("#noWishlistCollectionsPerPage").length === 0) {
                loadMoreCollections(accountId);
            }
        }
    }, 300);
}

function loadFirstWishlistCollections(accountId){
    componentResource('wishlistCollectionsPerPage', jsRoutes.controllers.WishlistController.collectionPerPage(`${accountId}`, 1));
    if($(document).height() > 900) {
        setTimeout(() => {
            loadWishlistCollectionOnScroll(`${accountId}`)
        }, 1000);
    }
}