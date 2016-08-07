package com.athena.mis.application.controller

import com.athena.mis.application.actions.report.userrole.DownloadUserRoleReportActionService
import com.athena.mis.application.actions.role.*

class RoleController extends BaseController {

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST",
            showMyRole: "POST", listMyRole: "POST", downloadUserRoleReport: "GET"
    ]

    ListRoleActionService listRoleActionService
    CreateRoleActionService createRoleActionService
    UpdateRoleActionService updateRoleActionService
    DeleteRoleActionService deleteRoleActionService
    ListMyRoleActionService listMyRoleActionService
    DownloadUserRoleReportActionService downloadUserRoleReportActionService

    def show() {
        render(view: '/application/role/show', model: [oId: params.oId?params.oId:null])
    }

    def list() {
        renderOutput(listRoleActionService, params)
    }

    def create() {
        renderOutput(createRoleActionService, params)
    }

    def update() {
        renderOutput(updateRoleActionService, params)
    }

    def delete() {
        renderOutput(deleteRoleActionService, params)
    }

    /**
     * show UI to view list of logged in user role
     */
    def showMyRole() {
        render(view: '/application/role/showMyRole')
    }

    /**
     * show list of role of logged in user
     */
    def listMyRole() {
        renderOutput(listMyRoleActionService, params)
    }

    /**
     * Download user role report
     */
    def downloadUserRoleReport() {
        Map result = (Map) getServiceResponse(downloadUserRoleReportActionService, params).report
        String mime = grailsApplication.config.grails.mime.types[result.format]
        renderOutputStream(result.report.toByteArray(), mime, result.reportFileName)
    }
}
