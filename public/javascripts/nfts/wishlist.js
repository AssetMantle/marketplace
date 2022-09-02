function updateWishlist() {
    $(".nft-likes").each(function () {
        wishlistCounter(this, jsRoutes.controllers.NFTController.likesCounter($(this).attr("data-id")));
    });


}

function wishlistCounter(source, route) {
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                console.log(data)
                $(source).text(data);
            },
            401: function () {
            },
            500: function () {
            }
        }
    });
}

updateWishlist();

function wishlist(route, wishlistButton) {
    console.log(route);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function () {
                let parent = $(wishlistButton).parent();
                let counter = parent.find(".nft-likes");
                setTimeout(() => {
                    console.log($(counter).attr("data-id"));
                    wishlistCounter(counter, jsRoutes.controllers.NFTController.likesCounter($(counter).attr("data-id")));
                }, 5000);
            },
            401: function () {
                console.log("400 response");
            },
            500: function () {
                console.log("500 response");
            }
        }
    });
}

function addRemoveWishlist(element, NFTId) {
    let wishlistIcon = $(element).children(".addToWishlist");
    if (!$(wishlistIcon).hasClass("clicked")) {
        wishlist(jsRoutes.controllers.NFTController.addToWishList(`${NFTId}`), element);
        $(wishlistIcon).addClass("clicked");
    } else {
        wishlist(jsRoutes.controllers.NFTController.deleteFromWishList(`${NFTId}`), element);
        $(wishlistIcon).removeClass("clicked");
    }
}