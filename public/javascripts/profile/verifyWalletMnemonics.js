// Hide submit button
$("#formSubmitButton").hide();

// 12/24 Button
subHeading = $("#modalSubtitle").text();
$("#modalSubtitle").text("");
$("#modalSubtitle").append(`
            <div class="d-flex flex-row flex-wrap justify-content-between align-items-center">
                <div class="subHeading">${subHeading}</div>
                <div class="btn-group mt-2 mt-sm-2 mt-md-0 toggleBtn" role="group">
                  <button id="option1" type="button" class="btn p-2 active">12</button>
                  <button id="option2" type="button" class="btn p-2">24</button>
                </div>
            </div>
        `);
// 12 Button event
$("#option1").click(function(){
    $(this).addClass("active");
    $("#option2").removeClass("active");
    $(".seedsSection1").addClass("active");
    $(".seedsSection2").removeClass("active");
});
// 24 Button event
$("#option2").click(function(){
    $(this).addClass("active");
    $("#option1").removeClass("active");
    $(".seedsSection1").removeClass("active");
    $(".seedsSection2").addClass("active");
});
// Show/Hide Mnemonics & Password Screen
function showHideMnemonicsModalScreen(showScreen, hideScreen) {
    $(hideScreen).hide();
    $("#staticBackdropLabel").text("Confirm Password");
    $(".subHeading").text("Enter your password to confirm action");
    $(".toggleBtn").hide();
    $("#formSubmitButton").show();
    $(showScreen).show();
}