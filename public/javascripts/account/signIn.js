function showPassword() {
    let password = $('#signInPassword')[0];
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

function activeButton() {
    let signInUsername = document.getElementById("signInUsername");
    let signInPassword = document.getElementById("signInPassword");

    let inputUsername = $("#signInUsername").val();
    let inputPassword = $("#signInPassword").val();

    let regexUsername = new RegExp("^[a-zA-Z0-9!@#$._-]*$");
    let regexPassword = new RegExp("^[A-Za-z0-9!@#$%^&*._-]*$");

    if(regexUsername.test(inputUsername)){
        $("#errorMessageUsername").slideUp();
    }else{
        $("#errorMessageUsername").slideDown();
    }

    if(regexPassword.test(inputPassword)){
        $("#errorMessagePassword").slideUp();
    }else{
        $("#errorMessagePassword").slideDown();
    }

    if ($.trim(signInUsername.value).length &&
        $.trim(signInPassword.value).length &&
        regexUsername.test(inputUsername) &&
        regexPassword.test(inputPassword)){
        $(".cmuk-button").removeClass("disable");
    } else {
        $(".cmuk-button").addClass("disable");
    }
}