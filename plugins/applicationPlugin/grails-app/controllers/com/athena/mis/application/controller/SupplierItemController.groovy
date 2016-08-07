package com.athena.mis.application.controller

import com.athena.mis.application.actions.supplieritem.*

class SupplierItemController extends BaseController {

    static allowedMethods = [
            show                      : "POST", create: "POST", update: "POST",
            list                      : "POST", delete: "POST",
            getItemListForSupplierItem: "POST"
    ]

    ShowSupplierItemActionService showSupplierItemActionService
    CreateSupplierItemActionService createSupplierItemActionService
    UpdateSupplierItemActionService updateSupplierItemActionService
    DeleteSupplierItemActionService deleteSupplierItemActionService
    ListSupplierItemActionService listSupplierItemActionService
    GetItemListSupplierItemActionService getItemListSupplierItemActionService


    def show() {
        String view = "/application/supplierItem/show"
        renderView(showSupplierItemActionService, params, view)
    }

    def create() {
        renderOutput(createSupplierItemActionService, params)
    }

    def list() {
        renderOutput(listSupplierItemActionService, params)
    }

    def update() {
        renderOutput(updateSupplierItemActionService, params)
    }

    def delete() {
        renderOutput(deleteSupplierItemActionService, params)
    }

    def getItemListForSupplierItem() {
        renderOutput(getItemListSupplierItemActionService, params)
    }

    def dropDownSupplierItemReload() {
        render app.dropDownSupplierItem(params)
    }
}
