$(window).resize(function() {
    if($(document).width() <= 766) {
        changeColumnOrder();
    }else{
        resetColumnOrder();
    }
});

$(document).ready(function() {
    if($(document).width() <= 766) {
        changeColumnOrder();
    }else{
        resetColumnOrder();
    }
});

function openPropertyList() {
    $("#showHidePropertyButton").toggleClass("active");
    $(".propertyListContainer").slideToggle();
}

function changeColumnOrder(){
    $("#leftContent").addClass("order-3");
    $("#centerContent").addClass("order-1");
    $("#rightContent").addClass("order-2");
    $("#showHidePropertyButton").addClass("active");
    $(".propertyListContainer").slideUp();
}

function resetColumnOrder(){
    $("#leftContent").removeClass("order-3");
    $("#centerContent").removeClass("order-1");
    $("#rightContent").removeClass("order-2");
    $("#showHidePropertyButton").removeClass("active");
    $(".propertyListContainer").slideDown();
}