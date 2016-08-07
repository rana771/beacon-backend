package com.athena.mis.application.controller

import com.athena.mis.application.actions.appfaq.CreateAppFaqActionService
import com.athena.mis.application.actions.appfaq.DeleteAppFaqActionService
import com.athena.mis.application.actions.appfaq.ListAppFaqActionService
import com.athena.mis.application.actions.appfaq.ListAppFaqForListViewActionService
import com.athena.mis.application.actions.appfaq.ShowAppFaqActionService
import com.athena.mis.application.actions.appfaq.UpdateAppFaqActionService

class AppFaqController extends BaseController {

    static allowedMethods = [
            list: "POST", show: "POST", create: "POST", update: "POST", delete: "POST"
    ]

    ShowAppFaqActionService showAppFaqActionService
    ListAppFaqActionService listAppFaqActionService
    CreateAppFaqActionService createAppFaqActionService
    UpdateAppFaqActionService updateAppFaqActionService
    DeleteAppFaqActionService deleteAppFaqActionService
    ListAppFaqForListViewActionService listAppFaqForListViewActionService


    def show() {
        String view = "/application/appFaq/show"
        renderView(showAppFaqActionService, params, view)
    }

    def create() {
        renderOutput(createAppFaqActionService, params)
    }

    def update() {
        renderOutput(updateAppFaqActionService, params)
    }

    def list() {
        renderOutput(listAppFaqActionService, params)
    }

    def delete() {
        renderOutput(deleteAppFaqActionService, params)
    }

    def listFaq() {
        renderOutput(listAppFaqForListViewActionService, params)
    }
    def reloadFaq() {
        render app.appFaq(params, null)
    }


}
