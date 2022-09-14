function emptyCallBack() {
}

function getForm(route, modal = '#commonModal', modalContent = '#modal-content', callback = emptyCallBack) {
    let myModal = new bootstrap.Modal($(modal), {});
    console.log(route.url)
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $(modal).addClass('active');
                $(modalContent).html(data);
                myModal.show();
                callback();
            },
            500: function (data) {
                replaceDocument(data);
            },
            400: function (data) {
                $(modal).addClass('active');
                $(modalContent).html(data.responseText);
                myModal.show();
                callback();
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
}
