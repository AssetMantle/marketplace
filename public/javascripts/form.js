// Sign In / Sign Up Popup Box
var elements = $('.modal-overlay, .modalContainer');

// $('button').click(function(){
//     elements.addClass('active');
// });
//
// $('.close-modal').click(function(){
//     elements.removeClass('active');
// });

function closeModal(){
    elements.removeClass('active');
    $("body").removeClass("modal-active");
}