package com.athena.mis.application.controller

import com.athena.mis.application.actions.appcountry.CreateAppCountryActionService
import com.athena.mis.application.actions.appcountry.DeleteAppCountryActionService
import com.athena.mis.application.actions.appcountry.ListAppCountryActionService
import com.athena.mis.application.actions.appcountry.UpdateAppCountryActionService

class AppCountryController extends BaseController {

    static allowedMethods = [show: 'POST', create: 'POST', delete: 'POST', update: 'POST', list: 'POST']

    CreateAppCountryActionService createAppCountryActionService
    UpdateAppCountryActionService updateAppCountryActionService
    DeleteAppCountryActionService deleteAppCountryActionService
    ListAppCountryActionService listAppCountryActionService
    /**
     * show country list
     */
    def show() {
        render(view: '/application/appCountry/show')

    }
    /**
     * create country
     */
    def create() {
        renderOutput(createAppCountryActionService, params)
    }
    /**
     * update country
     */
    def update() {
        renderOutput(updateAppCountryActionService, params)
    }
    /**
     * delete country
     */
    def delete() {
        renderOutput(deleteAppCountryActionService, params)
    }
    /**
     * list and search country
     */
    def list() {
        renderOutput(listAppCountryActionService, params)
    }
}
