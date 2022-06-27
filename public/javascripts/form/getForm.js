function getForm(route, modal = '#commonModal', modalContent = '#modal-content') {
    // let modalID = $('#' + modal);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                // modal.html(data);
                $(modal+" .modal").removeClass('active');
                setTimeout(function(){
                    $(modalContent).html(data);
                    $(modal).addClass('active');
                    $(modal+" .modal").addClass('active');
                    $("body").addClass('modal-active');
                },1000);

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
