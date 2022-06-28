// Hide submit button
$("#formSubmitButton").hide();
$("#modalBackButton").hide();

function showPassword() {
    let password = $('#forgotNewPassword')[0];
    let matchPassword = $('#forgotNewConfirmPassword')[0];
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
    // var completeRegularExpression = /^(?=.*\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,128}$/;

    let numberMatchPattern = passwordValue.match(/\d+/g);
    const isUpperCase = (x) => /[A-Z]/.test(x);
    let isSpecialCharacter = (x) => /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/.test(x);

    if(passwordValue.length > 7 && passwordValue.length < 129) { $(".error-info-1 .error-icon").addClass('active'); flag1=0; } else{ $(".error-info-1 .error-icon").removeClass('active'); flag1=1;}
    if(numberMatchPattern != null) { $(".error-info-2 .error-icon").addClass('active'); flag2=0; } else { $(".error-info-2 .error-icon").removeClass('active'); flag2=1; }
    if(isUpperCase(passwordValue)) { $(".error-info-3 .error-icon").addClass('active'); flag3=0; } else { $(".error-info-3 .error-icon").removeClass('active'); flag3=1; }
    if(isSpecialCharacter(passwordValue)) { $(".error-info-4 .error-icon").addClass('active'); flag4=0;} else { $(".error-info-4 .error-icon").removeClass('active'); flag4=1;}
    if(flag1 == 1 || flag2 == 1 || flag3 == 1 || flag4 == 1)
    {
        $(".error-message").slideDown();
    }else{
        $(".error-message").slideUp();
    }
}
$("#modalBackButton").click(function (){
    $("#modalBackButton").hide();
});

function showHideForgotModalScreen(showScreen, hideScreen, pageNumber) {
    $(".modalContainer").removeClass('active');
    setTimeout(function () {
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
    }, 1000);
}

