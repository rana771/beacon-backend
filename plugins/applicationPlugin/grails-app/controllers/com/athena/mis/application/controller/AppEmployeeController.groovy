package com.athena.mis.application.controller

import com.athena.mis.application.actions.appemployee.CreateAppEmployeeActionService
import com.athena.mis.application.actions.appemployee.DeleteAppEmployeeActionService
import com.athena.mis.application.actions.appemployee.ListAppEmployeeActionService
import com.athena.mis.application.actions.appemployee.UpdateAppEmployeeActionService

class AppEmployeeController extends BaseController {

    static allowedMethods = [
            create: "POST", show: "POST",  update: "POST", delete: "POST", list: "POST"
    ]

    CreateAppEmployeeActionService createAppEmployeeActionService
    ListAppEmployeeActionService listAppEmployeeActionService
    UpdateAppEmployeeActionService updateAppEmployeeActionService
    DeleteAppEmployeeActionService deleteAppEmployeeActionService

    /**
     * show employee list
     */
    def show() {
        render(view: '/application/appEmployee/show')
    }

    /**
     * create employee
     */
    def create() {
        renderOutput(createAppEmployeeActionService, params)
    }

    /**
     * update employee
     */
    def update() {
        renderOutput(updateAppEmployeeActionService, params)
    }

    /**
     * delete employee
     */
    def delete() {
        renderOutput(deleteAppEmployeeActionService, params)
    }

    /**
     * list and search employee
     */
    def list() {
        renderOutput(listAppEmployeeActionService, params)
    }
}
