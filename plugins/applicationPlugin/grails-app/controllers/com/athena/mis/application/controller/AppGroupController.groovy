package com.athena.mis.application.controller

import com.athena.mis.application.actions.appgroup.*

class AppGroupController extends BaseController {

    static allowedMethods = [
            create: "POST", show: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    CreateAppGroupActionService createAppGroupActionService
    ListAppGroupActionService listAppGroupActionService
    UpdateAppGroupActionService updateAppGroupActionService
    DeleteAppGroupActionService deleteAppGroupActionService

    /**
     * Show AppGroup
     */
    def show() {
        render(view: '/application/appGroup/show', model: [oId: params.oId?params.oId:null])
    }

    /**
     * Create new AppGroup
     */
    def create() {
        renderOutput(createAppGroupActionService, params)
    }

    /**
     * Update AppGroup
     */
    def update() {
        renderOutput(updateAppGroupActionService, params)
    }

    /**
     * Delete AppGroup
     *
     */
    def delete() {
        renderOutput(deleteAppGroupActionService, params)
    }

    /**
     * List & Search AppGroup
     */
    def list() {
        renderOutput(listAppGroupActionService, params)
    }
}
