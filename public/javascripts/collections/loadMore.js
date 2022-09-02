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
        let sectionMenu = $.trim($("#sectionMenu").find(".menuItem.active").text());

        let route = "";
        let loadMore = "";
        switch (sectionMenu) {
            case "Art":
                route = jsRoutes.controllers.CollectionController.collectionsPerPage($(".collectionPage").length + 1);
                loadMore = $(".collectionsPerPage");
                break;
            case "Wishlist":
                route = jsRoutes.controllers.CollectionController.wishListCollectionPerPage($(".collectionPage").length + 1);
                loadMore = $(".collectionsPerPage");
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
            },
            statusCode: {
                200: function (data) {
                    const loadMore = $(".collectionsPerPage");
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

function getDocHeight() {
    let D = document;
    return Math.max(
        D.body.scrollHeight, D.documentElement.scrollHeight,
        D.body.offsetHeight, D.documentElement.offsetHeight,
        D.body.clientHeight, D.documentElement.clientHeight
    );
}

collectionPageTimeout = 0;

function loadCo() {
    if (!clicked) {
        clicked = true;
        loadMoreCollections();
    }
}

function showLoadMoreButton(){
    $("#loadMoreBtnContainer").removeClass("hide");
}