package com.athena.mis.application.controller

import com.athena.mis.application.actions.requestmap.*
import grails.converters.JSON

class RequestMapController extends BaseController {

    static allowedMethods = [show             : "POST", update: "POST",
                             resetRequestMap  : "POST", reloadRequestMap: "POST",
                             listAvailableRole: "POST", listAssignedRole: "POST"]

    UpdateRequestMapActionService updateRequestMapActionService
    ResetRequestMapByPluginIdActionService resetRequestMapByPluginIdActionService
    ReloadRequestMapActionService reloadRequestMapActionService
    ListAvailableRoleActionService listAvailableRoleActionService
    ListAssignedRoleActionService listAssignedRoleActionService

    /**
     * Show Request Map
     */
    def show() {
        render(view: '/application/requestMap/show')
    }

    /**
     * Update Request Map
     */
    def update() {
        Map result = this.getServiceResponse(updateRequestMapActionService, params)
        String leftMenuData = g.render(plugin: 'applicationplugin', template: '/application/leftmenu')
        result.leftMenuData = leftMenuData
        String output = result as JSON
        render output
    }

    /**
     * Reset Request Map
     */
    def resetRequestMap() {
        renderOutput(resetRequestMapByPluginIdActionService, params)
    }

    /**
     * Reload Request Map
     */
    def reloadRequestMap() {
        Map result = this.getServiceResponse(reloadRequestMapActionService, params)
        String leftMenuData = g.render(plugin: 'applicationplugin', template: '/application/leftmenu')
        result.leftMenuData = leftMenuData
        String output = result as JSON
        render output
    }

    def listAvailableRole() {
        renderOutput(listAvailableRoleActionService, params)
    }

    def listAssignedRole() {
        renderOutput(listAssignedRoleActionService, params)
    }
}
