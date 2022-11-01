$(document).ready(function () {
    if ($(".nftPage .singleNFTCard").length <= 5) {
        $("#loadMoreBtnContainer").addClass("hide");
    }
});

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

function wishlist(form, route, wishlistButton, snackBarMessage, NFTId, add) {
    $.ajax({
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        url: form.attr('action'),
        data: form.serialize(),
        async: true,
        statusCode: {
            200: function (data) {
                let parent = $(wishlistButton).parent();
                let counter = parent.find(".nft-likes");
                showSnackbar('', snackBarMessage, 'info');
                if (add) {
                    $('#addToWishlistContainer_' + NFTId).html(data);
                } else {
                    $('#deleteFromWishlistContainer_' + NFTId).html(data);
                }
                setTimeout(() => {
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

function addRemoveWishlist(element, NFTId, addMessage, removedMessage) {
    let wishlistIcon = $(element).children(".addToWishlist");
    if (!$(wishlistIcon).hasClass("clicked")) {
        const form = $("#addToWishlist_" + NFTId + " form")
        wishlist(form, jsRoutes.controllers.WishlistController.add(), element, addMessage, NFTId, true);
        $(wishlistIcon).addClass("clicked");
    } else {
        const form = $("#deleteFromWishlist_" + NFTId + " form")
        wishlist(form, jsRoutes.controllers.WishlistController.delete(), element, removedMessage, NFTId, false);
        $(wishlistIcon).removeClass("clicked");
    }
}