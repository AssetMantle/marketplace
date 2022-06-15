function checkUserAvailability(){
    let validUser = {users:['jaggu','pratik']};
    var field = document.getElementById("signUpUsername");
    if(field.value !== ''){
        if(validUser.users.includes(field.value)){
            $(".checkIcon").fadeOut();
            $("#usernameAvailableError").slideDown();
            $('#signUpUsername').css("border-color","var(--error)");
        }
        else{
            $(".checkIcon").fadeIn();
            $("#usernameAvailableError").slideUp();
            $('#signUpUsername').css("border-color","var(--default)");
        }
    }
    else{
        $(".checkIcon").fadeOut();
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
        $('#signUpConfirmPassword').css("border-color","var(--error)");
    } else {
        $("#repeatPasswordError").hide(300);
        $('#signUpConfirmPassword').css("border-color","var(--default)");
    }
}