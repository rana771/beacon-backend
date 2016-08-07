package com.athena.mis.application.controller

import com.athena.mis.application.actions.appserverdbinstancemapping.CreateAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.DeleteAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.ListAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.ShowAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.UpdateAppServerDbInstanceMappingActionService

class AppServerDbInstanceMappingController extends BaseController {

    static allowedMethods = [show: "POST", list: "POST", create: "POST", update: "POST", delete: "POST"]

    ShowAppServerDbInstanceMappingActionService showAppServerDbInstanceMappingActionService
    ListAppServerDbInstanceMappingActionService listAppServerDbInstanceMappingActionService
    CreateAppServerDbInstanceMappingActionService createAppServerDbInstanceMappingActionService
    UpdateAppServerDbInstanceMappingActionService updateAppServerDbInstanceMappingActionService
    DeleteAppServerDbInstanceMappingActionService deleteAppServerDbInstanceMappingActionService

    def show() {
        String view = "/application/appServerDbInstanceMapping/show"
        renderView(showAppServerDbInstanceMappingActionService, params, view)
    }

    def list() {
        renderOutput(listAppServerDbInstanceMappingActionService, params)
    }

    def create() {
        renderOutput(createAppServerDbInstanceMappingActionService, params)
    }

    def update() {
        renderOutput(updateAppServerDbInstanceMappingActionService, params)
    }

    def delete() {
        renderOutput(deleteAppServerDbInstanceMappingActionService, params)
    }

}
