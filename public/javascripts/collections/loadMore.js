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
    let activeTab = $('.active').attr('id').slice(0, -4);
    if ($(".noCollection").length === 0) {
        let route = jsRoutes.controllers.CollectionController.collectionsPerPage(activeTab, $(".collectionPage").length + 1);
        let loadMore = $("#collectionsPerPage_" + activeTab);
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

function loadFirstCollections(){
    loadMoreCollections();
    if($(document).height() > 900){
        setTimeout(loadMoreCollections, 1000);
    }
}

function setSoldNFTProgressBar(){
    let progressBar = document.querySelector('.progressBar > span');
    let totalNFTs = progressBar.getAttribute("data-totalNFT");
    let soldNFTs = progressBar.getAttribute("data-soldNFT");
    let progress = (soldNFTs*100) / totalNFTs;
    let soldPercentage = document.querySelector(".analysisTitle .analysisPercentage")
    soldPercentage.textContent = "("+progress+"%)";
    for(let i = 0; i < progress; i++) {
        progressBar.style.width = i + '%';
    }
}