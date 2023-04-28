// Goto top on load
document.onload = function () {
    window.scrollTo({ top:0, left:0, behavior: "instant"});
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
    route = jsRoutes.controllers.WalletController.gasTokenPrice();
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

function commonCollectionCardInfo(collectionId, id) {
    let route = jsRoutes.controllers.CollectionController.commonCardInfo(collectionId, id);
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

function setSoldNFTProgressBar(){
    let progressBar = document.querySelector('.progressBar > span');
    let totalNFTs = progressBar.getAttribute("data-totalNFT");
    let soldNFTs = progressBar.getAttribute("data-soldNFT");
    let progress = (soldNFTs*100) / totalNFTs;
    let soldPercentage = document.querySelector(".analysisTitle .analysisPercentage")
    soldPercentage.textContent = "("+progress.toFixed(2)+"%)";
    for(let i = 0; i < progress; i++) {
        progressBar.style.width = i + '%';
    }
}

// Skeleton Loading
function showLoader() {
    $('.skeletonLoadContainer img').one('load', function () {
        $(this).parent().closest('.skeletonLoadContainer').addClass("hide");
        $(this).addClass("show");
    });
}

// Banner
function setBanner(){
    $("#bannerContent").html("").html($(".bannerContainer"));
}

// Audio NFT
// Set Audio Duration
function setDuration(e){
    event.preventDefault();
    let player = $(e).closest(".audioNftContainer").find(".audioPlayer");
    $(".time .length").text(getTimeCodeFromNum($(player)[0].duration));
}

// Play/Pause Audio
function playToggle(e) {
    event.preventDefault();
    let player = $(e).closest(".audioNftContainer").find(".audioPlayer");
    let buttonIcon = $(e).find("i");
    if ($(player)[0].paused === false) {
        $(player)[0].pause();
        $(buttonIcon).attr("class","bi bi-play");
    } else {
        $(player)[0].play();
        $(buttonIcon).attr("class","bi bi-pause");
    }
    if($(".timelineContainer").length) {
        setInterval(() => {
            let progressBar = $(e).closest(".controls").find(".progress");
            $(progressBar).css("width",$(player)[0].currentTime / $(player)[0].duration * 100 + "%");
            $(".time .current").text(getTimeCodeFromNum($(player)[0].currentTime));
        }, 100);
    }
}

// Audio Volume
function setVolume(e){
    event.preventDefault();
    let player = $(e).closest(".audioNftContainer").find(".audioPlayer");
    let buttonIcon = $(e).closest(".volumeContainer").find("i");
    const sliderWidth = window.getComputedStyle(e).width;
    const newVolume = event.offsetX / parseInt(sliderWidth);
    $(player)[0].volume = newVolume;
    $(player)[0].muted = false;
    if(newVolume >= 0.5){
        $(buttonIcon).attr("class","bi bi-volume-up");
    }
    else if(newVolume < 0.5 && newVolume > 0){
        $(buttonIcon).attr("class","bi bi-volume-down");
    }
    else{
        $(buttonIcon).attr("class","bi bi-volume-mute");
    }
    $(e).find(".volumePercentage").css("width", newVolume * 100 + '%');
}

// Mute/Unmute Audio
function muteVolume(e){
    event.preventDefault();
    let player = $(e).closest(".audioNftContainer").find(".audioPlayer");
    let buttonIcon = $(e).closest(".volumeContainer").find("i");
    let volumeSeekBar = $(e).next(".volumeSlider").find(".volumePercentage");

    $(player)[0].muted = !$(player)[0].muted;
    if ($(player)[0].muted) {
        $(buttonIcon).attr("class","bi bi-volume-mute");
        $(volumeSeekBar).css("width", '0%');
    } else {
        if($(player)[0].volume >= 0.5){
            $(buttonIcon).attr("class","bi bi-volume-up");
        }
        else if($(player)[0].volume < 0.5 && $(player)[0].volume > 0){
            $(buttonIcon).attr("class","bi bi-volume-down");
        }
        else{
            $(buttonIcon).attr("class","bi bi-volume-mute");
        }
        $(volumeSeekBar).css("width", $(player)[0].volume * 100 +'%');
    }
}

// Audio Timeline
function skipAudio(e){
    event.preventDefault();
    let player = $(e).closest(".audioNftContainer").find(".audioPlayer");
    const timelineWidth = window.getComputedStyle(e).width;
    const timeToSeek = event.offsetX / parseInt(timelineWidth) * $(player)[0].duration;
    $(player)[0].currentTime = timeToSeek;
}

// Convert Audio time from second to min
function getTimeCodeFromNum(num) {
    let seconds = parseInt(num);
    let minutes = parseInt(seconds / 60);
    seconds -= minutes * 60;
    const hours = parseInt(minutes / 60);
    minutes -= hours * 60;

    if (hours === 0) return `${minutes}:${String(seconds % 60).padStart(2, 0)}`;
    return `${String(hours).padStart(2, 0)}:${minutes}:${String(
        seconds % 60
    ).padStart(2, 0)}`;
}

// Reset Audio Player
function resetAudioPlayer(e){
    let player = $(e);
    let buttonIcon = $(e).next(".controls").find(".playButton i");
    if ($(player)[0].ended) {
        $(buttonIcon).attr("class","bi bi-play");
    }

    if($(".timelineContainer").length) {
        let progressBar = $(e).next(".controls").find(".timelineContainer .timeline .progress");
        // console.log($(player)[0].currentTime);
        // console.log($(player)[0].duration * 100);
        // $(progressBar).css("width",$(player)[0].currentTime / $(player)[0].duration * 100 + "%");
        // $(".time .current").text("0:00");
    }
}