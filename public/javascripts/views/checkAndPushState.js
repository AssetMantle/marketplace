function checkAndPushState(route, parameter, functionName) {
    console.log(route)
    console.log(parameter)
    if (addState === true) {
        if (route === "" && parameter === "") {
            window.history.pushState(functionName, "assetMantle", "/");
        } else {
            let address = "";
            if (parameter !== "") {
                address = "/" + route.split('/')[1] + "/" + parameter.toString();
            } else {
                address = "/" + route.split('/')[1];
            }
            console.log(address)
            window.history.pushState(functionName, "assetMantle", address);
        }
    } else {
        addState = true
    }
}