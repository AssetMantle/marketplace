function checkNewPassword() {
    let passwordValue = document.getElementById('password').value;
    // var completeRegularExpression = /^(?=.*\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,128}$/;

    let numberMatchPattern = passwordValue.match(/\d+/g);
    const isUpperCase = (x) => /[A-Z]/.test(x);
    let isSpecialCharacter = (x) => /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/.test(x);

    (passwordValue.length > 7 && passwordValue.length < 129) ? $(".error-info-1 .error-icon").addClass('active') : $(".error-info-1 .error-icon").removeClass('active');
    (numberMatchPattern != null) ? $(".error-info-2 .error-icon").addClass('active') : $(".error-info-2 .error-icon").removeClass('active');
    isUpperCase(passwordValue) ? $(".error-info-3 .error-icon").addClass('active') : $(".error-info-3 .error-icon").removeClass('active');
    isSpecialCharacter(passwordValue) ? $(".error-info-4 .error-icon").addClass('active') : $(".error-info-4 .error-icon").removeClass('active');
    $(".password_field + .error-message").slideDown();
}

function collectionCardSelect(card) {
    $(".collection_card").removeClass('active');
    $(card).addClass('active');
    $(".form-next-button").removeClass('disable-button');
}


// Create Collection : Input type=file
inputs = document.querySelectorAll('.file-input')
for (var i = 0, len = inputs.length; i < len; i++) {
    customInput(inputs[i])
}

function customInput(el) {
    const fileInput = el.querySelector('[type="file"]')
    const label = el.querySelector('[data-js-label]')

    fileInput.onchange =
        fileInput.onmouseout = function () {
            if (!fileInput.value) return

            var value = fileInput.value.replace(/^.*[\\\/]/, '')
            el.className += ' -chosen'
            label.innerText = value
            label.classList.add('active')
        }
}

// Create Collection : Dropdown Menu
$('.dropdown-el').click(function (e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).toggleClass('expanded');
    $('#' + $(e.target).attr('for')).prop('checked', true);
});
$(document).click(function () {
    $('.dropdown-el').removeClass('expanded');
});

// Create Collection : Add More Property
function addCollectionProperty() {

}

// Create Collection : Add More Tags
function addCollectionTag() {

    let element = document.createElement("div");
    element.innerHTML = `<div class="form-field-2-columns">
                            <div class="form-field-2-columns-row-main">
                                <div class="row">
                                    <div class="form-field-column">
                                        <div class="form-field-top">
                                            &nbsp;
                                        </div>
                                        <div class="form-field-bottom">
                                            <input type="text" id="tagName" placeholder="Hashtag"/>
                                        </div>
                                    </div>
                                    <img src="" class="delete-property" onclick="removeCollectionTag(this)"/>
                                </div>
                            </div>
                        </div>`;
    document.getElementsByClassName("collection-form-tag-field")[0].appendChild(element);
}

// Create Collection : Remove Tags
function removeCollectionTag(e) {
    let element = e.parentNode.parentNode.parentNode.parentNode;
    // console.log(element.);
    document.getElementsByClassName("collection-form-tag-field")[0].removeChild(element);
}

// $(function() {
//     $('input[name="daterange"]').daterangepicker({
//         opens: 'left'
//     }, function(start, end, label) {
//         console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
//     });
// });
//
// // Date Range Picker
// function setDateRange(){
//     document.getElementsByClassName("dateRangeField")[0].setAttribute("name","daterange");
//     console.log(document.getElementsByClassName("dateRangeField")[0].getAttribute('name'));
// }


// Hero Section Swiper/Slider 2
var swiper = new Swiper(".mySwiper2", {
    effect: "coverflow",
    watchSlidesProgress: true,
    slidesPerView: 3,
    autoplay: true,
    allowTouchMove: false,
    loop: true,
    centeredSlides: true,
});

// Featured Swiper/Slider
var featuredSwiper = new Swiper(".featured-container .swiper-container", {
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
var swiper = new Swiper(".popular-container .swiper-container", {
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


// New Drops Swiper/Slider
// var swiper = new Swiper(".new-drops-section .swiper-container", {
//     slidesPerView: "auto",
//     grid: {
//         rows: 2,
//     },
//     spaceBetween: 20,
//     pagination: {
//         el: ".swiper-pagination",
//         clickable: true,
//     },
//     autoplay: {
//         delay: 3000,
//         disableOnInteraction: false
//     },
// });


// Title tag marquee
// (function titleMarquee() {
//     document.title = document.title.substring(1)+document.title.substring(0,1);
//     setTimeout(titleMarquee, 200);
// })();

// var currentLocation = 'firstPage';
// // No need to set these inside the event listener since they are always the same.
// var firstHeight = $('#firstPage').offset().top,
//     secondHeight = $('#secondPage').offset().top,
//     thirdHeight = $('#thirdPage').offset().top;
//
// // Helper so we can check if the scroll is triggered by user or by animation.
// var autoScrolling = false;
//
// $(document).scroll(function(e){
//     var scrolled = $(window).scrollTop();
//
//     // Only check if the user scrolled
//     if (!autoScrolling) {
//         if (scrolled > 1 && currentLocation == 'firstPage') {
//             scrollPage(secondHeight, 'secondPage');
//         } else if (scrolled > secondHeight + 1 && currentLocation == 'secondPage') {
//             scrollPage(thirdHeight, 'thirdPage');
//         } else if (scrolled < thirdHeight - 1 && currentLocation == 'thirdPage') {
//             scrollPage(secondHeight, 'secondPage');
//         } else if (scrolled < secondHeight - 1 && currentLocation == 'secondPage') {
//             scrollPage(firstHeight, 'firstPage');
//         }
//     }
//
//     // Since they all have the same animation, you can avoid repetition
//     function scrollPage(nextHeight, page) {
//         currentLocation = page;
//
//         // At this point, the page will start scrolling by the animation
//         // So we switch this var so the listener does not trigger all the if/else
//         autoScrolling = true;
//         $('body,html').animate({scrollTop:nextHeight}, 500, function () {
//             // Once the animation is over, we can reset the helper.
//             // Now it is back to detecting user scroll.
//             autoScrolling = false;
//         });
//     }
//
// })
// document.addEventListener("mousewheel", this.mousewheel.bind(this), { passive: false });


// const scrollElement =
//     window.document.scrollingElement ||
//     window.document.body ||
//     window.document;


// Sections are zero indexed to match array from getElementsByClassName
// var scroll = {
//     activeSection: 0,
//     totalSections: document.getElementsByClassName('section').length,
//     throttled: false,
//     throttleDur: 500,
// }
//
// var downSection = () => {
//     if (scroll.activeSection < 7) {
//         ++scroll.activeSection;
//         console.log(scroll.activeSection);
//         scrollToSection(scroll.activeSection)
//     }
// }
//
// var upSection = () => {
//     if (scroll.activeSection > 0) {
//         --scroll.activeSection;
//         console.log(scroll.activeSection);
//         scrollToSection(scroll.activeSection)
//
//     }
// }
//
// var scrollToSection = (section) => {
//     anime({
//         targets: scrollElement,
//         scrollTop: (section) * window.innerHeight,
//         duration: scroll.throttleDur,
//         easing: 'linear'
//     })
//
//     scroll.activeSection = section
// }
//
// window.addEventListener('scroll', function(e) {
//     e.preventDefault()
// }, false)
//
// window.addEventListener('wheel', function(e) {
//     e.preventDefault()
//
//     if (!scroll.throttled) {
//         scroll.throttled = true
//
//         setTimeout(function() {
//             scroll.throttled = false
//         }, 1.5 * scroll.throttleDur)
//
//         if(e.deltaY < 0) {
//             upSection()
//         } else {
//             downSection()
//         }
//     }
// }, false)


// Don't use following code
// var initialY = null
//
// window.addEventListener('touchstart', function(e) {
//     initialY = e.touches[0].clientY
// }, false)

// window.addEventListener('touchmove', function(e) {
//     e.preventDefault()
//
//     if (initialY === null) {
//         return
//     }
//
//     var currentY = e.touches[0].clientY;
//
//     var diffY = initialY - currentY;
//
//     if(!scroll.throttled) {
//         scroll.throttled = true
//
//         setTimeout(function() {
//             scroll.throttled = false
//         }, 1.5 * scroll.throttleDur)
//
//         if (diffY > 0) {
//             downSection()
//         } else {
//             upSection()
//         }
//     }
//
//     initialy = null
//
// }, {passive: false})


// Scroll back to correct section when resized.
// window.addEventListener('resize', function(e) {
//     scrollToSection(scroll.activeSection)
// }, false)

// Goto top when refresh the page
window.onbeforeunload = function () {
    window.scrollTo(0, 0);
}
document.onload = function () {
    window.scrollTo(0, 0);
}

// Copy to Clipboard
function copyToClipboard(e) {
    var element = $(e).next('.form-copy-message');
    var copyText = $(e).prevAll('.username-data');

    element.addClass("active");
    element.one('webkitAnimationEnd oanimationend msAnimationEnd animationend', function () {
        element.removeClass('active');
    });

    navigator.clipboard.writeText(copyText.text());
}

// Multi Step
dots = document.getElementsByClassName('progress-bar__dot')
numberOfSteps = 3
currentStep = 1

for (let i = 0; i < dots.length; ++i) {
    dots[i].addEventListener('click', () => {
        goToStep(i + 1)
    })
}

function goToStep(stepNumber) {
    currentStep = stepNumber

    let indicators = document.getElementsByClassName('progress-bar__dot')

    for (let i = indicators.length - 1; i >= currentStep; --i) {
        indicators[i].classList.remove('full')
    }

    for (let i = 0; i < currentStep; ++i) {
        indicators[i].classList.add('full')
    }
}

// Reveal Navbar on Scroll
// const body = document.body;
// let lastScroll = 0;
//
// window.addEventListener("scroll", () => {
//     const currentScroll = window.pageYOffset;
//     if (currentScroll <= 0) {
//         body.classList.remove("scroll-up");
//         return;
//     }
//
//     if (currentScroll > lastScroll && !body.classList.contains("scroll-down")) {
//         body.classList.remove("scroll-up");
//         body.classList.add("scroll-down");
//     } else if (
//         currentScroll < lastScroll &&
//         body.classList.contains("scroll-down")
//     ) {
//         body.classList.remove("scroll-down");
//         body.classList.add("scroll-up");
//     }
//     lastScroll = currentScroll;
// });


// Show and hide modal screen
function showHideModalScreen(showScreen, hideScreen) {
    $(".modalContainer").removeClass('active');
    // setTimeout(function () {
        $(hideScreen).hide()
        $(showScreen).show();
        $(".modalContainer").addClass('active');
    // }, 1000);
}