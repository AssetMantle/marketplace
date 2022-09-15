function validateForm(form) {

    let formValidationBoolean = true;

    form.find("dl").each(function () {
            let fieldName = "";
            let fieldError = false;
            const dlElement = $(this);
            try {
                dlElement.find(".error").remove();
            } catch {
            }

            if (dlElement.find(("textarea"))[0] !== undefined) {
                const inputElement = dlElement.find("textarea")[0];
                const inputValue = inputElement.value;
                fieldName = inputElement.getAttribute("name");

                dlElement.find(".info").each(function () {
                        const ddInfoElement = $(this)[0];
                        const ddValidationInfo = ddInfoElement.innerHTML.split(": ");
                        switch (ddValidationInfo[0]) {
                            case "Minimum length":
                                if (inputValue.length < parseInt(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#minimumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#minimumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Maximum length":
                                if (inputValue.length > parseInt(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#maximumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#maximumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            default :
                                const newRegEx = new RegExp(ddInfoElement.innerHTML);
                                if (!(newRegEx.test(inputValue))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#regexFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#regexFieldError_' + fieldName).addClass("hidden");
                                }
                        }
                    }
                );
            } else if (dlElement.find(("select"))[0] !== undefined) {
                let selectElement = dlElement.find(("select"))[0];
                fieldName = selectElement.getAttribute("name");
                if (!selectElement.disabled && selectElement.value === "") {
                    formValidationBoolean = false;
                    fieldError = true;
                    $('#fieldError_' + fieldName).removeClass("hidden");
                } else {
                    $('#fieldError_' + fieldName).addClass("hidden");
                }
            } else {
                const inputElement = dlElement.find("input")[0];
                const inputValue = inputElement.value;
                inputElement.classList.remove("errorInput");
                if (inputElement.type === "checkbox" || (inputElement.getAttribute("required") === "false" && inputValue === "") || inputElement.disabled === true) {
                    return;
                }
                fieldName = inputElement.getAttribute("name");
                dlElement.find(".info").each(function () {
                        const ddInfoElement = $(this)[0];
                        const ddValidationInfo = ddInfoElement.innerHTML.split(": ");
                        switch (ddValidationInfo[0]) {
                            case "Numeric":
                                if (inputValue === "" || isNaN(inputValue)) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#minimumFieldError_' + fieldName).removeClass("hidden");
                                    $('#maximumFieldError_' + fieldName).removeClass("hidden");
                                    $('#customFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#minimumFieldError_' + fieldName).addClass("hidden");
                                    $('#maximumFieldError_' + fieldName).addClass("hidden");
                                    $('#customFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Real":
                                if (inputValue === "" || isNaN(inputValue)) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#minimumFieldError_' + fieldName).removeClass("hidden");
                                    $('#maximumFieldError_' + fieldName).removeClass("hidden");
                                    $('#customFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#minimumFieldError_' + fieldName).addClass("hidden");
                                    $('#maximumFieldError_' + fieldName).addClass("hidden");
                                    $('#customFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Minimum value":
                                if (inputValue < parseFloat(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#minimumFieldError_' + fieldName).removeClass("hidden");
                                    $('#customFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#minimumFieldError_' + fieldName).addClass("hidden");
                                    $('#customFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Maximum value":
                                if (inputValue > parseFloat(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#maximumFieldError_' + fieldName).removeClass("hidden");
                                    $('#customFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#maximumFieldError_' + fieldName).addClass("hidden");
                                    $('#customFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Minimum length":
                                if (inputValue.length < parseInt(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#minimumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#minimumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Maximum length":
                                if (inputValue.length > parseInt(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#maximumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#maximumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Date ('yyyy-MM-dd')":
                                if (inputValue === "") {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#fieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#fieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            default :
                                const newRegEx = new RegExp(ddInfoElement.innerHTML);
                                if (!(newRegEx.test(inputValue))) {
                                    formValidationBoolean = false;
                                    fieldError = true;
                                    $('#regexFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#regexFieldError_' + fieldName).addClass("hidden");
                                }
                        }
                    }
                );
            }
            if (fieldError) {
                $("#" + fieldName).addClass("errorField");
            } else {
                $("#" + fieldName).removeClass("errorField");
            }
            let nonEpochField = $("#" + fieldName.replace("epoch_", ""));
            if (nonEpochField.length !== 0 && nonEpochField.hasClass("errorField")) {
                $("#" + fieldName).addClass("errorField");
            }
        }
    )
    ;
    return formValidationBoolean;
}