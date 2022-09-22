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

function loadMoreCollections() {
    const loading = document.querySelector('.loading');
    if ($(".noCollection").length === 0) {
        const activeSection = $.trim($("#sectionMenu").find(".menuItem.active").attr('id'));
        let route = jsRoutes.controllers.CollectionController.collectionsPerPage($(".collectionPage").length + 1);
        let loadMore = $(".collectionsPerPage");
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
            },
            statusCode: {
                200: function (data) {
                    loadMore.append(data);
                    if ($(".noCollection").length !== 0) {
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

function showLoadMoreButton() {
    $("#loadMoreBtnContainer").removeClass("hide");
}

$("#sectionMenu .menuItem").on('click', function () {
    showLoadMoreButton();
    $("#sectionMenu").find(".active").removeClass("active");
    $(this).addClass("active");
});

timeout = 0;
function loadCollectionOnScroll(){
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 100)) {
            if ($(".noCollection").length === 0) {
                loadMoreCollections();
            }
        }
    }, 300);
}