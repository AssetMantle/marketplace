function checkUserAvailability(){
    let validUser = {users:['jaggu','pratik']};
    var field = document.getElementById("signInUsername");
    if(field.value !== ''){
        if(validUser.users.includes(field.value)){
            $("#incorrectUsernameError").slideDown();
            $('#signInUsername').css("border-color","var(--error)");
        }
        else{
            $("#incorrectUsernameError").slideUp();
            $('#signInUsername').css("border-color","var(--inactive-gray)");
        }
    }
}
