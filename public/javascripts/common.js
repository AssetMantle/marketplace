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
function addressShorter(message, fieldId, length) {
    $("#" + fieldId).text(message.substr(0, length) + "..." + message.substr($("#" + fieldId).length - length));
}

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

function epochToDate(elementId) {
    const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    let epochVal = $("#" + elementId).text();
    let dateTime = new Date(Number(epochVal));
    $("#" + elementId).text(months[dateTime.getMonth()] + " " + dateTime.getFullYear());
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

function getDollarPrice(mntlPrice, nftId) {
    route = jsRoutes.controllers.BlockchainTransactionController.gasTokenPrice();
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                let salePrice = mntlPrice;
                let currentMntlPrice = data;
                $("#dollarPrice_" + nftId.split(".")[0]).text("($" + (salePrice * currentMntlPrice).toFixed(2) + ")");
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
                if (data !== "--") {
                    $("#price_" + nftId.split(".")[0]).html(data);
                    getDollarPrice(data, nftId);
                }
            }
        }
    });
}

function commonCollectionCardInfo(collectionId, showPublicListingPrice) {
    let route = jsRoutes.controllers.CollectionController.commonCardInfo(collectionId, showPublicListingPrice);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                let totalData = data.split("|");
                $('#counCollectionNFTs_' + collectionId).html(totalData[0]);
                $('#collectionNFTsPrice_' + collectionId).html(totalData[1]);
                if (totalData[2] == 1) {
                    $("#saleBadge_" + collectionId + " .option").removeClass("active");
                    $("#saleBadge_" + collectionId + " .live").addClass("active");
                } else if (totalData[2] == 2) {
                    $("#saleBadge_" + collectionId + " .option").removeClass("active");
                    $("#saleBadge_" + collectionId + " .soldOut").addClass("active");
                } else if (totalData[2] == 3) {
                    $("#saleBadge_" + collectionId + " .option").removeClass("active");
                    $("#saleBadge_" + collectionId + " .ended").addClass("active");
                } else {
                    $("#saleBadge_" + collectionId + " .option").removeClass("active");
                }
            },
            400: function (data) {
                console.log(data.responseText)
            }
        }
    });
}

// Gas Toggle Button
function setGasOption(element, value) {
    $(".toggleOption").removeClass("active");
    $(element).addClass("active");
    $('#GAS_PRICE').val(value);
}

// Sharable Link
function setSharableLink(imageUrl){
    if(imageUrl !== "") {
        $('meta[name=sharableImage]').attr('content', imageUrl);
    }
}