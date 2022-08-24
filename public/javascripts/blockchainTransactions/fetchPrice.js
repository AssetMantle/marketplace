function getMNTLPrice() {
    $.ajax({
        url: "https://api.coingecko.com/api/v3/simple/price?ids=assetmantle&vs_currencies=usd",
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            console.log(res);
            alert(res);
        }
    });
}