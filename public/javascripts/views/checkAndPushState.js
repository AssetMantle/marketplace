function checkAndPushState(route, parameter, stateName) {
    console.log("---------")
    console.log(route)
    console.log(parameter)
    console.log(stateName)
    if (addState === true) {
        if (route === "" && parameter === "") {
            window.history.pushState(stateName, "assetMantle", "/");
        } else {
            let address = "";
            if (parameter !== "") {
                address = "/" + route.split('/')[1] + "/" + parameter.toString();
            } else {
                address = "/" + route.split('/')[1];
            }
            window.history.pushState(stateName, "assetMantle", address);
        }
    } else {
        addState = true
    }
}