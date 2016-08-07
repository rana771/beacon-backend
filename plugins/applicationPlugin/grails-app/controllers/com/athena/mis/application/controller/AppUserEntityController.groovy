package com.athena.mis.application.controller

import com.athena.mis.application.actions.appuserentity.*

class AppUserEntityController extends BaseController {

    static allowedMethods = [
            show  : "POST",
            create: "POST",
            update: "POST",
            delete: "POST",
            list: "POST",
            dropDownAppUserEntityReload: "POST"
    ];

    ShowAppUserEntityActionService showAppUserEntityActionService
    CreateAppUserEntityActionService createAppUserEntityActionService
    UpdateAppUserEntityActionService updateAppUserEntityActionService
    DeleteAppUserEntityActionService deleteAppUserEntityActionService
    ListAppUserEntityActionService listAppUserEntityActionService
    ListAppUserByEntityTypeAndEntityService listAppUserByEntityTypeAndEntityService

    def show() {
        String view = "/application/appUserEntity/show"
        renderView(showAppUserEntityActionService, params, view)
    }

    def create() {
        renderOutput(createAppUserEntityActionService, params)
    }

    def update() {
        renderOutput(updateAppUserEntityActionService, params)
    }

    def delete() {
        renderOutput(deleteAppUserEntityActionService, params)
    }

    def list() {
        renderOutput(listAppUserEntityActionService, params)
    }

    def listUserByEntityTypeAndEntity() {
        renderOutput(listAppUserByEntityTypeAndEntityService, params)
    }

    /**
     * Reload AppUser list for drop down; used in AppUserEntity mapping CRUD
     */
    def dropDownAppUserEntityReload() {
        render app.dropDownAppUserEntity(params)
    }
}
