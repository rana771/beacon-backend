package com.athena.mis.application.controller

import com.athena.mis.application.actions.appversion.ListAppVersionActionService

class AppVersionController extends BaseController{

    static allowedMethods = [
            show: "POST"
    ]

    ListAppVersionActionService listAppVersionActionService

    def show() {
        render(view: '/application/appVersion/show')
    }

    def list() {
        renderOutput(listAppVersionActionService, params)
    }
}
