function submitForm(isModal, source, targetID) {
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
                    clearFormElements("formBody");
                    $(".error").closest("dd").prev().find('input').css({"border-color": "var(--error)"});
                },
                401: function (data) {
                    replaceDocument(data.responseText);
                },
                500: function (data) {
                    replaceDocument(data.responseText);
                },
                200: function (data) {
                    replaceDocument(data);
                },
                206: function (data) {
                    if (isModal === "true") {
                        $(".modalContainer").removeClass('active');
                        // setTimeout(function(){
                        $(target).html(data);
                        $(".modalContainer").addClass('active');
                        // },1000);
                    } else {
                        $(target).html(data);
                    }
                },
                404: function (data) {
                    result.html(data.responseText);
                },
                201: function (callbackUrl) {
                    window.location = callbackUrl;
                }
            }
        }).fail(function (XMLHttpRequest) {
            if (XMLHttpRequest.readyState === 0) {
                $('#connectionError').fadeIn(100);
            }
        });
    } else {
        console.log("Form validation failed")
    }
}

function onKeyPress(event, isModal, source, targetID) {
    if (event.keyCode === 13) {
        event.preventDefault();
        submitForm(isModal, source, targetID);
    }
}

function onKeyUp(source) {
    const form = $(source).closest("form");
    let allFiled = checkAllFieldsFilled(form);
    console.log(allFiled);
}

function clearFormElements(target_id) {
    jQuery("#" + target_id).find(':input').each(function () {
        switch (this.type) {
            case 'checkbox':
            case 'radio':
                this.checked = false;
                break;
        }
    });
}