timer = 0;
timeoutFlag = true;

function checkUsernameAvailable(source, usernameAvailableCheckBoxID) {
    if (timeoutFlag) {
        timeoutFlag = false;
        clearTimeout(timer);
        timer = setTimeout(function () {
            timeoutFlag = true;
            const username = $(source).val();
            const usernameAvailableCheckBox = $(usernameAvailableCheckBoxID);
            const route = jsRoutes.controllers.AccountController.checkUsernameAvailable(username);
            let loadingSpinner = $('#usernameAvailableLoading');
            if (username.length > 0) {
                $.ajax({
                    url: route.url,
                    type: route.type,
                    async: true,
                    beforeSend: function () {
                        loadingSpinner.show();
                    },
                    complete: function () {
                        loadingSpinner.hide();
                    },
                    statusCode: {
                        200: function () {
                            usernameAvailableCheckBox[0].checked = true;
                            $("#checkIcon").fadeIn();
                            $("#usernameAvailableError").hide(300);
                            $("#signUpUsername:focus").css("border-color", "var(--dark)");
                        },
                        204: function () {
                            usernameAvailableCheckBox[0].checked = false;
                            $("#checkIcon").fadeOut();
                            $("#usernameAvailableError").show(300);
                            $("#signUpUsername").css("border-color", "var(--error)");
                        },
                    }
                });
            } else {
                $("#checkIcon").hide();
            }
        }, 1500);
    }
}

function showPassword() {
    let password = $('#signUpPassword')[0];
    if (password.type === "password") {
        password.type = "text";
        $(".closeEye").addClass("hidden");
        $(".openEye").removeClass("hidden");
    } else {
        password.type = "password";
        $(".closeEye").removeClass("hidden");
        $(".openEye").addClass("hidden");
    }
}

function checkPasswords() {
    let confirmPassword = $('#signUpConfirmPassword').val();
    let password = $('#signUpPassword').val();
    if (confirmPassword !== password) {
        $("#repeatPasswordError").show(300);
        $('#signUpConfirmPassword').css("border-color", "var(--error)");
    } else {
        $("#repeatPasswordError").hide(300);
        $('#signUpConfirmPassword').css("border-color", "var(--default)");
    }
}

function checkNewPassword() {
    let flag1, flag2, flag3, flag4 = 0;
    let passwordValue = document.getElementById('signUpPassword').value;
    if ($.trim(passwordValue).length) {

        let numberMatchPattern = passwordValue.match(/\d+/g);
        const isUpperCase = (x) => /[A-Z]/.test(x);
        let isSpecialCharacter = (x) => /[ `!@@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/.test(x);

        if (passwordValue.length > 7 && passwordValue.length < 129) {
            $(".error-info-1 .error-icon").addClass('active');
            flag1 = 0;
        } else {
            $(".error-info-1 .error-icon").removeClass('active');
            flag1 = 1;
        }
        if (numberMatchPattern != null) {
            $(".error-info-2 .error-icon").addClass('active');
            flag2 = 0;
        } else {
            $(".error-info-2 .error-icon").removeClass('active');
            flag2 = 1;
        }
        if (isUpperCase(passwordValue)) {
            $(".error-info-3 .error-icon").addClass('active');
            flag3 = 0;
        } else {
            $(".error-info-3 .error-icon").removeClass('active');
            flag3 = 1;
        }
        if (isSpecialCharacter(passwordValue)) {
            $(".error-info-4 .error-icon").addClass('active');
            flag4 = 0;
        } else {
            $(".error-info-4 .error-icon").removeClass('active');
            flag4 = 1;
        }
        if (flag1 == 1 || flag2 == 1 || flag3 == 1 || flag4 == 1) {
            $(".error-message").slideDown();
        } else {
            $(".error-message").slideUp();
        }
    }
}

function activeButton() {
    let termsCondition = document.getElementById("termsCondition");
    if (termsCondition.checked === true) {
        $(".cmuk-button").removeClass("disable");
    } else {
        $(".cmuk-button").addClass("disable");
    }
}

function changeFieldBorder() {
    $("#signUpUsername:focus").css("border-color", "var(--dark)");
    $("#signUpUsername:not(:focus)").css("border-color", "var(--inactive-gray)");
}

$(document).ready(function () {
    $('#FORM_SIGN_UP_SUBMIT').addClass("disable");
})

