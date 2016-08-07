package com.athena.mis.application.controller

import com.athena.mis.application.actions.appnote.*

class AppNoteController extends BaseController {

    static allowedMethods = [
            list: "POST", show: "POST", create: "POST", update: "POST", delete: "POST",
            reloadEntityNote: "POST", listEntityNote: "POST"
    ]

    CreateAppNoteActionService createAppNoteActionService
    ListAppNoteActionService listAppNoteActionService
    ShowAppNoteActionService showAppNoteActionService
    UpdateAppNoteActionService updateAppNoteActionService
    DeleteAppNoteActionService deleteAppNoteActionService
    ListAppNoteForListViewActionService listAppNoteForListViewActionService

    def show() {
        String view = "/application/appNote/show"
        renderView(showAppNoteActionService, params, view)
    }

    def create() {
        renderOutput(createAppNoteActionService, params)
    }

    def update() {
        renderOutput(updateAppNoteActionService, params)
    }

    def list() {
        renderOutput(listAppNoteActionService, params)
    }

    def delete() {
        renderOutput(deleteAppNoteActionService, params)
    }

    def reloadEntityNote() {
        render app.appNote(params, null)
    }

    def viewEntityNote() {
        render(view: "/application/appNote/showNotes", model: [params: params])
    }

    def listEntityNote() {
        renderOutput(listAppNoteForListViewActionService, params)
    }
}
