function emptyCallBack() {
}

function defaultCheckBeforeSubmit() {
    return true;
}

function submitForm(refreshOverlay, source, targetID, checkBeforeSubmit = defaultCheckBeforeSubmit, callback = emptyCallBack) {
    const target = '#' + targetID;
    const form = $(source).closest("form");
    if (validateForm(form) && checkBeforeSubmit()) {
        const result = $(target);
        $.ajax({
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            url: form.attr('action'),
            data: form.serialize(),
            async: true,
            beforeSend: function () {
                $("#modalSpinner").show();
                $(target).hide();
            },
            complete: function () {
                $("#modalSpinner").hide();
                $(target).show();
            },
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
                    callback();
                },
                206: function (data) {
                    if (refreshOverlay === "true") {
                        $(".modalContainer").removeClass('active');
                        $(target).html(data);
                        $(".modalContainer").addClass('active');
                    } else {
                        $(target).html(data);
                    }
                    callback();
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

function onKeyPress(event, refreshOverlay, source, targetID, runBeforeSubmit) {
    console.log('onKeyPress')
    if (event.keyCode === 13) {
        event.preventDefault();
        console.log('runBeforeSubmit 1');
        runBeforeSubmit();
        console.log('runBeforeSubmit 2');
        submitForm(refreshOverlay, source, targetID);
    }
}

function onKeyUp(source, submitButtonId) {
    const form = $(source).closest("form");
    let allFiled = checkAllFieldsFilled(form);
    if (allFiled) {
        $('#' + submitButtonId).removeClass("disable");
    } else {
        $('#' + submitButtonId).addClass("disable");
    }
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