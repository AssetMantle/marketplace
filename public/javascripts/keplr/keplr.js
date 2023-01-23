chainConfig = {
    rpc: "http://localhost:26657",
    lcd: "http://localhost:1317",
    chainID: "test",
    chainName: "AssetMantle",
    keplrSet: false,
    stakingDenom: "umntl",
    maxGas: 100000,
    MicroFactor: 1000000,
}

function setKeplrConfigParameters(chaiName, rpc, lcd, chainID, stakingDenom) {
    chainConfig.chainName = chaiName
    chainConfig.rpc = rpc;
    chainConfig.lcd = lcd;
    chainConfig.chainID = chainID;
    chainConfig.stakingDenom = stakingDenom;
}

async function initializeKeplr() {
    if (!window.keplr) {
        alert("Please install keplr extension");
    } else {
        if (!chainConfig.keplrSet) {
            await setChain();
            await window.keplr.enable(chainConfig.chainID);
            chainConfig.keplrSet = true;
        }
    }
}

async function getKeplrWallet() {
    if (!window.keplr) {
        alert("Please install keplr extension");
    } else {
        try {
            let offlineSigner = window.keplr.getOfflineSigner(chainConfig.chainID);
            let accounts = await offlineSigner.getAccounts();
            return [offlineSigner, accounts[0].address];
        } catch (e) {
            console.log(e)
        }
    }
}

async function broadcastTransaction(wallet, signerAddress, msgs, fee, memo = "") {
    const cosmJS = await window.SigningStargateClient.connectWithSigner(
        chainConfig.rpc,
        wallet,
    );
    return await cosmJS.signAndBroadcast(signerAddress, msgs, fee, memo);
}

async function signArbitrary(signer, data) {
    try {
        return await window.keplr.signArbitrary(chainConfig.chainID, signer, data);
    } catch (e) {
        console.log(e)
    }
}

function getTxFee(amount = 0, gas = chainConfig.maxGas) {
    return {amount: [{amount: String(amount), denom: chainConfig.stakingDenom}], gas: String(gas)};
}

async function setChain() {
    await window.keplr.experimentalSuggestChain({
        chainId: chainConfig.chainID,
        chainName: chainConfig.chainName,
        rpc: chainConfig.rpc,
        rest: chainConfig.lcd,
        bip44: {
            coinType: 118,
        },
        bech32Config: {
            bech32PrefixAccAddr: "mantle",
            bech32PrefixAccPub: "mantle" + "pub",
            bech32PrefixValAddr: "mantle" + "valoper",
            bech32PrefixValPub: "mantle" + "valoperpub",
            bech32PrefixConsAddr: "mantle" + "valcons",
            bech32PrefixConsPub: "mantle" + "valconspub",
        },
        currencies: [
            {
                coinDenom: "MNTL",
                coinMinimalDenom: "umntl",
                coinDecimals: 6,
                coinGeckoId: "mantle",
            },
        ],
        feeCurrencies: [
            {
                coinDenom: "MNTL",
                coinMinimalDenom: "umntl",
                coinDecimals: 6,
                coinGeckoId: "mantle",
            },
        ],
        stakeCurrency: {
            coinDenom: "MNTL",
            coinMinimalDenom: "umntl",
            coinDecimals: 6,
            coinGeckoId: "mantle",
        },
        coinType: 118,
        gasPriceStep: {
            low: 0.01,
            average: 0.025,
            high: 0.03,
        },
    });

}