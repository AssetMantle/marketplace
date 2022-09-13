function getForm(route, modal = '#commonModal', modalContent = '#modal-content') {
    let myModal = new bootstrap.Modal($(modal), {});
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $(modal).addClass('active');
                $(modalContent).html(data);
                myModal.show();
            },
            500: function (data) {
                replaceDocument(data);
            },
            400: function (data) {
                $(modal).addClass('active');
                $(modalContent).html(data.responseText);
                myModal.show();
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
