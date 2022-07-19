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

// Open New Address Option
$("#newAddressOptionOpenBtn").click(function(){
    $("#newAddressOption").addClass("open");
});

// Close New Address Option
$("#newAddressOptionCloseBtn").click(function(){
    $("#newAddressOption").removeClass("open");
});

// Open New Address Form
$("#newAddressFormOpenBtn").click(function(){
    $("#newAddressForm").addClass("open");
});

// Close New Address Form
$("#newAddressFormCloseBtn").click(function(){
    $("#newAddressForm").removeClass("open");
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
