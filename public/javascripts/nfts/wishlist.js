function updateWishlist() {
    $(".nft-likes").each(function () {
        wishlistCounter(this, jsRoutes.controllers.NFTController.likesCounter($(this).attr("data-id")));
    });

    function wishlistCounter(source, route) {
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            statusCode: {
                200: function (data) {
                    console.log(data);
                    console.log(source);
                    $(source).text(data);
                },
                401: function () {
                    console.log("400 Response");
                },
                500: function () {
                    console.log("500 Response");
                }
            }
        });
    }
}
updateWishlist();

function wishlist(route) {
    console.log(route);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function () {
                console.log("Ok response");
                setTimeout(updateWishlist,2000);
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

function addRemoveWishlist(element, NFTId){
    var wishlistIcon = $(element).children(".addToWishlist");
    if(!$(wishlistIcon).hasClass("clicked")){
        wishlist(jsRoutes.controllers.NFTController.addToWishList(`${NFTId}`));
        $(wishlistIcon).addClass("clicked");
    }else{
        wishlist(jsRoutes.controllers.NFTController.deleteFromWishList(`${NFTId}`));
        $(wishlistIcon).removeClass("clicked");
    }
}