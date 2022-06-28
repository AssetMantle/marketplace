$(".option-layout .option").click(function(){
    $(".option-layout .option").removeClass("active");
    $(this).addClass("active");
});

// Grid - Slab View Transition
const gridViewButton = document.querySelector('.grid-view-button');
const listViewButton = document.querySelector('.list-view-button');
const list = document.querySelector('.profile-list ol');

gridViewButton.onclick = function () {
    // list.classList.add('grid-view-filter');
    // list.classList.remove('list-view-filter');
    $(".grid-view-filter").show();
    $(".list-view-filter").hide();
}

listViewButton.onclick = function () {
    // list.classList.remove('grid-view-filter');
    // list.classList.add('list-view-filter');
    $(".grid-view-filter").hide();
    $(".list-view-filter").show();
}

