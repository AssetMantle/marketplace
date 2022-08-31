var getMNTLPrice = async () => {
    const request = await fetch("https://api.coingecko.com/api/v3/simple/price?ids=assetmantle&vs_currencies=usd");
    const res = await request.json();
    return (res["assetmantle"]["usd"]);
};