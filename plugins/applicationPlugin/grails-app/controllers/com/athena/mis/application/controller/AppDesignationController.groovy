package com.athena.mis.application.controller

import com.athena.mis.application.actions.appdesignation.*

class AppDesignationController extends BaseController {

    static allowedMethods = [
            create: "POST", show: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    CreateAppDesignationActionService createAppDesignationActionService
    ListAppDesignationActionService listAppDesignationActionService
    UpdateAppDesignationActionService updateAppDesignationActionService
    DeleteAppDesignationActionService deleteAppDesignationActionService

    /**
     * Show Designation
     */
    def show() {
        render(view: '/application/appDesignation/show')
    }

    /**
     * Create Designation
     */
    def create() {
        renderOutput(createAppDesignationActionService, params)
    }

    /**
     * Update Designation
     */
    def update() {
        renderOutput(updateAppDesignationActionService, params)
    }

    /**
     * Delete Designation
     */
    def delete() {
        renderOutput(deleteAppDesignationActionService, params)
    }

    /**
     * List and Search Designation
     */
    def list() {
        renderOutput(listAppDesignationActionService, params)
    }
}
