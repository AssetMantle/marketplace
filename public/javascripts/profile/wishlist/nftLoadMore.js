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
    if ($(".noNFT").length === 0) {
        let route = jsRoutes.controllers.WishlistController.collectionNFTsPerPage(accountId, collectionId, $(".nftPage").length + 1);
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
        $(".nftPage:last").css("margin-top", "0px");
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
                div.addClass("centerText componentError cmuk-card cmuk-card-default commonCard cmuk-animation-fade cmuk-card-body cmuk-height-medium cmuk-overflow-auto");
                div.html(imageElement);
                div.append("<p>" + data.responseText + "</p>")
            }
        }
    });
}

function checkAndLoadWishListCollectionNFTs(accountId, collectionId, collectionFileType) {
    let commonContent = document.getElementById('centerContent');
    if (commonContent === null) {
        window.location = '/profile/' + accountId;
    } else {
        loadFirstNFTBulk('nftsPerPage', jsRoutes.controllers.WishlistController.collectionNFTsPerPage(accountId, collectionId, 1));
        checkAndPushState(jsRoutes.controllers.WishlistController.viewCollectionNFTs(accountId, collectionId).url, (accountId + '/wishListNFTs/' + collectionId), "wishListCollection");
        getImage(jsRoutes.controllers.CollectionController.collectionFile(collectionId, collectionFileType, false), ('collectionUncompressedCoverImage_' + collectionId));
    }
}