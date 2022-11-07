var ul = document.querySelector("#hashtagList");
var input = document.querySelector("#hashtagField");
var tagNum = document.querySelector(".details span");

var maxTags = 5;
var tags = [];

countHashtags();
createHashtag();

function countHashtags(){
    input.focus();
    tagNum.innerText = maxTags - tags.length;
}

function createHashtag(){
    ul.querySelectorAll("li").forEach(li => li.remove());
    tags.slice().reverse().forEach(tag =>{
        let liTag = `<li class="d-flex align-items-center"><span>${tag}</span><i class="bi bi-x iconDefault" onclick="remove(this, '${tag}')"></i></li>`;
        ul.insertAdjacentHTML("afterbegin", liTag);
    });
    countHashtags();
}

function remove(element, tag){
    let index  = tags.indexOf(tag);
    tags = [...tags.slice(0, index), ...tags.slice(index + 1)];
    element.parentElement.remove();
    countHashtags();
    $("#NFT_TAGS").val(tags.join(","));
}

function addHashtag(e){
    if(e.key === ","){
        let tag = e.target.value.replace(/\s+/g, ' ');
        if(tag.length > 1 && !tags.includes(tag)){
            if(tags.length < maxTags){
                tag.split(',').forEach((tag, index, tagList) => {
                    if (index !== tagList.length - 1) {
                        tags.push(tag);
                        createHashtag();
                    }
                });
            }
        }
        e.target.value = "";
    }
    $("#NFT_TAGS").val(tags.join(","));
}

input.addEventListener("keyup", addHashtag);

// Select Collection
function setCollectionId(selectedItem){
    let selectedElement = $(selectedItem)[0];
    let selectedElementValue = selectedElement.getAttribute("value");
    $("#modalNextButtonContainer .form-primary-button").removeClass("disable");
    $("#modalNextButtonContainer .form-primary-button").attr("onclick",`getForm(jsRoutes.controllers.NFTController.uploadNFTFileForm('${selectedElementValue}'))`);
}

function addProperty(property){
    tags.push(property);
    countHashtags();
    createHashtag();
}