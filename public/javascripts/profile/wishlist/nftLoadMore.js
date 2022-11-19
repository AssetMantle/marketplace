window.onbeforeunload = function () {
    if ($(".nftContainer").length !== 0) {
        window.scrollTo(0, 0);
    }
}
document.onload = function () {
    if ($(".nftContainer").length !== 0) {
        window.scrollTo(0, 0);
    }
}

function loadMoreNFTs(accountId, collectionId) {
    const loading = document.querySelector('.loading');
    let totalNfts = $(".singleNFTCard").length;
    let draftNFT = $('.draftNft').length;
    let createCard = $(".createNFTCard").length;
    let totalWishlist = (totalNfts - createCard - draftNFT);
    let pageNumber = Math.floor(totalWishlist / 6) + 1;
    if (totalWishlist < (pageNumber * 6) && totalWishlist > ((pageNumber - 1) * 6) && totalWishlist % 6 !== 0) {
        pageNumber = pageNumber + 1;
    }
    if ($(".noNFT").length === 0) {
        let route = jsRoutes.controllers.WishlistController.collectionNFTsPerPage(accountId, collectionId, pageNumber);
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            beforeSend: function () {
                loading.classList.add('show');
                if ($(".noNFT").length === 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
            },
            complete: function () {
                loading.classList.remove('show');
                if ($(".noNFT").length === 0) {
                    $("#loadMoreBtnContainer").removeClass("hide");
                }
                if ($(".singleNFTCard").length % 6 !== 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
            },
            statusCode: {
                200: function (data) {
                    const loadMore = $(".nftsPerPage");
                    loadMore.append(data);
                    if ($(".noNFT").length !== 0) {
                        $("#loadMoreBtnContainer").addClass("hide");
                    }
                }
            }
        });
    } else {
        $(".nftsPerPage:last").css("margin-top", "0px");
        $("#loadMoreBtnContainer").addClass("hide");
    }
}

function loadFirstNFTBulk(source, route, loadingSpinnerID = 'commonSpinner', event = '') {
    const loading = document.querySelector('.loading');
    const div = $('#' + source);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        beforeSend: function () {
            loading.classList.add('show');
            $("#loadMoreBtnContainer").addClass("hide");
        },
        complete: function () {
            loading.classList.remove('show');
            $("#loadMoreBtnContainer").removeClass("hide");
        },
        statusCode: {
            200: function (data) {
                div.html(data);
            },
            401: function (data) {
                replaceDocument(data.responseText);
            },
            500: function (data) {
                let imageElement = document.createElement('img');
                const imageRoute = jsRoutes.controllers.Assets.versioned("images/exclamation.png");
                imageElement.src = imageRoute.url;
                div.addClass("centerText componentError commonCard");
                div.html(imageElement);
                div.append("<p>" + data.responseText + "</p>")
            }
        }
    });
}

timeout = 0;
function loadWishlistNftOnScroll(accountId, collectionId){
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 500)) {
            if ($(".noNFT").length === 0) {
                loadMoreNFTs(accountId,collectionId);
            }
        }
    }, 300);
}

function loadFirstWishlistNFTs(accountId, collectionId){
    loadFirstNFTBulk('nftsPerPage', jsRoutes.controllers.WishlistController.collectionNFTsPerPage(`${accountId}`,`${collectionId}`, 1));
    if($(document).height() > 1000) {
        setTimeout(() => {
            loadWishlistNftOnScroll(`${accountId}`,`${collectionId}`)
        }, 1000);
    }
}