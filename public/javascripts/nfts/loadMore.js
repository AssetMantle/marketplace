window.onbeforeunload = function () {
    window.scrollTo(0, 0);
}
document.onload = function () {
    window.scrollTo(0, 0);
}

function loadMoreNFTs(collectionId) {
    if ($(".noNFT").length === 0) {
        let route = jsRoutes.controllers.CollectionController.collectionNFTsPerPage(collectionId, $(".nftPage").length + 1);
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            statusCode: {
                200: function (data) {
                    const loadMore = $(".nftsPerPage");
                    loadMore.append(data);
                }
            }
        });
    } else {
        console.log("NO COLLECTION LEFT")
        $(".nftPage:last").css("margin-top", "0px");
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

nftPageTimeout = 0;
collectionId = '';
window.addEventListener('scroll', () => {
    clearTimeout(nftPageTimeout);
    nftPageTimeout = setTimeout(function () {
        if ($(window).scrollTop() + $(window).height() >= (getDocHeight() - 10) && $(".nftsPerPage").length !== 0) {
            loadMoreNFTs(collectionId);
        }
    }, 100);
}, {
    passive: true
});

function setCollectionId(id) {
    collectionId = id;
}



