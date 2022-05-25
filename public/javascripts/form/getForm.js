function getForm(route) {
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {

            },
            500: function (data) {
                replaceDocument(data);
            },
        }
    }).fail(function (XMLHttpRequest) {
        if (XMLHttpRequest.readyState === 0) {
            $('#connectionError').fadeIn(100);
        }
    });
}

function replaceDocument(data) {
    $(window).trigger('replace');
    const newDocument = document.open("text/html", "replace");
    newDocument.write(data);
    newDocument.close();
    // webSocket.close();
}