function loadMoreNotifications() {
    let page = $('.notificationsPerPage').length + 1;
    let route = jsRoutes.controllers.ProfileController.loadMoreNotifications(page);
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                $('#notificationList').append(data);
            },
            400: function (data) {
                console.log(data.responseText)
            },
        }
    })
}

function convertMillisEpochToLocal(id) {
    let element = $('#' + id);
    let dateTime = new Date((element.text() * 1));
    element.text(dateTime.toLocaleDateString() + " " + dateTime.toLocaleTimeString());
}

function onNotificationClick(notificationId, route = "") {
    if (route !== "") {
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            statusCode: {
                200: function (data) {
                    replaceDocument(data);
                },
                500: function (data) {
                    replaceDocument(data.responseText);
                },
            }
        }).fail(function (XMLHttpRequest) {
            if (XMLHttpRequest.readyState === 0) {
                $('#connectionError').fadeIn(100);
            }
        });
    }
    markAsRead(notificationId);
}

function countUnread() {
    let route = jsRoutes.controllers.ProfileController.countUnreadNotification();
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                if(data > 0) {
                    $("#totalUnreadNotification").text(data);
                    $("#totalUnreadNotification").show();
                }else{
                    $("#totalUnreadNotification").hide();
                }
            },
            400: function (data) {
                console.log(data.responseText)
            },
        }
    })
}

function markAsRead(notificationId) {
    const form = $("#markAsRead_" + notificationId + " form");
    const route = jsRoutes.controllers.ProfileController.markNotificationsRead();
    let element = $("#" + notificationId);
    if (element.attr("class").includes("unread")) {
        $("#notificationIcon_" + notificationId).toggleClass("active");
        $.ajax({
            url: route.url,
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            data: form.serialize(),
            async: true,
            statusCode: {
                200: function () {
                    element.removeClass("unread").addClass("read");
                    countUnread();
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
}

function markAllAsRead(){
    const form = $("#markAllAsRead form");
    const route = jsRoutes.controllers.ProfileController.markNotificationsRead();
    $.ajax({
        url: route.url,
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data: form.serialize(),
        async: true,
        statusCode: {
            200: function () {
                $(".notificationDetail.unread").each((index,item)=>{
                    $(item).removeClass("unread").addClass("read");
                });
                countUnread();
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