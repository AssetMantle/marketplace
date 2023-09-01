firebaseConfig = {
    apiKey: "AIzaSyC2WdGgro2R9RK6SS_BxaK7wtfJ8zqpuOE",
    authDomain: "dev-marketplace-c6667.firebaseapp.com",
    databaseURL: "https://dev-marketplace-c6667-default-rtdb.asia-southeast1.firebasedatabase.app",
    projectId: "dev-marketplace-c6667",
    storageBucket: "dev-marketplace-c6667.appspot.com",
    messagingSenderId: "635356272218",
    appId: "1:635356272218:web:cee7016208ed605a4e2a63",
};

ws = {
    url: 'wss://' + $(location).attr('host') + '/websocket',
    start: 'START',
};
if ($(location).attr('host') === "localhost:9000") {
    ws.url = 'ws://' + $(location).attr('host') + '/websocket'
}
maxWebSocket = 5;
addState = true;
microFactor = 1000000;