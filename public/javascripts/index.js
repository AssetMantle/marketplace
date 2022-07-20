heroSwiper = new Swiper(".mySwiper2", {
    effect: "coverflow",
    watchSlidesProgress: true,
    slidesPerView: 3,
    autoplay: true,
    allowTouchMove: false,
    loop: true,
    centeredSlides: true,
});

featuredSwiper = new Swiper(".featured-container .swiper-container", {
    autoplay: true,
    // speed:1000,
    effect: "coverflow",
    grabCursor: true,
    centeredSlides: true,
    slidesPerView: "auto",
    autoplay: {
        delay: 6000,
        disableOnInteraction: false
    },
    speed: 1000,
    coverflowEffect: {
        rotate: 10,
        stretch: 0,
        depth: 100,
        modifier: 1,
        slideShadows: true,

    },
    loop: true,
    navigation: {
        nextEl: ".swiper-button-next",
        prevEl: ".swiper-button-prev",
    },
    breakpoints: {
        // when window width is >= 499px
        100: {
            slidesPerView: 1,
            // spaceBetweenSlides: 50
        },
        // // when window width is >= 499px
        499: {
            slidesPerView: 3,
            // spaceBetweenSlides: 50
        },
        // // when window width is >= 999px
        999: {
            slidesPerView: 4,
            // spaceBetweenSlides: 50
        }
    }
});


// Popular Swiper/Slider
swiperPopular = new Swiper(".popular-container .swiper-container", {
    slidesPerView: "auto",
    spaceBetween: 5,
    loop: true,
    speed: 1000,
    autoplay: {
        delay: 3000,
        disableOnInteraction: false
    },
    pagination: {
        el: ".swiper-pagination",
        clickable: true,
    },
    breakpoints: {
        "@0.00": {
            slidesPerView: 1,
            spaceBetween: 10,
        },
        "@0.75": {
            slidesPerView: 2,
            spaceBetween: 20,
        },
        "@1.00": {
            slidesPerView: 3,
            spaceBetween: 20,
        },
        "@1.50": {
            slidesPerView: 4,
            spaceBetween: 20,
        },
    },
});

// checkAndPushState(jsRoutes.controllers.IndexController.index().url, '', '');
// Pre-loader
function HideScrollbar() {
    var style = document.createElement("style");
    style.innerHTML = `body::-webkit-scrollbar {display: none;}`;
    document.head.appendChild(style);
}
HideScrollbar()

// function ShowScrollbar() {
//     var style = document.createElement("style");
//     style.innerHTML = `body::-webkit-scrollbar {display: block;}`;
//     document.head.appendChild(style);
// }

intro = document.querySelector('.intro');

window.addEventListener('DOMContentLoaded', ()=>{
    setTimeout(()=>{
        setTimeout(()=>{
            intro.style.top = '-100vh';
            intro.style.visibility = 'hidden';
            // ShowScrollbar();
        },3500);
    });
});