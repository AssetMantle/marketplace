function processTxResponse(response) {
    console.log(response);
    const success = (response.code === 0);
    $('#txFields').hide();
    $('#keplrFormSubmitButton').hide();
    if (success) {
        $('#txSuccessful').show();
    } else {
        $('#txFailed').show();
    }
    $('#txResponse').show();
    $('#txResponseHash').html(response.transactionHash);
}

function processKeplrError(e) {
    console.log(e);
    $('#txFields').hide();
    $('#keplrFormSubmitButton').hide();
    $('#keplrErrorMessage').html(e);
    $('#keplrError').show();
}

function SendMsg(fromAddress, toAddress, amount, denom) {
    return {
        typeUrl: "/cosmos.bank.v1beta1.MsgSend",
        value: window.msgSend.fromPartial({
            fromAddress: fromAddress,
            toAddress: toAddress,
            amount: [{
                denom: denom,
                amount: String(amount),
            }],
        }),
    };
}

async function fundWallet() {
    try {
        await initializeKeplr();
        const wallet = await getKeplrWallet();
        getForm(jsRoutes.controllers.BlockchainTransactionController.fundWalletForm(wallet[1]));
    } catch (e) {
        console.log(e)
    }
}

async function onFundWalletSubmit(fromAddress, toAddress, denom) {
    try {
        const amount = $('#SEND_COIN_AMOUNT').val();
        const msg = SendMsg(fromAddress, toAddress, amount * chainConfig.MicroFactor, denom);
        const wallet = await getKeplrWallet();
        const txFee = getTxFee();
        const response = await broadcastTransaction(wallet[0], wallet[1], [msg], txFee);
        processTxResponse(response);
    } catch (e) {
        processKeplrError(e);
    }
}