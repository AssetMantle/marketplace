window.addEventListener('popstate', e => {
    addState = false
    //The last part of URL -> eg. "409925" in http://localhost:9000/blocks/409925
    let lastPart = window.location.href.split("/").slice(4).join("/");//window.location.href.substr(window.location.href.lastIndexOf('/') + 1);

    switch (this.history.state) {
        case "collections":
            viewCollections(lastPart);
            break;
        case "collectionForPublicListing":
            viewCollection(lastPart, true);
            break;
        case "collectionForWhitelistSale":
            viewCollection(lastPart, false);
            break;
        case "wishListCollection":
            viewWishListCollection(lastPart);
            break;
        case "collectedCollection":
            viewCollectedCollection(lastPart);
            break;
        case "nft":
            viewNFT(lastPart);
            break;
        case "setting":
            viewSetting();
            break;
        case "profile":
            viewProfile(lastPart);
            break;
        case "publicListedCollections":
            viewPublicListedCollections();
            break;
        case "whitelistSaleCollections":
            viewWhitelistSaleCollections();
            break;
        case "index":
            window.location = "/";
            break;
        default:
            window.location = "/";
            break;
    }
    let elems = document.querySelectorAll(".active");
    [].forEach.call(elems, function (el) {
        el.classList.remove("active");
    });
    // navBar()
})

