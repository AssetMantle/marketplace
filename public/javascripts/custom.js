$(document).click(function (e) {
    if ($(e.target).is('#commonModal')) {
        $(".modal-overlay").removeClass("active");
        $(".modalContainer").removeClass("active");
        $("body").removeClass("modal-active");
    }
});
function checkNewPassword() {
    let passwordValue = document.getElementById('password').value;

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
    document.getElementsByClassName("collection-form-tag-field")[0].removeChild(element);
}

// Goto top when refresh the page
window.onbeforeunload = function () {
    window.scrollTo(0, 0);
}
document.onload = function () {
    window.scrollTo(0, 0);
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

// Show and hide modal screen
function showHideModalScreen(showScreen="", hideScreen="") {
    $(".modalContainer").removeClass('active');
        $(hideScreen).hide()
        $(showScreen).show();
        $(".modalContainer").addClass('active');
}