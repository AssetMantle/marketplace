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
    // var copyText = $(e).prevAll('.username-data');
    var copyText = $(e).prevAll('.username-data').attr("data-value");

    if ($(window).width() > 1200) {
        element.addClass("active");
        element.one('webkitAnimationEnd oanimationend msAnimationEnd animationend', function () {
            element.removeClass('active');
        });
    }

    navigator.clipboard.writeText(copyText);
}

function changeActive(address) {
    let route = jsRoutes.controllers.AccountController.changeActiveKey(address);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {

            },
            400: function (data) {
                console.log("invalid");
            }
        }
    });
}