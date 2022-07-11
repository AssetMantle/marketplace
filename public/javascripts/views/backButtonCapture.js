window.addEventListener('popstate', e => {
    addState = false
    //The last part of URL -> eg. "409925" in http://localhost:9000/blocks/409925
    let lastPart = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);

    switch (this.history.state) {
        case "collections":
            viewCollections();
            break;
        case "collection":
            viewCollection(lastPart);
            break;
        case "nft":
            viewNFT(lastPart);
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

