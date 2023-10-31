ul = document.querySelector("#hashtagList");
input = document.querySelector("#hashtagField");
tagNum = document.querySelector(".details span");

maxTags = 5;
tags = [];

countHashtags();
createHashtag();

function countHashtags() {
    input.focus();
    tagNum.innerText = maxTags - tags.length;
}

function createHashtag() {
    ul.querySelectorAll("li").forEach(li => li.remove());
    tags.slice().reverse().forEach(tag => {
        let liTag = `<li class="d-flex align-items-center"><span>${tag}</span><i class="bi bi-x iconDefault" onclick="remove(this, '${tag}')"></i></li>`;
        ul.insertAdjacentHTML("afterbegin", liTag);
    });
    countHashtags();
}

function remove(element, tag) {
    let index = tags.indexOf(tag);
    tags = [...tags.slice(0, index), ...tags.slice(index + 1)];
    element.parentElement.remove();
    countHashtags();
    updateNFTTagsField();
}

function updateNFTTagsField() {
    $("#NFT_TAGS").val(tags.join(","));
}

function addHashtag(e) {
    if (e.key === ",") {
        let tag = e.target.value.replace(/\s+/g, ' ');
        if (tag.length > 1 && !tags.includes(tag) && tags.length < maxTags) {
            tag.split(',').forEach((tag, index, tagList) => {
                if (index !== tagList.length - 1) {
                    tags.push(tag);
                    createHashtag();
                }
            });
        }
        e.target.value = "";
    }
    updateNFTTagsField();
}

input.addEventListener("keyup", addHashtag);

// Select Collection
function setCollectionId(selectedItem) {
    let selectedElement = $(selectedItem)[0];
    let selectedElementValue = selectedElement.getAttribute("value");
    $("#modalNextButtonContainer .form-primary-button").removeClass("disable");
    $("#modalNextButtonContainer .form-primary-button").attr("onclick", `getForm(jsRoutes.controllers.NFTController.uploadNFTFileForm('${selectedElementValue}'))`);
}

function addProperty(property) {
    tags.push(property);
    countHashtags();
    createHashtag();
    updateNFTTagsField();
}

function setTagBackButton(collectionId, fileName) {
    $("#modalBackButton").attr("onclick", `getForm(jsRoutes.controllers.NFTController.basicDetailsForm('${collectionId}', '${fileName}'))`);
}

function updateNFTContainer(collectionId) {
    if ($("#nftsPerPage").length) {
        $('#nftsPerPage').html("");
        loadFirstNFTBulk('nftsPerPage', jsRoutes.controllers.CollectionController.collectionNFTsPerPage(collectionId, 1));
        componentResource('rightContent', jsRoutes.controllers.CollectionController.topRightCard(collectionId, true));
    }
}

function addDraftTags(tags) {
    let allTags = tags.split(',');
    for (let i = 0; i < allTags.length; i++) {
        addProperty(allTags[i]);
    }
}