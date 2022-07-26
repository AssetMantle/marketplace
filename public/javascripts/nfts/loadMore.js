window.onbeforeunload = function () {
    if($(".nftContainer").length !== 0) {
        window.scrollTo(0, 0);
    }
}
document.onload = function () {
    if($(".nftContainer").length !== 0) {
        window.scrollTo(0, 0);
    }
}

function loadMoreNFTs(collectionId) {
    const loading = document.querySelector('.loading');
    // if ($(".noNFT").length === 0 && $(".nftPage").length !== 0) {
    console.log($(".nftPage").length);
    if ($(".noNFT").length === 0) {
        let route = jsRoutes.controllers.CollectionController.collectionNFTsPerPage(collectionId, $(".nftPage").length + 1);
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
                    const loadMore = $(".nftsPerPage");
                    loadMore.append(data);
                    if($(".noNFT").length !== 0){
                        $("#loadMoreCollectionButton").addClass("hide");
                    }
                }
            }
        });
    } else {
        console.log("NO COLLECTION LEFT NFT")
        $(".nftPage:last").css("margin-top", "0px");
        $("#loadMoreCollectionButton").addClass("hide");
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
// window.addEventListener('scroll', () => {
//     clearTimeout(nftPageTimeout);
//     nftPageTimeout = setTimeout(function () {
//         if ($(window).scrollTop() + $(window).height() >= (getDocHeight() - 10) && $(".nftsPerPage").length !== 0) {
//             loadMoreNFTs(collectionId);
//         }
//     }, 100);
// }, {
//     passive: true
// });


// window.addEventListener('scroll', () => {
//     if($(".nftsPerPage").length !== 0){
//         clearTimeout(nftPageTimeout);
//         nftPageTimeout = setTimeout(function () {
//             if ($(window).scrollTop() + $(window).height() >= (getDocHeight() - 10)) {
//                 loadMoreNFTs(collectionId);
//             }
//         }, 100);
//     }
// }, {
//     passive: true
// });

function loadCollections(){
    loadMoreNFTs(collectionId);
}

function setCollectionId(id) {
    collectionId = id;
}



