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
$("#userWallet").click(function(){
   $("#walletPopup").addClass("active");
   $("#walletBackDrop").addClass("active");
   $("body").addClass("modal-open");
});

// Close wallet popup
$("#walletBackDrop").click(function (){
    $("#walletPopup").removeClass("active");
    $("#walletBackDrop").removeClass("active");
    $("body").removeClass("modal-open");
    setTimeout(()=>{
        $("#walletMenu").removeClass("open");
        $("#addressBook").removeClass("open");
    },200);
});

// Open wallet menu
$("#openWalletMenu").click(function(){
   $("#walletMenu").addClass("open");
});

// Close wallet menu
$("#closeWalletMenu").click(function(){
    $("#walletMenu").removeClass("open");
});

// Open Address Book
$("#addressBookOpenBtn").click(function(){
    $("#addressBook").addClass("open");
});

// Close Address Book
$("#addressBookCloseBtn").click(function(){
    $("#addressBook").removeClass("open");
});

function showOptions(current)
{
    console.log($(current));
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
    // });
}

function findParent(element, parentclass){
    for (var i = 0; i < 10; i++) { if($(element).hasClass(parentclass)) return $(element); else element = $(element).parent(); }
}

function setOption(currentOption){
    var parent = findParent($(currentOption), "dropdown");
    var selectedItem = $(currentOption);

    $(parent).removeClass("is-open");
    $(".dropdownHead").removeClass("active");
    $(parent).find(".dropdownBodyInner").slideUp(400);

    changeSelected(parent, selectedItem);
}
function changeSelected(parent, selectedItem){
    $(parent).find(".currentSelected").text($(selectedItem).text());
    $(parent).find("input.dp-input").val($(selectedItem).attr("value"));
}
// $(function(){
//     // $(".dropdown .dropdownHead").click(function(){
//     function showOptions(current)
//     {
//         console.log($(current));
//         if ($(current).hasClass("active")) {
//             $(current).removeClass("active");
//         } else {
//             $(current).addClass("active");
//         }
//
//         var parent = findParent($(current), "dropdown");
//
//         $(parent).toggleClass("is-open");
//         if ($(parent).hasClass("is-open"))
//             $(parent).find(".dropdownBodyInner").slideDown(400);
//         else
//             $(parent).find(".dropdownBodyInner").slideUp(400);
//         // });
//     }
//
//     $(".dropdownItem").click(function(){
//         if($(".dropdown .dropdownHead").hasClass("active")) {
//             $(".dropdown .dropdownHead").removeClass("active");
//         }
//         else{
//             $(".dropdown .dropdownHead").addClass("active");
//         }
//
//         var parent = findParent($(this), "dropdown");
//         var selectedItem = $(this);
//
//         $(parent).removeClass("is-open");
//         $(parent).find(".dropdownBodyInner").slideUp(400);
//
//         changeSelected(parent, selectedItem);
//     });
//
//     function findParent(element, parentclass){
//         for (var i = 0; i < 10; i++) { if($(element).hasClass(parentclass)) return $(element); else element = $(element).parent(); }
//     }
//
//     function changeSelected(parent, selectedItem){
//         $(parent).find(".currentSelected").text($(selectedItem).text());
//         $(parent).find("input.dp-input").val($(selectedItem).attr("value"));
//     }
// });

// Address Shorter
var addresses = document.querySelectorAll('.username-data');
addresses.forEach(address => {
    $(address).text($(address).text().substr(0,8) + "..." + $(address).text().substr($(address).length - 8));
});

// Copy to Clipboard
function copyToClipboard(e) {
    var element = $(e).next('.form-copy-message');
    // var copyText = $(e).prevAll('.username-data');
    var copyText = $(e).prevAll('.username-data').attr("data-value");

    element.addClass("active");
    element.one('webkitAnimationEnd oanimationend msAnimationEnd animationend', function () {
        element.removeClass('active');
    });

    // navigator.clipboard.writeText(copyText.text());
    navigator.clipboard.writeText(copyText);
}

$(document).click(function (e) {
    if ($(e.target).is('#commonModal')) {
        $(".modal-overlay").removeClass("active");
        $(".modalContainer").removeClass("active");
        $("body").removeClass("modal-active");
    }
});