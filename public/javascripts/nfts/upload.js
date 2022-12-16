let hashData = "";
let collectionId = "";

function onSuccessfulUpload(fileHash) {
    $('#nftFileHashName').text(fileHash);
    const index = fileHash.lastIndexOf('/');
    hashData = fileHash.slice(index + 1);
    setFileName();
}

function passCollectionId(id) {
    collectionId = id;
}

function setFileName() {
    if (hashData !== "" && collectionId !== "") {
        const nftId = hashData.split(".")[0];
        $("#uploadNftNextButton").removeClass("disable");
        $("#uploadNftNextButton").attr("onclick", `getForm(jsRoutes.controllers.NFTController.basicDetailsForm("${collectionId}","${nftId}"))`);
    }
}