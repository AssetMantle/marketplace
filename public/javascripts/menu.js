(function(){
    var dropDownMenus = document.getElementsByClassName("dropDownContainer");
    for (let i = 0; i < dropDownMenus.length; i++) {
        let dropDownItems = dropDownMenus[i].getElementsByClassName("dropDownItem");
        for(let j = 0; j < dropDownItems.length; j++){
            let accordionMenu = dropDownItems[j].getElementsByClassName("accordionMenu")[0];
            if(accordionMenu.nextElementSibling != null){
                accordionMenu.dropDownItem = dropDownItems[j];
                accordionMenu.addEventListener("click", function() {
                    this.dropDownItem.classList.toggle("active");
                    var panel = this.nextElementSibling;
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null;
                    } else {
                        panel.style.maxHeight = panel.scrollHeight + "px";
                    }
                });
            }
        }
    }
})();

document.querySelector(".content .links li.exploreLink").addEventListener("mouseleave",function (){
    let menuContainer = document.querySelector(".content .links li.exploreLink .dropDownContainer .dropDownItem");
    let panel = document.querySelector(".content .links li.exploreLink .dropDownContainer .dropDownItem .panel");
    menuContainer.classList.remove("active");
    if (panel.style.maxHeight) {
        panel.style.maxHeight = null;
    }
});

