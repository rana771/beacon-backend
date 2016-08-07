package com.athena.mis.application.controller

import com.athena.mis.application.actions.appmessage.DeleteAppMessageActionService
import com.athena.mis.application.actions.appmessage.ListAppMessageActionService
import com.athena.mis.application.actions.appmessage.MarkAsUnReadAppMessageActionService
import com.athena.mis.application.actions.appmessage.PreviewAppMessageActionService

class AppMessageController extends BaseController {

    static allowedMethods = [show: "POST", list: "POST", markAsUnRead: "POST", preview: "POST"]

    ListAppMessageActionService listAppMessageActionService
    DeleteAppMessageActionService deleteAppMessageActionService
    MarkAsUnReadAppMessageActionService markAsUnReadAppMessageActionService
    PreviewAppMessageActionService previewAppMessageActionService


    def show() {
        render(view: '/application/appMessage/show')
    }

    def list() {
        renderOutput(listAppMessageActionService, params)
    }

    def preview() {
        renderOutput(previewAppMessageActionService, params)
    }

    def delete() {
        renderOutput(deleteAppMessageActionService, params)
    }

    def markAsUnRead() {
        renderOutput(markAsUnReadAppMessageActionService, params)
    }
}
