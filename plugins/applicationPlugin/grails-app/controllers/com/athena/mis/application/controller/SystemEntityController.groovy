package com.athena.mis.application.controller

import com.athena.mis.application.actions.systementity.*

class SystemEntityController extends BaseController {

    static allowedMethods = [
            create: "POST", show: "POST", update: "POST", list: "POST", delete: "POST"
    ]

    ShowSystemEntityActionService showSystemEntityActionService
    CreateSystemEntityActionService createSystemEntityActionService
    UpdateSystemEntityActionService updateSystemEntityActionService
    ListSystemEntityActionService listSystemEntityActionService
    DeleteSystemEntityActionService deleteSystemEntityActionService

    /**
     * Show system entity
     */
    def show() {
        String view = '/application/systemEntity/show'
        renderView(showSystemEntityActionService, params, view)
    }

    /**
     * Create system entity
     */
    def create() {
        renderOutput(createSystemEntityActionService, params)
    }

    /**
     * Update system entity
     */
    def update() {
        renderOutput(updateSystemEntityActionService, params)
    }

    /**
     * Delete system entity
     */
    def delete() {
        renderOutput(deleteSystemEntityActionService, params)
    }

    /**
     * List & Search system entity
     */
    def list() {
        renderOutput(listSystemEntityActionService, params)
    }
}
