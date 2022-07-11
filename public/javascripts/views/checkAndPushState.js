function checkAndPushState(route, parameter, functionName) {
    if (addState === true) {
        if (route === "" && parameter === "") {
            window.history.pushState(functionName, "assetMantle", "/");
        } else {
            let address = "";
            if (parameter !== "") {
                console.log(route)
                console.log(route.split('/'))
                address = "/" + route.split('/')[1] + "/" + parameter.toString();
                console.log(address)
            } else {
                address = "/" + route.split('/')[1];
            }
            window.history.pushState(functionName, "assetMantle", address);
        }
    } else {
        addState = true
    }
}