$gif = $("#gifImage");
$timer = undefined;
myModal = new bootstrap.Modal($("#nftRevealModal"), {});
isModalOpen = false;

function loadGif() {
    var style = document.createElement("style");
    style.innerHTML = `body::-webkit-scrollbar {display: none;}`;
    document.head.appendChild(style);

    $gif.attr("src", "");
    clearTimeout($timer);

    $("#mainContainer").hide();

    setTimeout(()=>{
        src = jsRoutes.controllers.Assets.versioned("images/GIF/nftReveal.gif");
        $gif.attr("src", src.url);
        $gif.show();
    },100);

    $("#nftRevealModal").addClass('active');
    myModal.show();

    $(".modal-open").attr("style","overflow: hidden !important; height: 100vh !important;");

    $(".backRipple").addClass("anim");
    isModalOpen = true;
}
loadGif();

function playGif(){
    if(isModalOpen) {
        clearTimeout($timer);
        $timer = setTimeout(() => {
            $gif.hide();
            $gif.attr("src", "");
        }, 8500);
        showNftCard();
    }
}

function showNftCard(){
    if(isModalOpen) {
        $("#mainContainer").show();
        setTimeout(() => {
            ScrollTrigger.batch(".cardd", {
                scroller: "#cards_container",
                onEnter: (batch) =>
                    gsap.to(batch, {autoAlpha: 1, stagger: 0.1}),
            });
            $("#cards_container").addClass("anim");
        }, 8000);
        cardTilt();
    }
}

function cardTilt(){
    if(isModalOpen) {
        var x;
        var $cards = $(".cardRotator");

        $cards.on("mousemove touchmove", function (e) {
            var pos = [e.offsetX, e.offsetY];
            e.preventDefault();
            if (e.type === "touchmove") {
                pos = [e.touches[0].clientX, e.touches[0].clientY];
            }
            var $card = $(this);
            var l = pos[0];
            var t = pos[1];
            var h = $card.height();
            var w = $card.width();
            var px = Math.abs(Math.floor((100 / w) * l) - 100);
            var py = Math.abs(Math.floor((100 / h) * t) - 100);
            var lp = 50 + (px - 50) / 1.5;
            var tp = 50 + (py - 50) / 1.5;
            var ty = ((tp - 50) / 2) * -1;
            var tx = ((lp - 50) / 1.5) * 0.5;
            var tf = `transform: rotateX(${ty}deg) rotateY(${tx}deg)`;
            $cards.removeClass("active");
            $card.attr("style", tf);
            if (e.type === "touchmove") {
                return false;
            }
            clearTimeout(x);
        })
            .on("mouseout touchend touchcancel", function () {
                var $card = $(this);
                $card.removeAttr("style");
            });
    }
}

var $cards = $(".cartTranslator");
var cardInFocus = null;

function reset(cardToReset = null){
    if (cardToReset) {
        $(cardToReset).animate({
            top: 0,
            left: 0,
        });
        $(cardToReset).find(".cardRotator").removeClass("fliped");
        setTimeout(()=>{
            $(cardToReset)
                .parent()
                .attr("style", "z-index:1; opacity:1 !important;");
            cardInFocus = null;
        },500);

    }else if(cardInFocus){
        var lastCard = cardInFocus;
        $(lastCard).animate({
            top: 0,
            left: 0,
        });
        $(lastCard).find(".cardRotator").removeClass("fliped");

        setTimeout(()=>{
            $(lastCard)
                .parent()
                .attr("style", "z-index:1; opacity:1 !important;");
        },500);
    }
}

$cards.on("click", function (e) {
    if ($(this).find(".cardRotator").hasClass("fliped")) {
        reset(cardInFocus);
    } else {
        reset();
        cardInFocus = this;
        var $card = $(this);

        $(this).animate(
            {
                left:
                    $(".middlePoint").offset().left -
                    $(this).offset().left -
                    $card.width() / 2,
                top:
                    $(".middlePoint").offset().top -
                    $(this).offset().top -
                    $card.height() / 2,
            },
            1000
        );
        $(this).parent().attr("style", "z-index:9999; opacity:1 !important;");
        $(this).find(".cardRotator").addClass("fliped");
    }
});

function closeNftRevealModal(){
    if(isModalOpen) {
        var style = document.createElement("style");
        style.innerHTML = `body::-webkit-scrollbar {display: unset;}`;
        document.head.appendChild(style);

        $("#nftRevealModal").removeClass('active');
        myModal.hide();

        $(".modal-open").attr("style", "overflow: unset !important; height: unset !important;");
        isModalOpen = false;
    }
}