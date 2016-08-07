package com.athena.mis.application.controller

import com.athena.mis.application.actions.appcustomer.*

class AppCustomerController extends BaseController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"]

    CreateCustomerActionService createCustomerActionService
    DeleteCustomerActionService deleteCustomerActionService
    ListCustomerActionService listCustomerActionService
    UpdateCustomerActionService updateCustomerActionService

    def show() {
        render(view: "/application/appCustomer/show")
    }

    def create() {
        renderOutput(createCustomerActionService, params)
    }

    def list() {
        renderOutput(listCustomerActionService, params)
    }

    def delete() {
        renderOutput(deleteCustomerActionService, params)
    }

    def update() {
        renderOutput(updateCustomerActionService, params)
    }
}
