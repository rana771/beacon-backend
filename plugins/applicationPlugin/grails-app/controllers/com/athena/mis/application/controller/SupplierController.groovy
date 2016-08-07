package com.athena.mis.application.controller

import com.athena.mis.application.actions.supplier.*
import grails.converters.JSON

class SupplierController extends BaseController {

    static allowedMethods = [
            show           : "POST",
            create         : "POST",
            update         : "POST",
            delete         : "POST",
            list           : "POST",
            listAllSupplier: "POST"
    ];

    CreateSupplierActionService createSupplierActionService
    UpdateSupplierActionService updateSupplierActionService
    DeleteSupplierActionService deleteSupplierActionService
    ListSupplierActionService listSupplierActionService
    GetSupplierListActionService getSupplierListActionService

    def show() {
        render(view: "/application/supplier/show", model: [oId: params.oId?params.oId:null])
    }

    def list() {
        renderOutput(listSupplierActionService, params)
    }

    def create() {
        renderOutput(createSupplierActionService, params)
    }

    def update() {
        renderOutput(updateSupplierActionService, params)
    }

    def delete() {
        renderOutput(deleteSupplierActionService, params)
    }

    /* get wrappedSupplierList to show on grid
       Used to populate right grid on Procurement->SupplierWise PO report
     */

    def listAllSupplier() {
        renderOutput(getSupplierListActionService , params)
    }
}
