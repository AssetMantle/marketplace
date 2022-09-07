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

clicked = false;

function loadMoreCollections() {
    const loading = document.querySelector('.loading');
    if ($(".noCollection").length === 0) {
        const activeSection = $.trim($("#sectionMenu").find(".menuItem.active").attr('id'));
        let route = "";
        let loadMore = "";
        switch (activeSection) {
            case "art":
                route = jsRoutes.controllers.CollectionController.collectionsPerPage($(".collectionPage").length + 1);
                loadMore = $(".collectionsPerPage");
                break;
            case "wishListCollections":
                route = jsRoutes.controllers.CollectionController.wishListCollectionPerPage($(".collectionPage").length + 1);
                loadMore = $(".wishlistCollectionsPerPage");
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
                if ($(".noCollection").length === 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
            },
            complete: function () {
                loading.classList.remove('show');
                if ($(".noCollection").length === 0) {
                    $("#loadMoreBtnContainer").removeClass("hide");
                }
                if (activeSection === 'art' && $(".artCollection").length % 6 !== 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
                if (activeSection === 'wishListCollections' && $(".wishListCollection").length % 6 !== 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
            },
            statusCode: {
                200: function (data) {
                    loadMore.append(data);
                    if ($(".noCollection").length !== 0) {
                        $("#loadMoreBtnContainer").addClass("hide");
                    }
                    clicked = false;
                }
            }
        });
    } else {
        console.log("NO COLLECTION LEFT")
        $(".collectionPage:last").css("margin-top", "0px");
        $("#loadMoreBtnContainer").addClass("hide");
    }
}

collectionPageTimeout = 0;

function loadCollection() {
    if (!clicked) {
        clicked = true;
        loadMoreCollections();
    }
}

function showLoadMoreButton() {
    $("#loadMoreBtnContainer").removeClass("hide");
}

$("#sectionMenu .menuItem").on('click', function () {
    showLoadMoreButton();
    $("#sectionMenu").find(".active").removeClass("active");
    $(this).addClass("active");
});