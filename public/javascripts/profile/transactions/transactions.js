userTransactionCurrentPageNumber = 1;

function hideBackButtonOnFirstLoad(){
    if (userTransactionCurrentPageNumber === 1) {
        $('#userTransactionPaginationBack').hide();
    }
}

function setUserTransactionCurrentPage(page) {
    userTransactionCurrentPageNumber = page;
}

function userTransactionPaginationOnNext() {
    componentResource('userTransactionsPerPage', jsRoutes.controllers.ProfileController.transactionsPerPage(userTransactionCurrentPageNumber + 1));
    if (userTransactionCurrentPageNumber === 1) {
        $('#userTransactionPaginationBack').hide();
    } else {
        $('#userTransactionPaginationBack').show();
    }
    setUserTransactionCurrentPage(userTransactionCurrentPageNumber + 1);
}

function userTransactionPaginationOnBack() {
    if (userTransactionCurrentPageNumber > 1) {
        componentResource('userTransactionsPerPage', jsRoutes.controllers.ProfileController.transactionsPerPage(userTransactionCurrentPageNumber - 1));
        if ((userTransactionCurrentPageNumber - 1) === 1) {
            $('#userTransactionPaginationBack').hide();
        }
    }
    $('#userTransactionPaginationNext').show();
    setUserTransactionCurrentPage(userTransactionCurrentPageNumber - 1);
}