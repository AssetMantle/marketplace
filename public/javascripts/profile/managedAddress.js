// Hide submit button
$("#formSubmitButton").hide();

// 12 Button event
function setOption1(){
    $("#option1").addClass("active");
    $("#option2").removeClass("active");
    $(".seedsSection1").addClass("active");
    $(".seedsSection2").removeClass("active");
    $(".seedsSection2 .mnemonicValue").removeClass("active");
}
// 24 Button event
function setOption2(){
    $("#option2").addClass("active");
    $("#option1").removeClass("active");
    $(".seedsSection1").removeClass("active");
    $(".seedsSection2").addClass("active");
    $(".seedsSection2 .mnemonicValue").addClass("active");
}
// 12/24 Button
function addSeedOptionButton() {
    subHeading = $("#modalSubtitle").text();
    $("#modalSubtitle").text("");
    $("#modalSubtitle").append(`
            <div class="d-flex flex-row flex-wrap justify-content-between align-items-center">
                <div class="subHeading">${subHeading}</div>
                <div class="btn-group mt-2 mt-sm-2 mt-md-0 toggleBtn" role="group">
                  <button id="option1" type="button" class="btn p-2 active" onclick="setOption1()">12</button>
                  <button id="option2" type="button" class="btn p-2" onclick="setOption2()">24</button>
                </div>
            </div>
        `);
}

// Show/Hide Mnemonics & Password Screen
function showHideManagedModalScreen(showScreen, hideScreen, screenNo) {
    $(hideScreen).hide();
    if(screenNo === 1){
        $("#staticBackdropLabel").text("Disclaimer");
        $("#modalSubtitle").hide();
    } else if(screenNo === 2){
        $("#staticBackdropLabel").text("Seed Pharse");
        $("#modalSubtitle").text("Enter your seed phrase to add a managed wallet");

        $("#option1").addClass("active");
        $("#option2").removeClass("active");
        $(".seedsSection1").addClass("active");
        $(".seedsSection2").removeClass("active");

        let mnemonicsLayout = "";
        let mnemonicsNumber = 1;

        for(i=1; i<=3; i++){
            mnemonicsLayout += `<div class="d-flex mb-3 form-seed-box-list">`;
            for(j=1; j<=4; j++){
                mnemonicsLayout += `
                <div class="form-seed-box">
                    <div class="form-seed-box-number">${('0' + mnemonicsNumber).slice(-2)}</div>
                    <input type="password" class="mt-2 form-seed-box-phrase mnemonicValue active" id="mnemonic${mnemonicsNumber}"/>
                </div>`;
                mnemonicsNumber+=1;
            }
            mnemonicsLayout += `</div>`;
            $(".seedsSection1").append(mnemonicsLayout);
            mnemonicsLayout = "";
        }

        for(i=1; i<=3; i++){
            mnemonicsLayout += `<div class="d-flex mb-3 form-seed-box-list">`;
            for(j=1; j<=4; j++){
                mnemonicsLayout += `
                <div class="form-seed-box">
                    <div class="form-seed-box-number">${('0' + mnemonicsNumber).slice(-2)}</div>
                    <input type="password" class="mt-2 form-seed-box-phrase mnemonicValue" id="mnemonic${mnemonicsNumber}"/>
                </div>`;
                mnemonicsNumber+=1;
            }
            mnemonicsLayout += `</div>`;
            $(".seedsSection2").append(mnemonicsLayout);
            mnemonicsLayout = "";
        }
        $("#modalSubtitle").show();
        addSeedOptionButton();
    } else if(screenNo === 3){
        let mnemonicsList = '';
        $('.mnemonicValue.active').each(function(){
             if($(this).val()) {
                 mnemonicsList += $(this).val() + " ";
            }
        });
        $("#mnemonicsList").val(mnemonicsList);

        $("#mnemonicsList").val();
        $("#staticBackdropLabel").text("Confirm Password");
        $(".subHeading").text("Enter your password to confirm action");
        $(".toggleBtn").hide();
        $("#formSubmitButton").show();
    }
    $(showScreen).show();
}

// Show/Hide Seed Phrase
function showHideSeed(){
    allSeeds = $('.mnemonicValue');
    if (allSeeds[0].type === "password"){
        allSeeds.each(function(){
            $(this).attr("type","text");
        });
        $(".closeEye").addClass("hidden");
        $(".openEye").removeClass("hidden");
    } else {
        allSeeds.each(function(){
            $(this).attr("type","password");
        });
        $(".closeEye").removeClass("hidden");
        $(".openEye").addClass("hidden");
    }
}