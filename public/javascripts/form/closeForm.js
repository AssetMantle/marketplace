let elements = $('.modal-overlay, .modalContainer');

function closeModal(){
    $(".modal-overlay").removeClass("active");
    $(".modalContainer").removeClass("active");
    $("body").removeClass("modal-active");
}