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

function onNotificationClick(route) {
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
}

function markAsRead(id) {
    let route = jsRoutes.controllers.ProfileController.markNotificationRead(id);
    let element = $('#' + id);
    if (element.attr("class").includes("unread")) {
        $.ajax({
            url: route.url,
            type: route.type,
            async: true,
            statusCode: {
                200: function (data) {
                    element.removeClass("unread").addClass("read");
                    console.log(data);
                },
                400: function (data) {
                    console.log(data.responseText)
                },
            }
        });
    }
}

function countUnread() {
    let route = jsRoutes.controllers.ProfileController.countUnreadNotification();
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function (data) {
                console.log(data);
            },
            400: function (data) {
                console.log(data.responseText)
            },
        }
    })
}