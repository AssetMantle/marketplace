function getImage(route, id) {
    return $('#' + id).html('<img src="' + route.url + '">');
}