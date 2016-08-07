package com.athena.mis.application.controller

import com.athena.mis.application.actions.dbinstancequery.*

class DbInstanceQueryController extends BaseController {

    static allowedMethods = [
            show                  : "POST",
            create                : "POST",
            list                  : "POST",
            showQueryResult       : "POST",
            listQueryResult       : "POST",
            downloadQueryResultCsv: "GET",
            downloadQueryResult   : "GET",
            update                : "POST",
            delete                : "POST",
            execute               : "POST",
            executeQuery          : "POST"
    ]

    ShowDbInstanceQueryActionService showDbInstanceQueryActionService
    ListDbInstanceQueryActionService listDbInstanceQueryActionService
    CreateDbInstanceQueryActionService createDbInstanceQueryActionService
    UpdateDbInstanceQueryActionService updateDbInstanceQueryActionService
    DeleteDbInstanceQueryActionService deleteDbInstanceQueryActionService
    DownloadCsvDbInstanceQueryActionService downloadCsvDbInstanceQueryActionService
    DownloadDbInstanceQueryActionService downloadDbInstanceQueryActionService
    ListDbInstanceQueryResultActionService listDbInstanceQueryResultActionService
    ShowDbInstanceQueryResultActionService showDbInstanceQueryResultActionService
    ExecuteDbInstanceQueryActionService executeDbInstanceQueryActionService
    ExecuteDbInstanceQueryForSqlActionService executeDbInstanceQueryForSqlActionService

    def show() {
        String view = '/application/dbInstanceQuery/show'
        renderView(showDbInstanceQueryActionService, params, view)
    }

    def list() {
        renderOutput(listDbInstanceQueryActionService, params)
    }

    def create() {
        renderOutput(createDbInstanceQueryActionService, params)
    }

    def update() {
        renderOutput(updateDbInstanceQueryActionService, params)
    }

    def delete() {
        renderOutput(deleteDbInstanceQueryActionService, params)
    }

    def showQueryResult() {
        String view = '/application/dbInstanceQuery/showDbInstanceQueryResult'
        renderView(showDbInstanceQueryResultActionService, params, view)
    }

    def listQueryResult() {
        renderOutput(listDbInstanceQueryResultActionService, params)
    }

    def downloadQueryResultCsv() {
        Map result = (Map) getServiceResponse(downloadCsvDbInstanceQueryActionService, params)
        String mime = 'text/csv'
        renderOutputStream(result.outputBytes, mime, result.fileName)
    }

    def downloadQueryResult() {
        Map result = (Map) getServiceResponse(downloadDbInstanceQueryActionService, params).report
        String mime = grailsApplication.config.grails.mime.types[result.format]
        renderOutputStream(result.report.toByteArray(), mime, result.reportFileName)
    }

    def execute() {
        renderOutput(executeDbInstanceQueryActionService, params)
    }

    // direct execute from create panel
    def executeQuery() {
        renderOutput(executeDbInstanceQueryForSqlActionService, params)
    }
}
