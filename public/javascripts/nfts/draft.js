function nftDraft(form, route, snackBarMessage) {
    $.ajax({
        url: route.url,
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data: form.serialize(),
        async: true,
        statusCode: {
            200: function () {
                showSnackbar('', snackBarMessage, 'info');
                setTimeout(()=>{
                    location.reload();
                },3000);
            },
            401: function () {
                console.log("400 response");
            },
            500: function () {
                console.log("500 response");
            }
        }
    });
}

function removeNftDraft(fileName, fileHash, removedMessage) {
    const form = $("#deleteNftDraft_" + fileHash + " form");
    nftDraft(form, jsRoutes.controllers.NFTController.deleteDraft(), removedMessage);
    closeModal();
}