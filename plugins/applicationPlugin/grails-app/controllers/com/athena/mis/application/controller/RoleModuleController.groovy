package com.athena.mis.application.controller

import com.athena.mis.application.actions.rolemodule.CreateRoleModuleActionService
import com.athena.mis.application.actions.rolemodule.DeleteRoleModuleActionService
import com.athena.mis.application.actions.rolemodule.ListRoleModuleActionService
import com.athena.mis.application.actions.rolemodule.ShowRoleModuleActionService
import com.athena.mis.application.actions.rolemodule.UpdateRoleModuleActionService

class RoleModuleController extends BaseController {

    static allowedMethods = [
            create: "POST", show: "POST",  update: "POST", delete: "POST", list: "POST"
    ]

    CreateRoleModuleActionService createRoleModuleActionService
    DeleteRoleModuleActionService deleteRoleModuleActionService
    ListRoleModuleActionService listRoleModuleActionService
    ShowRoleModuleActionService showRoleModuleActionService
    UpdateRoleModuleActionService updateRoleModuleActionService

    /**
     * show employee list
     */
    def show() {
        String view = "/application/roleModule/show"
        renderView(showRoleModuleActionService, params, view)
    }

    /**
     * create employee
     */
    def create() {
        renderOutput(createRoleModuleActionService, params)
    }

    /**
     * update employee
     */
    def update() {
        renderOutput(updateRoleModuleActionService, params)
    }

    /**
     * delete employee
     */
    def delete() {
        renderOutput(deleteRoleModuleActionService, params)
    }

    /**
     * list and search employee
     */
    def list() {
        renderOutput(listRoleModuleActionService, params)
    }

    /**
     * Drop down AppUser for Role reload
     */
    def dropDownRoleModuleReload() {
        render app.dropDownModules(params)
    }
}
