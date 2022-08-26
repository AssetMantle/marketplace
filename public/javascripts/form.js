// Sign In / Sign Up Popup Box
elements = $('.modal-overlay, .modalContainer');

function closeModal() {
    elements.removeClass('active');
    $("body").removeClass("modal-active");
}