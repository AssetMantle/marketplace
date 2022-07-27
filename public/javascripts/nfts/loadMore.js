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
clicked = false;
function loadMoreNFTs(collectionId) {
    const loading = document.querySelector('.loading');
    console.log($(".nftPage").length);
    if ($(".noNFT").length === 0) {
        let route = jsRoutes.controllers.CollectionController.collectionNFTsPerPage(collectionId, $(".nftPage").length + 1);
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            beforeSend: function () {
                loading.classList.add('show');
                if($(".noNFT").length === 0) {
                    $("#loadMoreBtnContainer").addClass("hide");
                }
            },
            complete: function () {
                loading.classList.remove('show');
                if($(".noNFT").length === 0) {
                    $("#loadMoreBtnContainer").removeClass("hide");
                }
            },
            statusCode: {
                200: function (data) {
                    const loadMore = $(".nftsPerPage");
                    loadMore.append(data);
                    if($(".noNFT").length !== 0){
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
function loadCollections(){
    if(!clicked){
        clicked = true;
        loadMoreNFTs(collectionId);
    }
}

function setCollectionId(id) {
    collectionId = id;
}