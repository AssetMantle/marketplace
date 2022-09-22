window.onbeforeunload = function () {
    if ($(".collectionsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}
document.onload = function () {
    if ($(".collectionsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}

function loadMoreCollections(accountId) {
    const loading = document.querySelector('.loading');
    if ($(".noCollectionLeft").length === 0) {
        let route = jsRoutes.controllers.WishlistController.collectionPerPage(accountId, ($(".wishlistCollectionsPerPage").length + 1));
        console.log(route.url)
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            beforeSend: function () {
                loading.classList.add('show');
                if ($(".noCollectionLeft").length === 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
            },
            complete: function () {
                loading.classList.remove('show');
                if ($(".noCollectionLeft").length === 0) {
                    $("#loadMoreBtnContainer").removeClass("hide");
                }
                if ($(".wishListCollection").length % 6 !== 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
            },
            statusCode: {
                200: function (data) {
                    $(".collectionsPerPage").append(data);
                    if ($(".noCollectionLeft").length !== 0) {
                        $("#loadMoreBtnContainer").addClass("hide");
                    }
                }
            }
        });
    } else {
        $(".collectionPage:last").css("margin-top", "0px");
        $("#loadMoreBtnContainer").addClass("hide");
    }
}

timeout = 0;
function loadWishlistCollectionOnScroll(accountId){
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 100)) {
            if ($(".noCollectionLeft").length === 0) {
                loadMoreCollections(accountId);
            }
        }
    }, 300);
}