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
                    countUnread();
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
                if(data > 0){
                    $(".haveNotification").show();
                    $(".noNotification").hide();
                    if(data > 2){
                        $(".loadOlderNotificationButton").show();
                    }
                }else{
                    $(".haveNotification").hide();
                    $(".noNotification").show();
                }
            },
            400: function (data) {
                console.log(data.responseText)
            },
        }
    })
}

function markAllAsRead() {
    let route = jsRoutes.controllers.ProfileController.markAllNotificationRead();
    $.ajax({
        url: route.url,
        type: route.type,
        async: true,
        statusCode: {
            200: function () {
                countUnread();
            },
            400: function (data) {
                console.log(data.responseText)
            },
        }
    })
}

let list = [];
function addToReadList(notificationId){
    $("#markAsReadButton").show();
    $("#notificationIcon_"+notificationId).toggleClass("active");
    if($("#notificationIcon_"+notificationId).hasClass("active")){
        list.push(notificationId);
    }else{
        const index = list.indexOf(notificationId);
        if (index > -1) {
            list.splice(index, 1);
        }
    }
}

function markListItemAsRead(){
    list.forEach((id)=>{
        markAsRead(id,);
    });
    $("#markAsReadButton").hide();
}

function clearMarkAsReadList(){
    list = [];
    $(".notificationIcon").each((index,item)=>{
       $(item).removeClass("active");
    });
    $("#markAsReadButton").hide();
}