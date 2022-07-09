function getForm(route, modal = '#commonModal', modalContent = '#modal-content') {
    // let modalID = $('#' + modal);
    let myModal = new bootstrap.Modal($(modal), {});
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                // $(modalContent).html(data);
                $(modal).addClass('active');
                // $(modal + " .modalContainer").addClass('active');
                $(modalContent).html(data);
                myModal.show();
                // $("body").addClass('modal-active');
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
