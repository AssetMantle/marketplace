function submitForm(source, targetID) {
    const target = '#' + targetID;
    const form = $(source).closest("form");
    if (validateForm(form)) {
        const result = $(target);
        $.ajax({
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            url: form.attr('action'),
            data: form.serialize(),
            async: true,
            statusCode: {
                400: function (data) {
                    result.html(data.responseText);
                },
                500: function (data) {
                    replaceDocument(data.responseText);
                },
                200: function (data) {
                    replaceDocument(data);
                },
                206: function (data) {
                    $(target).html(data);
                },
                404: function (data) {
                    $(target).html(data.responseText);
                }
            }
        }).fail(function (XMLHttpRequest) {
            if (XMLHttpRequest.readyState === 0) {
                $('#connectionError').fadeIn(100);
            }
        });
    }
}