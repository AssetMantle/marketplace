// Show Password as Asterisk
$(document).ready(function (){
   let data = $("#userPassword").text();
   let pass = '';
    for(let i=0; i<data.length; i++){
        pass+="*";
    }
    $("#userPassword").text(pass);
});

// Wallet Popup Menu
$(".menuToolTip").click(function (){
    if($(this).hasClass("active")){
        $(this).removeClass("active");
    }
    else{
        $(".menuToolTip").removeClass("active");
        $(this).addClass("active");
    }
});

// Active Wallet Name
$('.walletInformation input').on('change', function() {
    $(".walletInfoField").removeClass("active");
    let parent = $(this).parents('.walletInfoField').last().addClass("active");
});

// Copy to Clipboard
function copyToClipboard(e) {
    var element = $(e).next('.form-copy-message');
    var copyText = $(e).prevAll('.username-data');

    element.addClass("active");
    element.one('webkitAnimationEnd oanimationend msAnimationEnd animationend', function () {
        element.removeClass('active');
    });

    navigator.clipboard.writeText(copyText.text());
}