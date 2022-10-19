function onSuccessfulUpload(fileHash) {
    $('#nftFileHashName').text(fileHash);
    setFileName(fileHash);
}