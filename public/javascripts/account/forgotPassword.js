// Hide submit button
$("#formSubmitButton").hide();
$("#modalBackButton").hide();
$("#navigationButtonPage0").addClass("disable");
$("#navigationButtonPage1").addClass("disable");
$("#formSubmitButton button").removeClass("disable");

function showPassword() {
    let password = $('#forgotNewPassword')[0];
    let matchPassword = $('#forgotNewConfirmPassword')[0];
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
    let confirmPassword = $('#forgotNewConfirmPassword').val();
    let password = $('#forgotNewPassword').val();
    if (confirmPassword !== password) {
        $("#repeatPasswordError").show(300);
        $('#forgotNewConfirmPassword').css("border-color","var(--error)");
    } else {
        $("#repeatPasswordError").hide(300);
        $('#forgotNewConfirmPassword').css("border-color","var(--default)");
    }
}

function checkNewPassword(){
    var flag1,flag2,flag3,flag4 = 0;
    let passwordValue = document.getElementById('forgotNewPassword').value;
    if($.trim(passwordValue).length) {
        // var completeRegularExpression = /^(?=.*\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,128}$/;

        let numberMatchPattern = passwordValue.match(/\d+/g);
        const isUpperCase = (x) => /[A-Z]/.test(x);
        let isSpecialCharacter = (x) => /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/.test(x);

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
$("#modalBackButton").click(function (){
    $("#modalBackButton").hide();
});

function showHideForgotModalScreen(showScreen, hideScreen, pageNumber) {
    $(".modalContainer").removeClass('active');
    // setTimeout(function () {
        $(hideScreen).hide()
        if(pageNumber === 1){
            $("#modalBackButton").show();
            $("#modalSubtitle").text("Confirm your recovery phrase to reset your password.");
        }
        else if(pageNumber === 2){
            $("#modalBackButton").hide();
            $("#modalSubtitle").text("Enter a new password.");
            $("#formSubmitButton").show();
        }

        $(showScreen).show();
        $(".modalContainer").addClass('active');
    // }, 1000);
}

function enableNavigationButton(pageNumber) {
    $("#formSubmitButton button").addClass("disable");
    if(pageNumber === 0) {
        let flag = 0;
        let username = document.getElementById("username");
        let walletAddress = document.getElementById("walletAddress");
        if($.trim(username.value).length && ($.trim(username.value).length < 3 || $.trim(username.value).length > 50)){
            $("#username").css("border-color", "var(--error)");
            $("#usernameError").show(300);
            flag = 1;
        }
        else {
            $("#username").css("border-color", "var(--default)");
            $("#usernameError").hide(300);
        }
        if ($.trim(walletAddress.value).length && $.trim(walletAddress.value).length !== 45) {
            $("#walletAddress").css("border-color", "var(--error)");
            $("#walletAddressError").show(300);
            flag = 1;
        }
        else {
            $("#walletAddress").css("border-color", "var(--default)");
            $("#walletAddressError").hide(300);
        }
        if ($.trim(username.value).length && $.trim(walletAddress.value).length && flag === 0) {
            $("#navigationButtonPage0").removeClass("disable");
        } else {
            $("#navigationButtonPage0").addClass("disable");
        }
    }
    else if(pageNumber === 1){
        let flag = 0;
        let phrase1 = document.getElementById("phrase1");
        let phrase2 = document.getElementById("phrase2");
        let phrase3 = document.getElementById("phrase3");
        let phrase4 = document.getElementById("phrase4");
        if(($.trim(phrase1.value).length && ($.trim(phrase1.value).length < 3 || $.trim(phrase1.value).length > 20)) ||
            ($.trim(phrase2.value).length && ($.trim(phrase2.value).length < 3 || $.trim(phrase2.value).length > 20)) ||
            ($.trim(phrase3.value).length && ($.trim(phrase3.value).length < 3 || $.trim(phrase3.value).length > 20)) ||
            ($.trim(phrase4.value).length && ($.trim(phrase4.value).length < 3 || $.trim(phrase4.value).length > 20))){
                $("#phraseError").show(300);
                flag = 1;
        }else{
            $("#phraseError").hide(300);
        }
        if ($.trim(phrase1.value).length && $.trim(phrase2.value).length && $.trim(phrase3.value).length && $.trim(phrase4.value).length && flag === 0) {
            $("#navigationButtonPage1").removeClass("disable");
        } else {
            $("#navigationButtonPage1").addClass("disable");
        }
    }
    else if(pageNumber === 2){

        let forgotNewPassword = document.getElementById("forgotNewPassword");
        let forgotNewConfirmPassword = document.getElementById("forgotNewConfirmPassword");
        if ($.trim(forgotNewPassword.value).length && $.trim(forgotNewConfirmPassword.value).length) {
            $("#formSubmitButton button").removeClass("disable");
        } else {
            $("#formSubmitButton button").addClass("disable");
        }
    }
}

function checkWalletAddress(){
    var value = $("#walletAddress").val();
    if (value.length !== 45) {
        $("#walletAddress").css("border-color", "var(--error)");
        $("#walletAddressError").show(300);
    }
    else {
        $("#walletAddress").css("border-color", "var(--default)");
        $("#walletAddressError").hide(300);
    }
}