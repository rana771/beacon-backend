package com.athena.mis.application.controller

import com.athena.mis.application.actions.testData.AppCreateTestDataActionService
import com.athena.mis.application.actions.testData.AppDeleteTestDataActionService
import com.athena.mis.application.actions.testData.ListTestDataActionService

class TestDataController extends BaseController {

    static allowedMethods = [
            list: "POST",
            show: "POST",
            create: "POST",
            delete: "POST"
    ]

    ListTestDataActionService listTestDataActionService
    AppCreateTestDataActionService appCreateTestDataActionService
    AppDeleteTestDataActionService appDeleteTestDataActionService

    def list() {
        renderOutput(listTestDataActionService, params)
    }

    def show() {
        render(view: '/application/testData/show', model: [oId: params.oId ? params.oId : null, url: params.url ? params.url : null])
    }

    def create() {
        renderOutput(appCreateTestDataActionService, params)
    }

    def delete() {
        renderOutput(appDeleteTestDataActionService, params)
    }
}
