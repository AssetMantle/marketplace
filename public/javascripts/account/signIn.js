reCaptchaFlag = false;

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

$(document).ready(function () {
    $('#FORM_SIGN_IN_WITH_CALLBACK_SUBMIT').addClass("disable");
})

function activeButton() {
    let signInUsername = $("#signInUsername").val();
    let signInPassword = $("#signInPassword").val();
    if (signInUsername !== "" && signInPassword !== "" && reCaptchaFlag === true) {
        $("#FORM_SIGN_IN_WITH_CALLBACK_SUBMIT").removeClass("disable");
    } else {
        $("#FORM_SIGN_IN_WITH_CALLBACK_SUBMIT").addClass("disable");
    }
}

$("#signInUsername, #signInPassword").on("input", function()
{
    activeButton();
});

var onSubmit = function(token) {
    reCaptchaFlag = true;
    activeButton();
};

<!-- Google reCaptcha -->
function CaptchaCallback() {
    if ( $('#myRecaptcha').length ) {
        grecaptcha.render('myRecaptcha', {'sitekey' : '6LfhFrwnAAAAAKKOVgEAdCHHtgalQj0PosHPn8Xu','callback' : onSubmit});
    }
}