sellOrdersCurrentPageNumber = 0;
yourOrdersCurrentPageNumber = 0;

function resetSellOrderPage(nftId) {
    sellOrdersCurrentPageNumber = 0;
    componentResource("tradeSectionContent", jsRoutes.controllers.NFTController.sellOrders(nftId));
}

function sellOrdersPaginationOnNext(nftId, totalSellOrders, ordersPerPage) {
    componentResource("tradeSellOrdersTableBody", jsRoutes.controllers.NFTController.sellOrdersPerPage(nftId, sellOrdersCurrentPageNumber + 1));
    let lastPage = Math.ceil(totalSellOrders / ordersPerPage);
    if ((sellOrdersCurrentPageNumber + 1) === lastPage) {
        $('#sellOrdersPaginationNext').hide();
    }
    if (sellOrdersCurrentPageNumber === 0) {
        $('#sellOrdersPaginationBack').hide();
    } else {
        $('#sellOrdersPaginationBack').show();
    }
    sellOrdersCurrentPageNumber = sellOrdersCurrentPageNumber + 1;
}

function sellOrdersPaginationOnBack(nftId) {
    if (sellOrdersCurrentPageNumber > 1) {
        componentResource("tradeSellOrdersTableBody", jsRoutes.controllers.NFTController.sellOrdersPerPage(nftId, sellOrdersCurrentPageNumber - 1));
        if ((sellOrdersCurrentPageNumber - 1) === 1) {
            $('#sellOrdersPaginationBack').hide();
        }
    }
    $('#sellOrdersPaginationNext').show();
    sellOrdersCurrentPageNumber = sellOrdersCurrentPageNumber - 1;
}

function resetYourOrderPage(nftId) {
    yourOrdersCurrentPageNumber = 0;
    componentResource("tradeSectionContent", jsRoutes.controllers.NFTController.yourOrders(nftId));
}

function yourOrdersPaginationOnNext(nftId, totalYourOrders, ordersPerPage) {
    componentResource("tradeYourOrdersTableBody", jsRoutes.controllers.NFTController.yourOrdersPerPage(nftId, yourOrdersCurrentPageNumber + 1));
    let lastPage = Math.ceil(totalYourOrders / ordersPerPage);
    if ((yourOrdersCurrentPageNumber + 1) === lastPage) {
        $('#yourOrdersPaginationNext').hide();
    }
    if (yourOrdersCurrentPageNumber === 0) {
        $('#yourOrdersPaginationBack').hide();
    } else {
        $('#yourOrdersPaginationBack').show();
    }
    yourOrdersCurrentPageNumber = yourOrdersCurrentPageNumber + 1;
}

function yourOrdersPaginationOnBack(nftId) {
    if (yourOrdersCurrentPageNumber > 1) {
        componentResource("tradeYourOrdersTableBody", jsRoutes.controllers.NFTController.yourOrdersPerPage(nftId, yourOrdersCurrentPageNumber - 1));
        if ((yourOrdersCurrentPageNumber - 1) === 1) {
            $('#yourOrdersPaginationBack').hide();
        }
    }
    $('#yourOrdersPaginationNext').show();
    yourOrdersCurrentPageNumber = yourOrdersCurrentPageNumber - 1;
}

function showSellOrders(nftId, dropBoxText) {
    $(".contentContainer .contentTitle .title .titleLabel").text(dropBoxText);
    resetSellOrderPage(nftId);
}

function showYourOrders(nftId, dropBoxText) {
    $(".contentContainer .contentTitle .title .titleLabel").text(dropBoxText);
    resetYourOrderPage(nftId);
}

