function submitForm(isModal,source, targetID) {
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
                    if(isModal === "true"){
                        $(".modalContainer").removeClass('active');
                        setTimeout(function(){
                            $(target).html(data);
                            $(".modalContainer").addClass('active');
                        },1000);
                    }
                    else{
                        $(target).html(data);
                    }
                },
                404: function (data) {
                    result.html(data.responseText);
                }
            }
        }).fail(function (XMLHttpRequest) {
            if (XMLHttpRequest.readyState === 0) {
                $('#connectionError').fadeIn(100);
            }
        });
    }
}