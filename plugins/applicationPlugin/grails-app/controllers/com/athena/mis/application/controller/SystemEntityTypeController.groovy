package com.athena.mis.application.controller

import com.athena.mis.application.actions.systementitytype.ListSystemEntityTypeActionService
import com.athena.mis.application.actions.systementitytype.ShowSystemEntityTypeActionService

class SystemEntityTypeController extends BaseController {

    static allowedMethods = [
            show: "POST", list: "POST"
    ]

    ShowSystemEntityTypeActionService showSystemEntityTypeActionService
    ListSystemEntityTypeActionService listSystemEntityTypeActionService

    def show() {
        String view = "/application/systemEntityType/show"
        renderView(showSystemEntityTypeActionService, params, view)
    }

    def list() {
        renderOutput(listSystemEntityTypeActionService, params)
    }
}
