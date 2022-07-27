$("#formSubmitButton .buttonPrimary").addClass("disable");
$("#oldPassword, #changePassword, #changeConfirmPassword").keyup(function (){
    if ($.trim(oldPassword.value).length && $.trim(changePassword.value).length && $.trim(changeConfirmPassword.value).length) {
        $("#formSubmitButton .buttonPrimary").removeClass("disable");
    } else {
        $("#formSubmitButton .buttonPrimary").addClass("disable");
    }
});

function showPassword() {
    let password = $('#changePassword')[0];
    let matchPassword = $('#changeConfirmPassword')[0];
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
    let confirmPassword = $('#changeConfirmPassword').val();
    let password = $('#changePassword').val();
    if (confirmPassword !== password) {
        $("#repeatPasswordError").show(300);
        $('#changeConfirmPassword').css("border-color","var(--error)");
    } else {
        $("#repeatPasswordError").hide(300);
        $('#changeConfirmPassword').css("border-color","var(--default)");
    }
}

function checkNewPassword(){
    var flag1,flag2,flag3,flag4 = 0;
    let passwordValue = document.getElementById('changePassword').value;
    if($.trim(passwordValue).length) {

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