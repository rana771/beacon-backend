package com.athena.mis.application.controller

import com.athena.mis.application.actions.userrole.*

class UserRoleController extends BaseController {

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST",
            dropDownAppUserForRoleReload: "POST", dropDownRoleForCompanyUserReload:"POST",
            showForCompanyUser: "POST", listForCompanyUser: "POST", createForCompanyUser: "POST",
            updateForCompanyUser: "POST"
    ]

    ShowUserRoleActionService showUserRoleActionService
    ListUserRoleActionService listUserRoleActionService
    CreateUserRoleActionService createUserRoleActionService
    UpdateUserRoleActionService updateUserRoleActionService
    DeleteUserRoleActionService deleteUserRoleActionService
    ShowUserRoleForCompanyUserActionService showUserRoleForCompanyUserActionService
    ListUserRoleForCompanyUserActionService listUserRoleForCompanyUserActionService
    CreateUserRoleForCompanyUserActionService createUserRoleForCompanyUserActionService
    UpdateUserRoleForCompanyUserActionService updateUserRoleForCompanyUserActionService

    def show() {
        String view = "/application/userRole/show"
        renderView(showUserRoleActionService, params, view)
    }

    def create() {
        renderOutput(createUserRoleActionService, params)
    }

    def update() {
        renderOutput(updateUserRoleActionService, params)
    }

    def delete() {
        renderOutput(deleteUserRoleActionService, params)
    }

    def list() {
        renderOutput(listUserRoleActionService, params)
    }

    /**
     * Drop down AppUser for Role reload
     */
    def dropDownAppUserForRoleReload() {
        render app.dropDownAppUserRole(params)
    }

    def showForCompanyUser() {
        String view = "/application/userRole/showForCompanyUser"
        renderView(showUserRoleForCompanyUserActionService, params, view)
    }

    def listForCompanyUser() {
        renderOutput(listUserRoleForCompanyUserActionService, params)
    }

    def createForCompanyUser() {
        renderOutput(createUserRoleForCompanyUserActionService, params)
    }

    def updateForCompanyUser() {
        renderOutput(updateUserRoleForCompanyUserActionService, params)
    }

    /**
     * Reload Role list for drop down; used in User-Role mapping CRUD for company user
     */
    def dropDownRoleForCompanyUserReload() {
        render app.dropDownRoleForCompanyUser(params)
    }
}
