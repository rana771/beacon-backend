package com.athena.mis.application.controller

import com.athena.mis.application.actions.appserverinstance.*

class AppServerInstanceController extends BaseController {
    static allowedMethods = [show: "POST", list: "POST", create: "POST", update: "POST", delete: "POST"]

    ListAppServerInstanceActionService listAppServerInstanceActionService
    CreateAppServerInstanceActionService createAppServerInstanceActionService
    UpdateAppServerInstanceActionService updateAppServerInstanceActionService
    DeleteAppServerInstanceActionService deleteAppServerInstanceActionService
    TestServerInstanceConnectionActionService testServerInstanceConnectionActionService

    def show() {
        render(view: "/application/appServerInstance/show")
    }

    def list() {
        renderOutput(listAppServerInstanceActionService, params)
    }

    def create() {
        renderOutput(createAppServerInstanceActionService, params)
    }

    def update() {
        renderOutput(updateAppServerInstanceActionService, params)
    }

    def delete() {
        renderOutput(deleteAppServerInstanceActionService, params)
    }

    def testServerConnection() {
        renderOutput(testServerInstanceConnectionActionService, params)
    }
}
