package com.athena.mis.application.controller

import com.athena.mis.application.actions.appshellscript.*
import com.athena.mis.application.actions.report.appShellScript.DownloadAppShellScriptReportActionService

class AppShellScriptController extends BaseController {
    static allowedMethods = [show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST", evaluate: "POST"]

    ShowAppShellScriptActionService showAppShellScriptActionService
    ShowAppSqlScriptActionService showAppSqlScriptActionService
    CreateAppShellScriptActionService createAppShellScriptActionService
    ListAppShellScriptActionService listAppShellScriptActionService
    DeleteAppShellScriptActionService deleteAppShellScriptActionService
    UpdateAppShellScriptActionService updateAppShellScriptActionService
    EvaluateAppShellScriptActionService evaluateAppShellScriptActionService
    EvaluateAppSqlScriptActionService evaluateAppSqlScriptActionService
    DownloadAppShellScriptReportActionService downloadAppShellScriptReportActionService

    /**
     * show ShellScript list
     */
    def show() {
        String view = '/application/appShellScript/show'
        renderView(showAppShellScriptActionService, params, view)
    }

    def showSql() {
        String view = '/application/appSqlScript/show'
        renderView(showAppSqlScriptActionService, params, view)
    }

    /**
     * Create a ShellScript object
     */
    def create() {
        renderOutput(createAppShellScriptActionService, params)
    }

    /**
     * Update a ShellScript object
     */
    def update() {
        renderOutput(updateAppShellScriptActionService, params)
    }

    /**
     * Delete a ShellScript object
     */
    def delete() {
        renderOutput(deleteAppShellScriptActionService, params)
    }

    /**
     * List ShellScript object
     */
    def list() {
        renderOutput(listAppShellScriptActionService, params)
    }

    /**
     * Evaluate a ShellScript object from grid
     */
    def evaluate() {
        renderOutput(evaluateAppShellScriptActionService, params)
    }

    /**
     * Evaluate a ShellScript object from grid
     */
    def evaluateSqlScript() {
        renderOutput(evaluateAppSqlScriptActionService, params)
    }

    def downloadReport(){
        Map result = (Map) getServiceResponse(downloadAppShellScriptReportActionService, params).report
        String mime = grailsApplication.config.grails.mime.types[result.format]
        renderOutputStream(result.report.toByteArray(), mime, result.reportFileName)
    }
}
