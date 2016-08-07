package com.athena.mis.application.controller

import com.athena.mis.application.actions.appbank.CreateAppBankActionService
import com.athena.mis.application.actions.appbank.DeleteAppBankActionService
import com.athena.mis.application.actions.appbank.ListAppBankActionService
import com.athena.mis.application.actions.appbank.UpdateAppBankActionService

class AppBankController extends BaseController {

    static allowedMethods = [
            show:"POST",create: "POST", update: "POST", list: "POST", delete: "POST", reloadBankDropDownTagLib: "POST"
    ];

    CreateAppBankActionService createAppBankActionService
    UpdateAppBankActionService updateAppBankActionService
    DeleteAppBankActionService deleteAppBankActionService
    ListAppBankActionService listAppBankActionService

    /**
     * show bank list
     */
    def show() {
        render(view: "/application/appBank/show", model: [oId: params.oId ? params.oId : null])
    }

    /**
     * list and search bank
     */
    def list() {

        renderOutput(listAppBankActionService, params)
    }

    /**
     * create Bank
     */
    def create() {
        renderOutput(createAppBankActionService, params)
    }

    /**
     * update bank
     */
    def update() {
        renderOutput(updateAppBankActionService, params)
    }

    /**
     * delete bank
     */
    def delete() {
        renderOutput(deleteAppBankActionService, params)
    }

    def reloadBankDropDownTagLib() {
        render app.dropDownBank(params, null)
    }
}
