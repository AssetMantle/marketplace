// Goto top on load
document.onload = function () {
    window.scrollTo(0, 0);
}

// Hide modal backdrop
$(document).on('show.bs.modal', '.modal', function () {
    $(".modal-backdrop").remove();
});
$(document).on('hide.bs.modal', '.modal', function () {
    $("body").removeAttr("data-bs-overflow");
    $("body").removeAttr("data-bs-padding-right");
});

// Show wallet popup
function showWallet() {
    $("#walletPopup").addClass("active");
    $("#walletBackDrop").addClass("active");
    $("body").addClass("modal-open");
}

// Show notification popup
function showNotification() {
    $("#notificationPopup").addClass("active");
    $("#walletBackDrop").addClass("active");
    $("body").addClass("modal-open");
}

// Close wallet popup
function closeWallet() {
    $("#walletPopup, #notificationPopup").removeClass("active");
    $("#walletBackDrop").removeClass("active");
    $("body").removeClass("modal-open");
    setTimeout(() => {
        $("#walletMenu").removeClass("open");
        $("#addressBook").removeClass("open");
    }, 200);
}

// Resize window for wallet popup
$(window).resize(function () {
    if (($(window).width() <= 900) && ($("#walletPopup").hasClass("active"))) {
        $("#walletPopup").removeClass("active");
        $("#walletBackDrop").removeClass("active");
        $("body").removeClass("modal-open");
    }
});

function showOptions(current) {
    if ($(current).hasClass("active")) {
        $(current).removeClass("active");
    } else {
        $(current).addClass("active");
    }

    var parent = findParent($(current), "dropdown");

    $(parent).toggleClass("is-open");
    if ($(parent).hasClass("is-open"))
        $(parent).find(".dropdownBodyInner").slideDown(400);
    else
        $(parent).find(".dropdownBodyInner").slideUp(400);
}

function findParent(element, parentclass) {
    for (var i = 0; i < 10; i++) {
        if ($(element).hasClass(parentclass)) return $(element); else element = $(element).parent();
    }
}

function setOption(currentOption) {
    var parent = findParent($(currentOption), "dropdown");
    var selectedItem = $(currentOption);

    $(parent).removeClass("is-open");
    $(".dropdownHead").removeClass("active");
    $(parent).find(".dropdownBodyInner").slideUp(400);

    changeSelected(parent, selectedItem);
}

function changeSelected(parent, selectedItem) {
    $(parent).find(".currentSelected").text($(selectedItem).text());
    $(parent).find("input.dp-input").val($(selectedItem).attr("value"));
}

// Address Shorter
var addresses = document.querySelectorAll('.username-data');
addresses.forEach(address => {
    $(address).text($(address).text().substr(0, 8) + "..." + $(address).text().substr($(address).length - 8));
});

// Copy to Clipboard
function copyToClipboard(e) {
    var copyText = $(e).prevAll('.username-data').attr("data-value");
    navigator.clipboard.writeText(copyText);
}

$(document).click(function (e) {
    if ($(e.target).is('#commonModal')) {
        $(".modal-overlay").removeClass("active");
        $(".modalContainer").removeClass("active");
        $("body").removeClass("modal-active");
    }
});

// Close Modal
elements = $('.modal-overlay, .modalContainer');

function closeModal() {
    elements.removeClass('active');
    $("body").removeClass("modal-active");
}

function epochToDateTime(elementId) {
    let epochVal = $("#" + elementId).text();
    let epochSeconds = eval(epochVal * 1000);
    let dateTime = new Date(epochSeconds);

    $("#" + elementId).text(dateTime.toLocaleDateString() + " " + dateTime.toLocaleTimeString());
}

function loadSwitcherContent(divID) {
    $('#' + divID).click();
}

function showSnackbar(title, message, status) {
    let option = {
        title: title,
        message: message,
        status: status,
        timeout: 3000
    }

    Toast.create(option);
    Toast.setPlacement(TOAST_PLACEMENT.BOTTOM_RIGHT);
    Toast.setMaxCount(6);
}

// Truncate large string with ...
function truncate(message, fieldId, length) {
    let newMessage = (message.length > length) ? message.slice(0, length - 1) + '&hellip;' : message;
    $("#" + fieldId).html(newMessage);
}

function getDollarPrice(mntlPrice, nftId){
    route = jsRoutes.controllers.BlockchainTransactionController.gasTokenPrice();
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                let salePrice = mntlPrice;
                let currentMntlPrice = data;
                $("#dollarPrice_" + nftId.split(".")[0]).text("($" + (salePrice * currentMntlPrice).toFixed(5) + ")");
            }
        }
    });
}

function getNFTPrice(nftId) {
    let route = jsRoutes.controllers.NFTController.price(nftId);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                if(data !== "--") {
                    $("#price_" + nftId.split(".")[0]).html(data);
                    getDollarPrice(data, nftId);
                }
            }
        }
    });
}