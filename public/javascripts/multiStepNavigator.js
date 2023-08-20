// $('.steperContainer ul li').on('click', function () {
//     $('.steperContainer ul li').removeClass();
//     $(this).addClass("active");
//     $(this).prevAll().addClass("visited");
// });

function checkDefaultSelected(){
    let activeOption = $(".steperContainer ul .active");
    $(activeOption).prevAll().addClass("visited");
}
checkDefaultSelected();