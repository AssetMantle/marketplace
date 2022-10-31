function getFileTypes(documentType) {
    let fileTypes = [];
    switch (documentType) {
        case "PROFILE":
        case "COVER":
            fileTypes = ['jpg', 'png', 'jpeg', 'JPG', 'JPEG', 'PNG'];
            break;
        default:
            fileTypes = ['jpg', 'png', 'jpeg', 'JPG', 'JPEG', 'PNG', 'pdf'];
            break;
    }
    return fileTypes
}

function uploadFile(storeFileRoute, uploadRoute, id, documentType, filesSupported, maxFileSize, onSuccessCallback) {
    const rFile = new Resumable({
        target: storeFileRoute(id, documentType).url,
        fileType: filesSupported.split("_"),
        query: {csrfToken: $('[name="csrfToken"]').attr('value')},
        maxFileSize: maxFileSize
    });

    rFile.assignBrowse(document.getElementById('browseUploadButton_' + documentType));

    rFile.assignDrop(document.getElementById('uploadSelector_' + documentType));

    rFile.on('fileAdded', function (file) {
        $("#uploader_" + documentType).show();
        $("#uploadSelector_" + documentType).hide();
        $("#fileName_" + documentType).html(file.fileName);
        $("#uploader_" + documentType).show();
    });

    rFile.on('fileProgress', function (file) {
        moveProgressBar($("#fileUploadProgressBar_" + documentType), (file.progress(false) * 100.00));
    });

    let uploadCompletionMessage = document.getElementById('uploadCompletionMessage_' + documentType);

    rFile.on('fileSuccess', function (file) {
        $("#uploadControls_" + documentType).delay(1000).fadeOut(1000);
        let storeDbRoute = uploadRoute(id, documentType, file.fileName);
        // let loadingSpinner = $('#commonSpinner');
        $.ajax({
            url: storeDbRoute.url,
            type: storeDbRoute.type,
            // global: showSpinner('fileUpload'),
            // beforeSend: function () {
            //     loadingSpinner.show();
            // },
            // complete: function () {
            //     loadingSpinner.hide();
            // },
            statusCode: {
                200: function (data) {
                    $("#uploadCompletionMessage_" + documentType).show();
                    uploadCompletionMessage.textContent = data;
                    console.log(data);
                    onSuccessCallback(data);
                },
                206: function (data) {
                    $("#commonModalContent").html(data);
                },
                400: function (error) {
                    $("#uploadCompletionMessage_" + documentType).show();
                    uploadCompletionMessage.textContent = error.responseText;
                },
                500: function (data) {
                    const newDocument = document.open("text/html", "replace");
                    newDocument.write(data.responseText);
                    newDocument.close();
                }
            }
        });
    });

    rFile.on('fileError', function (file, message) {
        console.log(message)
        console.log(file.fileName)
    });

    $("#uploadButton_" + documentType).click(function () {
        rFile.upload();
    });

}

function updateFile(storeFileRoute, updateRoute, id, documentType, filesSupported, maxFileSize) {
    const rFile = new Resumable({
        target: storeFileRoute(id, documentType).url,
        fileType: filesSupported.split("_"),
        query: {csrfToken: $('[name="csrfToken"]').attr('value')},
        maxFileSize: maxFileSize
    });

    rFile.assignBrowse(document.getElementById('browseUpdateButton_' + documentType));

    rFile.assignDrop(document.getElementById('updateSelector_' + documentType));

    rFile.on('fileAdded', function (file) {
        $("#updater").show();
        $("#updateSelector").hide();
        $("#fileName").html(file.fileName);
    });

    rFile.on('fileProgress', function (file) {
        moveProgressBar($("#fileUpdateProgressBar"), (file.progress(false) * 100.00));
    });

    let updateCompletionMessage = document.getElementById('updateCompletionMessage_' + documentType);
    rFile.on('fileSuccess', function (file) {
        $("#updateControls").delay(1000).fadeOut(1000);
        let updateDbRoute = updateRoute(file.fileName, id, documentType);
        let loadingSpinner = $('#commonSpinner');
        $.ajax({
            url: updateDbRoute.url,
            type: updateDbRoute.type,
            // global: showSpinner('fileUpload'),
            // beforeSend: function () {
            //     loadingSpinner.show();
            // },
            // complete: function () {
            //     loadingSpinner.hide();
            // },
            statusCode: {
                200: function (data) {
                    $("#updateCompletionMessage").show();
                    updateCompletionMessage.textContent = data;
                },
                206: function (data) {
                    $("#commonModalContent").html(data);
                },
                400: function (error) {
                    $("#updateCompletionMessage").show();
                    updateCompletionMessage.textContent = error.responseText;
                },
                500: function (data) {
                    const newDocument = document.open("text/html", "replace");
                    newDocument.write(data.responseText);
                    newDocument.close();
                }
            }
        });
    });

    $("#updateButton").click(function () {
        rFile.upload();
    });

}

function moveProgressBar(target, progress) {
    const id = setInterval(frame, 10);
    let width = $(target).width();

    function frame() {
        if (width >= progress) {
            clearInterval(id);
        } else {
            width++;
            $(target).width(width + '%');
        }
    }
}