window.onbeforeunload = function () {
    if ($("#collectedNFTsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}
document.onload = function () {
    if ($("#collectedNFTsPerPage").length !== 0) {
        window.scrollTo(0, 0);
    }
}

function loadMoreCollectedNFTs(accountId) {
    const loading = document.querySelector('.loading');
    if ($("#noCollectedNFTsPerPage").length === 0) {
        let nextPageNumber = Math.ceil($(".ownedNFTPage").length + 1);
        console.log(nextPageNumber);
        let route = jsRoutes.controllers.NFTController.collectedNFTsPerPage(accountId, nextPageNumber);
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            beforeSend: function () {
                loading.classList.add('show');
            },
            complete: function () {
                loading.classList.remove('show');
            },
            statusCode: {
                200: function (data) {
                    $('.ownedNFTsPerPage').append(data);
                }
            }
        });
    } else {
        $(".ownedNFTsPerPage:last").css("margin-top", "0px");
    }
}

timeout = 0;

function loadCollectedNFTsOnScroll(accountId) {
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 100)) {
            if ($("#noCollectedNFTsPerPage").length === 0) {
                loadMoreCollectedNFTs(accountId);
            }
        }
    }, 300);
}

function setEmptyContainer(){
    let nftCard = $(".singleNFTCard").length;
    if(nftCard !== 0){
        $("#noCollectedNFTFound").hide();
    }else{
        $("#noCollectedNFTFound").show();
    }
}