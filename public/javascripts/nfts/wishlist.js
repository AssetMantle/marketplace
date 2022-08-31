function wishlist(route) {
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function () {
                console.log("Ok response");
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