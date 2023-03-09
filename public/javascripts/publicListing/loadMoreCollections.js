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
    if ($(".noCollection").length === 0) {
        let route = jsRoutes.controllers.PublicListingController.collectionsPerPage($(".collectionPage").length + 1);
        let loadMore = $("#collectionsPerPage");
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            statusCode: {
                200: function (data) {
                    loadMore.append(data);
                }
            }
        });
    }
}

timeout = 0;

function loadCollectionOnScroll() {
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 100)) {
            if ($(".noCollection").length === 0) {
                loadMoreCollections();
            }
        }
    }, 300);
}

function loadFirstCollections() {
    loadMoreCollections();
    if ($(document).height() > 900) {
        setTimeout(loadMoreCollections, 1000);
    }
}

function setMintedNFTProgressBar() {
    let progressBar = document.querySelector('.progressBar > span');
    let totalNFTs = progressBar.getAttribute("data-totalNFT");
    let mintedNFTs = progressBar.getAttribute("data-mintedNFT");
    let progress = (mintedNFTs * 100) / totalNFTs;
    for (let i = 0; i < progress; i++) {
        progressBar.style.width = i + '%';
    }
}

function setSoldNFTProgressBar(){
    let progressBar = document.querySelector('.progressBar > span');
    let totalNFTs = progressBar.getAttribute("data-totalNFT");
    let soldNFTs = progressBar.getAttribute("data-soldNFT");
    let progress = (soldNFTs*100) / totalNFTs;
    let soldPercentage = document.querySelector(".analysisTitle .analysisPercentage")
    soldPercentage.textContent = "("+progress.toFixed(2)+"%)";
    for(let i = 0; i < progress; i++) {
        progressBar.style.width = i + '%';
    }
}