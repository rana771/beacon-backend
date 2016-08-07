package com.athena.mis.application.controller

import com.athena.mis.application.actions.transactionlog.*

class TransactionLogController extends BaseController {

    static allowedMethods = [
            show                      : "POST",
            showForDplDataExport: "POST",
            showForDplDataImport      : "POST",
            clear                     : "POST",
            list                      : "POST"
    ]

    ShowTransactionLogActionService showTransactionLogActionService
    ShowTransactionLogForDplDataExportActionService showTransactionLogForDplDataExportActionService
    ShowTransactionLogForDplDataImportActionService showTransactionLogForDplDataImportActionService
    ListTransactionLogActionService listTransactionLogActionService
    ClearTransactionLogActionService clearTransactionLogActionService


    def show() {
        String view = '/application/transactionLog/show'
        renderView(showTransactionLogActionService, params, view)
    }

    def showForDplDataExport() {
        String view = '/dataPipeLine/transactionLog/showForDataExport'
        renderView(showTransactionLogForDplDataExportActionService, params, view)
    }

    def showForDplDataImport() {
        String view = '/dataPipeLine/transactionLog/showForDataImport'
        renderView(showTransactionLogForDplDataImportActionService, params, view)
    }

    def list() {
        renderOutput(listTransactionLogActionService, params)
    }

    def clear() {
        renderOutput(clearTransactionLogActionService, params)
    }

}
