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

                    function clearFormElements(target_id) {
                        jQuery("#" + target_id).find(':input').each(function () {
                            switch (this.type) {
                                // case 'password':
                                // case 'text':
                                // case 'textarea':
                                // case 'file':
                                // case 'select-one':
                                // case 'select-multiple':
                                // case 'date':
                                // case 'number':
                                // case 'tel':
                                // case 'email':
                                //     jQuery(this).val('');
                                //     break;
                                case 'checkbox':
                                case 'radio':
                                    this.checked = false;
                                    break;
                            }
                        });
                    }

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

function submitFormOnEnter(event, isModal, source, targetID) {
    if (event.keyCode === 13) {
        event.preventDefault();
        submitForm(isModal, source, targetID);
    }
}