let timer = 0;
let timeoutFlag = true;

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
                            $("#signUpUsername").css("border-color", "var(--dark)");
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
    let matchPassword = $('#signUpConfirmPassword')[0];
    if (password.type && matchPassword.type === "password") {
        password.type = "text";
        matchPassword.type = "text"
        $(".closeEye").addClass("hidden");
        $(".openEye").removeClass("hidden");
    } else {
        password.type = "password";
        matchPassword.type = "password";
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

function activeButton() {
    let termsCondition = document.getElementById("termsCondition");
    if (termsCondition.checked) {
        $(".cmuk-button").removeClass("disable");
    } else {
        $(".cmuk-button").addClass("disable");
    }
}

function changeFieldBorder() {
    $("#signUpUsername:focus").css("border-color", "var(--dark)");
    $("#signUpUsername:not(:focus)").css("border-color", "var(--inactive-gray)");
}

