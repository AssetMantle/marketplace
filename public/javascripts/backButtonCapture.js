window.addEventListener('popstate', e => {
    addState = false
    //The last part of URL -> eg. "409925" in http://localhost:9000/blocks/409925
    let lastPart = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);

    switch (this.history.state) {
        case "collection":
            componentResource('centerContent', jsRoutes.controllers.CollectionController.collection(lastPart))
            break;
        case "collections":
            componentResource('centerContent', jsRoutes.controllers.CollectionController.all());
            break;
        default:
            componentResource('centerContent', jsRoutes.controllers.CollectionController.viewCollections());
            break;
    }
    let elems = document.querySelectorAll(".active");
    [].forEach.call(elems, function (el) {
        el.classList.remove("active");
    });
    // navBar()
})

