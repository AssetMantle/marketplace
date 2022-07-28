// Hide submit button
$("#formSubmitButton").hide();
$("#mnemonicsList").attr("type","hidden");

// 12 Button event
function setOption1(){
    $("#option1").addClass("active");
    $("#option2").removeClass("active");
    $(".seedsSection1").addClass("active");
    $(".seedsSection2").removeClass("active");
    $(".seedsSection2 .mnemonicValue").removeClass("active");
    checkSeedInput();
}
// 24 Button event
function setOption2(){
    $("#option2").addClass("active");
    $("#option1").removeClass("active");
    $(".seedsSection1").removeClass("active");
    $(".seedsSection2").addClass("active");
    $(".seedsSection2 .mnemonicValue").addClass("active");
    checkSeedInput();
}
// 12/24 Button
function addSeedOptionButton() {
    subHeading = $("#modalSubtitle").text();
    $("#modalSubtitle").text("");
    $("#modalSubtitle").append(`
            <div class="d-flex flex-row flex-wrap justify-content-between align-items-center">
                <div class="subHeading">${subHeading}</div>
                <div class="btn-group mt-2 mt-sm-2 mt-md-0 toggleBtn" role="group">
                  <button id="option1" type="button" class="btn p-2" onclick="setOption1()">12</button>
                  <button id="option2" type="button" class="btn p-2 active" onclick="setOption2()">24</button>
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
        $(".seedsSection1").removeClass("active");
        $(".seedsSection2").addClass("active");

        let mnemonicsLayout = "";
        let mnemonicsNumber = 1;

        for(i=1; i<=3; i++){
            mnemonicsLayout += `<div class="d-flex mb-3 form-seed-box-list">`;
            for(j=1; j<=4; j++){
                mnemonicsLayout += `
                <div class="form-seed-box">
                    <div class="form-seed-box-number">${('0' + mnemonicsNumber).slice(-2)}</div>
                    <input type="password" class="mt-2 form-seed-box-phrase mnemonicValue active" id="mnemonic${mnemonicsNumber}" oninput="enableNavigationButton(2)"/>
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
                    <input type="password" class="mt-2 form-seed-box-phrase mnemonicValue active" id="mnemonic${mnemonicsNumber}" oninput="enableNavigationButton(2)"/>
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
        $("#mnemonicsList").attr("value", mnemonicsList);

        // $("#mnemonicsList").val();
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

// Enable button on fill
function enableNavigationButton(pageNumber){
    if (pageNumber === 0) {
        let flag = 0;
        let managedKeyName = document.getElementById("managedKeyName");
        let walletAddress = document.getElementById("managedKeyAddress");
        if ($.trim(managedKeyName.value).length && ($.trim(managedKeyName.value).length < 3 || $.trim(managedKeyName.value).length > 50)) {
            $("#managedKeyName").css("border-color", "var(--error)");
            $("#keyNameError").show(300);
            flag = 1;
        } else {
            $("#managedKeyName").css("border-color", "var(--default)");
            $("#keyNameError").hide(300);
        }
        if ($.trim(walletAddress.value).length && $.trim(walletAddress.value).length !== 45) {
            $("#managedKeyAddress").css("border-color", "var(--error)");
            $("#walletAddressError").show(300);
            flag = 1;
        } else {
            $("#managedKeyAddress").css("border-color", "var(--default)");
            $("#walletAddressError").hide(300);
        }
        if ($.trim(managedKeyName.value).length && $.trim(walletAddress.value).length && flag === 0) {
            $("#newManagedAddressScreenBtn").removeClass("disable");
        } else {
            $("#newManagedAddressScreenBtn").addClass("disable");
        }
    }
    else if(pageNumber === 1){
        $("#disclaimerButton").toggleClass("disable");
    }
    else if(pageNumber === 2){
        checkSeedInput();
    }
    else if(pageNumber === 3){
        let managedMnemonicPassword = document.getElementById("managedMnemonicPassword");

        if ($.trim(managedMnemonicPassword.value).length < 5 || $.trim(managedMnemonicPassword.value).length > 128) {
            $("#managedMnemonicPassword").css("border-color", "var(--error)");
            $("#passwordError").show(300);
        }else {
            $("#managedMnemonicPassword").css("border-color", "var(--default)");
            $("#passwordError").hide(300);
        }
    }
}

function checkSeedInput(){
    let flag = 0;
    allSeeds = $('.mnemonicValue.active');
    allSeeds.each(function () {
        if($.trim($(this).val()).length === 0){
            flag = 1;
        }
    });
    if(flag === 0){
        $("#newManagedAddressMnemonicsBtn").removeClass("disable");
    }
    else{
        $("#newManagedAddressMnemonicsBtn").addClass("disable");
    }
}

// Paste Seeds Phrase
function pasteSeeds(){
    navigator.clipboard
        .readText()
        .then(
            cliptext =>
            {
                var seedsList = cliptext.split(" ");
                var index = 0;
                allSeeds = $('.mnemonicValue.active');
                allSeeds.each(function () {
                    $(this).val(seedsList[index]);
                    index++;
                });
                checkSeedInput();
            },
            err => console.log(err)
        );
}