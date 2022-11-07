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