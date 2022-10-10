$('.steperContainer ul li').on('click', function () {
    $('.steperContainer ul li').removeClass();
    $(this).addClass("active");
    $(this).prevAll().addClass("visited");
});