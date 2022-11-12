createdWhitelistCurrentPageNumber = 0;
joinedWhitelistCurrentPageNumber = 0;

// Date Range Picker
$("#picker").daterangepicker({
    locale: {
        format: 'DD/MM/YYYY'
    }
});

function resetCreatedWhitelistPage() {
    setCreatedWhitelistCurrentPage(0);
    componentResource("whitelistSectionContent", jsRoutes.controllers.WhitelistController.createdWhitelists());
}

function resetJoinedWhitelistPage() {
    setJoinedWhitelistCurrentPage(0);
    componentResource("whitelistSectionContent", jsRoutes.controllers.WhitelistController.joinedWhitelists());
}

function showWhitelistScreen(screenID) {
    switch (screenID) {
        case "created":
            $(".contentContainer .contentTitle .title .titleLabel").text("Created");
            $(".contentContainer .contentTitle .titleMenu").show();
            resetCreatedWhitelistPage();
            break;
        case "joined":
            $(".contentContainer .contentTitle .title .titleLabel").text("Joined");
            $(".contentContainer .contentTitle .titleMenu").hide();
            resetJoinedWhitelistPage();
            break;
    }
}

function setCreatedWhitelistCurrentPage(page) {
    createdWhitelistCurrentPageNumber = page;
}

function createdWhitelistPaginationOnNext(totalWhitelists, whitelistPerPage) {
    componentResource('createdWhitelistTableBody', jsRoutes.controllers.WhitelistController.createdWhitelistsPerPage(createdWhitelistCurrentPageNumber + 1));
    let lastPage = Math.ceil(totalWhitelists / whitelistPerPage);
    if ((createdWhitelistCurrentPageNumber + 1) === lastPage) {
        $('#createdWhitelistPaginationNext').hide();
    }
    if (createdWhitelistCurrentPageNumber === 0) {
        $('#createdWhitelistPaginationBack').hide();
    } else {
        $('#createdWhitelistPaginationBack').show();
    }
    setCreatedWhitelistCurrentPage(createdWhitelistCurrentPageNumber + 1);
}

function createdWhitelistPaginationOnBack() {
    if (createdWhitelistCurrentPageNumber > 1) {
        componentResource('createdWhitelistTableBody', jsRoutes.controllers.WhitelistController.createdWhitelistsPerPage(createdWhitelistCurrentPageNumber - 1));
        if ((createdWhitelistCurrentPageNumber - 1) === 1) {
            $('#createdWhitelistPaginationBack').hide();
        }
    }
    $('#createdWhitelistPaginationNext').show();
    setCreatedWhitelistCurrentPage(createdWhitelistCurrentPageNumber - 1);
}

function setJoinedWhitelistCurrentPage(page) {
    joinedWhitelistCurrentPageNumber = page;
}

function joinedWhitelistPaginationOnNext(totalWhitelists, whitelistPerPage) {
    componentResource('joinedWhitelistTableBody', jsRoutes.controllers.WhitelistController.joinedWhitelistsPerPage(joinedWhitelistCurrentPageNumber + 1));
    let lastPage = Math.ceil(totalWhitelists / whitelistPerPage);
    if ((joinedWhitelistCurrentPageNumber + 1) === lastPage) {
        $('#joinedWhitelistPaginationNext').hide();
    }
    if (joinedWhitelistCurrentPageNumber === 0) {
        $('#joinedWhitelistPaginationBack').hide();
    } else {
        $('#joinedWhitelistPaginationBack').show();
    }
    setJoinedWhitelistCurrentPage(joinedWhitelistCurrentPageNumber + 1);
}

function joinedWhitelistPaginationOnBack() {
    if (joinedWhitelistCurrentPageNumber > 1) {
        componentResource('joinedWhitelistTableBody', jsRoutes.controllers.WhitelistController.joinedWhitelistsPerPage(joinedWhitelistCurrentPageNumber - 1));
        if ((joinedWhitelistCurrentPageNumber - 1) === 1) {
            $('#joinedWhitelistPaginationBack').hide();
        }
    }
    $('#joinedWhitelistPaginationNext').show();
    setJoinedWhitelistCurrentPage(joinedWhitelistCurrentPageNumber - 1);
}

// Tooltip
$(function () {
    $('[data-toggle="tooltip"]').tooltip();
})

function whitelistMember(form, route, snackBarMessage, fieldIndex) {
    $.ajax({
        url: route.url,
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data: form.serialize(),
        async: true,
        statusCode: {
            200: function () {
                showSnackbar('', snackBarMessage, 'info');
                $("#whitelistUser_"+fieldIndex).remove();
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

function removeWhitelistMember(username, removedMessage, index){
    const form = $("#removeWhitelistMember_" + username + " form");
    whitelistMember(form, jsRoutes.controllers.WhitelistController.deleteMember(), removedMessage, index);
}

function searchMember() {
    let searchField = document.getElementById('searchMemberField');
    let filter = searchField.value.toUpperCase();
    let members = document.getElementById("memberList");
    let memberList = members.getElementsByTagName('li');

    for (let i = 0; i < memberList.length; i++) {
        let memberName = memberList[i].querySelector(".username").textContent;
        if (memberName.toUpperCase().indexOf(filter) > -1) {
            memberList[i].style.display = "";
        } else {
            memberList[i].style.display = "none";
        }
    }
}