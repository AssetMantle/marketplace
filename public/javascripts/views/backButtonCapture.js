window.addEventListener('popstate', e => {
    addState = false
    //The last part of URL -> eg. "409925" in http://localhost:9000/blocks/409925
    let lastPart = window.location.href.split("/").slice(4).join("/");//window.location.href.substr(window.location.href.lastIndexOf('/') + 1);

    switch (this.history.state) {
        case "collections":
            viewCollections();
            break;
        case "collection":
            viewCollection(lastPart);
            break;
        case "earlyAccess":
            viewEarlyAccess();
            break;
        case "earlyAccessCollection":
            viewEarlyAccessCollection(lastPart);
            break;
        case "launchpad":
            viewLaunchpad();
            break;
        case "launchpadCollection":
            viewLaunchpadCollection(lastPart);
            break;
        case "secondaryMarket":
            viewSecondaryMarketCollections();
            break;
        case "marketCollection":
            viewMarketCollection(lastPart);
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

