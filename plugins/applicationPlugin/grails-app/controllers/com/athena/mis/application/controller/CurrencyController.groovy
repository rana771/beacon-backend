package com.athena.mis.application.controller

import com.athena.mis.application.actions.currency.CreateCurrencyActionService
import com.athena.mis.application.actions.currency.DeleteCurrencyActionService
import com.athena.mis.application.actions.currency.ListCurrencyActionService
import com.athena.mis.application.actions.currency.UpdateCurrencyActionService

class CurrencyController extends BaseController {

    static allowedMethods = [create: "POST", update: "POST", edit: "POST", list: "POST", delete: "POST"];

    ListCurrencyActionService listCurrencyActionService
    CreateCurrencyActionService createCurrencyActionService
    DeleteCurrencyActionService deleteCurrencyActionService
    UpdateCurrencyActionService updateCurrencyActionService

    /**
     * show currency
     */
    def show() {
        render(view: '/application/currency/show')
    }

    /**
     * create currency
     */
    def create() {
        renderOutput(createCurrencyActionService, params)
    }

    /**
     * update currency
     */
    def update() {
        renderOutput(updateCurrencyActionService, params)
    }

    /**
     * delete currency
     */
    def delete() {
        renderOutput(deleteCurrencyActionService, params)
    }

    /**
     * list & search currency
     */
    def list() {
        renderOutput(listCurrencyActionService, params)
    }

}

