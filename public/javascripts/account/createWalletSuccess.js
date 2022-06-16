$(document).ready(function() {
    if ($('.form-accordion').length) {
        $('.form-accordion').find('.accordion-toggle').click(function() {
            if ($(this).hasClass('open')) {
                $(this).next().slideUp('fast');
                $(this).removeClass('open');
            } else {
                $(this).next().slideDown('fast');
                $(this).addClass('open');
                $(".accordion-content").not($(this).next()).slideUp('fast');
                $(".accordion-toggle").not($(this)).removeClass('open');
            }
        });
    }

    setTimeout(()=>{
        setTimeout(()=>{
            $(".form-hidden").addClass('show');
        },500);
        $(".form-hidden").slideDown(1000);
    },1000);

});