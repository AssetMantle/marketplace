function checkNoCollection() {
    if ($(".singleCreatedCollection").length === 0) {
        $('#noCreatedCollectionsPerPage').removeClass("hidden");
    }
}

function collectionDraft(form, route, snackBarMessage) {
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

function removeCollectionDraft(collectionId, removedMessage) {
    const form = $("#deleteCollectionDraft_" + collectionId + " form");
    collectionDraft(form, jsRoutes.controllers.CollectionController.deleteDraft(), removedMessage);
    closeModal();
}