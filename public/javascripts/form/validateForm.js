function validateForm(form) {

    let formValidationBoolean = true;

    form.find("dl").each(function () {
            let fieldName = "";
            const dlElement = $(this);
            try {
                dlElement.find(".error").remove();
            } catch {
            }

            if (dlElement.find(("textarea"))[0] !== undefined) {
                const inputElement = dlElement.find("textarea")[0];
                const inputValue = inputElement.value;
                fieldName = inputElement.getAttribute("name");
                if ((inputElement.getAttribute("required") === "false" && inputValue === "") || inputElement.disabled === true) {
                    return;
                }
                dlElement.find(".info").each(function () {

                        const ddInfoElement = $(this)[0];
                        const ddValidationInfo = ddInfoElement.innerHTML.split(": ");
                        switch (ddValidationInfo[0]) {
                            case "Minimum length":
                                if (inputValue.length < parseInt(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    $('#minimumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#minimumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Maximum length":
                                if (inputValue.length > parseInt(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    $('#maximumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#maximumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            default :
                                const newRegEx = new RegExp(ddInfoElement.innerHTML);
                                if (!(newRegEx.test(inputValue))) {
                                    formValidationBoolean = false;
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
                                    $('#minimumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#minimumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Maximum length":
                                if (inputValue.length > parseInt(ddValidationInfo[1].replace(/,/g, ""))) {
                                    formValidationBoolean = false;
                                    $('#maximumFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#maximumFieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            case "Date ('yyyy-MM-dd')":
                                if (inputValue === "") {
                                    formValidationBoolean = false;
                                    $('#fieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#fieldError_' + fieldName).addClass("hidden");
                                }
                                break;
                            default :
                                const newRegEx = new RegExp(ddInfoElement.innerHTML);
                                if (!(newRegEx.test(inputValue))) {
                                    formValidationBoolean = false;
                                    $('#regexFieldError_' + fieldName).removeClass("hidden");
                                } else {
                                    $('#regexFieldError_' + fieldName).addClass("hidden");
                                }
                        }
                    }
                );
            }
        }
    )
    ;
    return formValidationBoolean;
}

function checkAllFieldsFilled(form) {

    let allFilled = true;

    form.find("dl").each(function () {
            const dlElement = $(this);

            if (dlElement.find(("textarea"))[0] !== undefined) {
                const inputElement = dlElement.find("textarea")[0];
                const inputValue = inputElement.value;
                if ((inputElement.getAttribute("required") === "false" && inputValue === "") || inputElement.disabled) {
                    return;
                }
                // TODO Check if minimum length is not 0;
                if (inputValue === "") {
                    allFilled = false;
                }
            } else if (dlElement.find(("select"))[0] !== undefined) {
                let selectElement = dlElement.find(("select"))[0];
                if (selectElement.value === "") {
                    allFilled = false;
                }
            } else {
                const inputElement = dlElement.find("input")[0];
                const inputValue = inputElement.value;

                if ((inputElement.getAttribute("required") === "false" && inputValue === "") || inputElement.disabled) {
                    return;
                }

                switch (inputElement.type) {
                    case "number":
                        if (inputValue === "" || isNaN(inputValue)) {
                            allFilled = false;
                        }
                        break;
                    case "text":
                    case "password":
                        // TODO Check if minimum length is not 0;
                        if (inputValue === "") {
                            allFilled = false;
                        }
                        break;
                    case "date":
                        if (inputValue === "") {
                            allFilled = false;
                        }
                        break;
                }
            }
        }
    );
    return allFilled;
}