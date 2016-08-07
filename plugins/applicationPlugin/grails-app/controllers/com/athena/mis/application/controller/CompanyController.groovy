package com.athena.mis.application.controller

import com.athena.mis.application.actions.company.*

class CompanyController extends BaseController {

    static allowedMethods = [
            create: "POST",
            show: "POST",
            update: "POST",
            delete: "POST",
            list: "POST",
            showForReseller: "POST",
            listForReseller: "POST",
            updateForReseller: "POST"
    ]

    DeleteCompanyActionService deleteCompanyActionService
    UpdateCompanyActionService updateCompanyActionService
    ListCompanyActionService listCompanyActionService
    CreateCompanyForResellerActionService createCompanyForResellerActionService
    ListCompanyForResellerActionService listCompanyForResellerActionService
    UpdateCompanyForResellerActionService updateCompanyForResellerActionService

    def show() {
        render(view: "/application/company/show")
    }

    def showForReseller() {
        render(view: "/application/company/showForReseller")
    }

    def list() {
        renderOutput(listCompanyActionService, params)
    }

    def listForReseller() {
        renderOutput(listCompanyForResellerActionService, params)
    }

    def create() {
        renderOutput(createCompanyForResellerActionService, params)
    }

    def update() {
        renderOutput(updateCompanyActionService, params)
    }

    def updateForReseller() {
        renderOutput(updateCompanyForResellerActionService, params)
    }

    def delete() {
        renderOutput(deleteCompanyActionService, params)
    }
}
