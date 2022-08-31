// Show Password as Asterisk
$(document).ready(function () {
    let data = $("#userPassword").text();
    let pass = '';
    for (let i = 0; i < data.length; i++) {
        pass += "*";
    }
    $("#userPassword").text(pass);
});

// Wallet Popup Menu
$(".menuToolTip").click(function () {
    if ($(this).hasClass("active")) {
        $(this).removeClass("active");
    } else {
        $(".menuToolTip").removeClass("active");
        $(this).addClass("active");
    }
});

// Active Wallet Name
$('.walletInformation input').on('change', function () {
    $(".walletInfoField").removeClass("active");
    let parent = $(this).parents('.walletInfoField').last().addClass("active");
});

// Address Shorter
var addresses = document.querySelectorAll('.username-data');
addresses.forEach(address => {
    $(address).text($(address).text().substr(0, 8) + "..." + $(address).text().substr($(address).length - 8));
});

// Copy to Clipboard
function copyToClipboard(e) {
    var element = $(e).next('.form-copy-message');
    var copyText = $(e).prevAll('.username-data').attr("data-value");

    if ($(window).width() > 1200) {
        element.addClass("active");
        element.one('webkitAnimationEnd oanimationend msAnimationEnd animationend', function () {
            element.removeClass('active');
        });
    }
    navigator.clipboard.writeText(copyText);
}

// Open/Close wallet screens
function openCloseWalletScreen(e, elementID){
    $(e).parent().closest(".walletPopupContainer").find(`#${elementID}`).toggleClass("open");
}

function changeActive(setAddress, oldAddress) {
    let route = jsRoutes.controllers.AccountController.changeActiveKey(setAddress);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $(setAddress).prop("checked", true);
                $(oldAddress).prop("checked", false);
            },
            400: function (data) {
            },
            204: function (data) {

            },
        }
    });
}

// Edit Bio
function initialCharacterCounter(maxLimit)
{
    $("#remainingCharacterLabel").text(maxLimit +"/"+maxLimit+" characters remaining");
}
function characterCounter(field, maxLimit)
{
    if (field.value.length > maxLimit) {
        field.value = field.value.substring( 0, maxLimit );
        return false;
    } else {
        $("#remainingCharacterLabel").text(maxLimit - field.value.length +"/"+maxLimit+" characters remaining");
    }
}

// Show/Hide social media field value
function showHideUsername() {
    let password = $('#instagramUsername')[0];
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