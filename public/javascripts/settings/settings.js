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

// Open/Close wallet screens
function openCloseWalletScreen(e, elementID) {
    $(e).parent().closest(".walletPopupContainer").find(`#${elementID}`).toggleClass("open");
}

function changeActive(setAddress) {
    let oldAddress = $('.activeKey').attr("id");
    if (oldAddress !== setAddress) {
        let form = $('#changeActiveKeyForm');
        $('#WALLET_ADDRESS').val(setAddress);
        $.ajax({
            url: form.attr('action'),
            type: 'POST',
            data: form.serialize(),
            async: true,
            statusCode: {
                200: function (data) {
                    $('#' + oldAddress).removeClass("activeKey");
                    $('#' + setAddress).addClass("activeKey");
                    $('#changeActiveKeyForm').html(data);
                },
                400: function (data) {
                },
                204: function (data) {

                },
            }
        });
    } else {
        console.log("setting to same address");
    }
}

function fetchBalances(walletAddresses) {
    const addresses = walletAddresses.split(",");
    for (let i = 0; i < addresses.length; i++) {
        fetchBalance(addresses[i]);
    }
}

function fetchBalance(address) {
    let route = jsRoutes.controllers.SettingController.walletBalance(address);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $("#walletBalance_" + address).html(data);
            },
            400: function (data) {
                $("#walletBalance_" + address).html(data.responseText);
            }
        }
    });
}

function updateKeyName() {
    $('.walletInfoField').load(document.URL);
}

function showHideSeed() {
    allSeeds = $('.mnemonicValue');
    if (allSeeds[0].type === "password") {
        allSeeds.each(function () {
            $(this).attr("type", "text");
        });
        $(".closeEye").addClass("hidden");
        $(".openEye").removeClass("hidden");
    } else {
        allSeeds.each(function () {
            $(this).attr("type", "password");
        });
        $(".closeEye").removeClass("hidden");
        $(".openEye").addClass("hidden");
    }
}

function fetchKeys() {
    let route = jsRoutes.controllers.SettingController.walletPopupKeys();
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $('#walletAddressList').html(data);
            },
            400: function (data) {
            },
            204: function (data) {

            },
            500: function (data) {
                console.log(data.responseText)
            }
        }
    });
}