webSocket = {};
webSocketRetries = 0;

function webSocketInit() {
    if (webSocketRetries <= maxWebSocket) {
        console.log("init websocket");
        try {
            webSocket.close();
        } catch (e) {
        }
        webSocket = new WebSocket(ws.url);
        webSocket.onopen = onOpen;
        webSocket.onclose = onClose;
        webSocket.onmessage = onMessage;
        webSocket.onerror = onError;
    } else {
        console.log('max webSocketRetries reached')
    }
}

function onOpen(event) {
    console.log("Sending START websocket msg: ")
    webSocket.send(ws.start);
}

function onClose(event) {
    console.log("closed websocket");
    webSocketRetries = webSocketRetries + 1;
}

function onError(event) {
    webSocket.close();
    setTimeout(webSocketInit, 2000);
}

function onMessage(event) {
    let receivedData = JSON.parse(event.data);
    switch (receivedData.messageType) {
        case 'NOTIFICATION':
            showSnackbar(receivedData.messageValue.title, receivedData.messageValue.message, receivedData.messageValue.notificationType);
            break;
        default :
            console.log("Unknown Message Type");
            console.log(event.data)
            break;
    }
}

// function onChat(message) {
//     if ($('#chatMessages').length && $('#' + message.chatID).length) {
//         refreshCard('chatMessages', jsRoutes.controllers.ChatController.loadMoreChats(message.chatID, 0), 'CHAT_LOADING');
//     }
// }

// function onAsset(message) {
//     console.log(message);
// }

// function refreshCard(source, route, loadingSpinnerID = 'commonSpinner') {
//     const div = $('#' + source);
//     let loadingSpinner = $('#' + loadingSpinnerID);
//     $.ajax({
//         url: route.url,
//         type: route.type,
//         async: true,
//         global: showSpinner('refreshCard'),
//         beforeSend: function () {
//             loadingSpinner.show();
//         },
//         complete: function () {
//             loadingSpinner.hide();
//         },
//         statusCode: {
//             200: function (data) {
//                 div.html(data)
//             },
//             401: function (data) {
//                 div.html(data)
//             },
//             500: function (data) {
//                 div.html(data)
//             }
//         }
//     });
// }

window.addEventListener("load", webSocketInit, false);