function showVerificationForm(){
    $(".modal").removeClass('active');
    setTimeout(function(){
        $(".wallet-mnemonics").hide()
        $("#verificationForm").show();
        $(".modal").addClass('active');
    },1000);
}