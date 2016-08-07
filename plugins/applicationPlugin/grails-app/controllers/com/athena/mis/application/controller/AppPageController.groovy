package com.athena.mis.application.controller

import com.athena.mis.application.actions.apppage.CreateAppPageActionService
import com.athena.mis.application.actions.apppage.DeleteAppPageActionService
import com.athena.mis.application.actions.apppage.ListAppPageActionService

import com.athena.mis.application.actions.apppage.RenderFromAppPageActionService
import com.athena.mis.application.actions.apppage.UpdateAppPageActionService
import grails.gsp.PageRenderer

class AppPageController extends BaseController {

    static allowedMethods = [
            show  : "POST",
            list  : "POST",
            create: "POST",
            update: "POST",
            delete: "POST",
    ]

    CreateAppPageActionService createAppPageActionService
    DeleteAppPageActionService deleteAppPageActionService
    ListAppPageActionService listAppPageActionService
    UpdateAppPageActionService updateAppPageActionService
    RenderFromAppPageActionService renderFromAppPageActionService
    PageRenderer groovyPageRenderer

    def show() {
        render(view: '/application/appPage/show')
    }

    def list() {
        renderOutput(listAppPageActionService, params)
    }

    def create() {
        renderOutput(createAppPageActionService, params)
    }

    def update() {
        renderOutput(updateAppPageActionService, params)
    }

    def delete() {
        renderOutput(deleteAppPageActionService, params)
    }

    def showPage() {
        Map result = this.getServiceResponse(renderFromAppPageActionService, params)
        String html = groovyPageRenderer.render(template: "/application/appPage/tmplPage", model: [page: result.page] )
        render(html)
    }
}