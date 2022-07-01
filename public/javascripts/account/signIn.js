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