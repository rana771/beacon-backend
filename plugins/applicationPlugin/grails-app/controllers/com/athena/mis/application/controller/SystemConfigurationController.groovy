package com.athena.mis.application.controller

import com.athena.mis.application.actions.sysconfiguration.ListSysConfigurationActionService
import com.athena.mis.application.actions.sysconfiguration.ShowSysConfigurationActionService
import com.athena.mis.application.actions.sysconfiguration.UpdateSysConfigurationActionService

class SystemConfigurationController extends BaseController {

    static allowedMethods = [
            show: "POST", list: "POST", update: "POST"
    ]

    ShowSysConfigurationActionService showSysConfigurationActionService
    UpdateSysConfigurationActionService updateSysConfigurationActionService
    ListSysConfigurationActionService listSysConfigurationActionService

    def show() {
        String view = '/application/sysConfiguration/show'
        renderView(showSysConfigurationActionService, params, view)
    }

    def update() {
        renderOutput(updateSysConfigurationActionService, params)
    }

    def list() {
        renderOutput(listSysConfigurationActionService, params)
    }
}
