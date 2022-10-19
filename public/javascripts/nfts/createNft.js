const ul = document.querySelector("#hashtagList");
const input = document.querySelector("#hashtagField");
const tagNum = document.querySelector(".details span");

let maxTags = 5;
let tags = [];

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
}

input.addEventListener("keyup", addHashtag);