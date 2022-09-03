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
clicked = false;

function loadMoreNFTs(collectionId, sectionName) {
    const loading = document.querySelector('.loading');
    console.log($(".nftPage").length);
    if ($(".noNFT").length === 0) {
        let route = "";
        switch (sectionName) {
            case "art":
                route = jsRoutes.controllers.CollectionController.collectionNFTsPerPage(collectionId, $(".nftPage").length + 1);
                break;
            case "wishlist":
                route = jsRoutes.controllers.CollectionController.wishListCollectionNFTsPerPage(collectionId, $(".nftPage").length + 1);
                break;
            default:
                break;
        }
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
            },
            statusCode: {
                200: function (data) {
                    const loadMore = $(".nftsPerPage");
                    loadMore.append(data);
                    if ($(".noNFT").length !== 0) {
                        $("#loadMoreBtnContainer").addClass("hide");
                    }
                    clicked = false;
                }
            }
        });
    } else {
        console.log("NO COLLECTION LEFT NFT")
        $(".nftPage:last").css("margin-top", "0px");
        $("#loadMoreBtnContainer").addClass("hide");
    }
}

nftPageTimeout = 0;
collectionId = '';

function loadCollections(section) {
    if (!clicked) {
        clicked = true;
        loadMoreNFTs(collectionId, section);
    }
}

function setCollectionId(id) {
    collectionId = id;
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