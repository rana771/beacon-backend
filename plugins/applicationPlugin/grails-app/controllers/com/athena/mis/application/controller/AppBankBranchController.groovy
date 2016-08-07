package com.athena.mis.application.controller

import com.athena.mis.application.actions.appbankbranch.*

class AppBankBranchController extends BaseController {

    static allowedMethods = [create: "POST", update: "POST", list: "POST", delete: "POST",
            listDistributionPoint: "POST", reloadBranchesDropDownByBankAndDistrict: "POST"
    ];

    ShowAppBankBranchActionService showAppBankBranchActionService
    CreateAppBankBranchActionService createAppBankBranchActionService
    UpdateAppBankBranchActionService updateAppBankBranchActionService
    DeleteAppBankBranchActionService deleteAppBankBranchActionService
    ListAppBankBranchActionService listAppBankBranchActionService
    ListExhDistributionPointActionService listExhDistributionPointActionService

    def show() {
        String view = "/application/appBankBranch/show"
        renderView(showAppBankBranchActionService, params, view)
    }

    def list() {
       renderOutput(listAppBankBranchActionService,params)
    }

    def create() {
        renderOutput(createAppBankBranchActionService,params)
    }

    /**
     * update bankBranch
     */
    def update() {
        renderOutput(updateAppBankBranchActionService,params)
    }

    /**
     * delete bankBranch
     * @return
     */
    def delete() {
        renderOutput(deleteAppBankBranchActionService, params)
    }

    /**
     * list & search bankBranch as distribution point
     */
    def listDistributionPoint() {
        renderOutput(listExhDistributionPointActionService, params)
    }

    def reloadBranchesDropDownByBankAndDistrict() {
        render app.dropDownBranchesByBankAndDistrict(params)
    }
}
