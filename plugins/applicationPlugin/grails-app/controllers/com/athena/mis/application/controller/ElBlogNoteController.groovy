package com.athena.mis.application.controller

import com.athena.mis.application.actions.elblognote.CreateElBlogNoteActionService
import com.athena.mis.application.actions.elblognote.DeleteElBlogNoteActionService
import com.athena.mis.application.actions.elblognote.ListElBlogNoteActionService
import com.athena.mis.application.actions.elblognote.UpdateElBlogNoteActionService

class ElBlogNoteController extends BaseController {

    CreateElBlogNoteActionService createElBlogNoteActionService
    UpdateElBlogNoteActionService updateElBlogNoteActionService
    ListElBlogNoteActionService listElBlogNoteActionService
    DeleteElBlogNoteActionService deleteElBlogNoteActionService

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    /**
     * Show project UI
     */
    def show() {
        render(view: "/application/elblognote/show", model: [oId: params.oId?params.oId:null])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createElBlogNoteActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updateElBlogNoteActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deleteElBlogNoteActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listElBlogNoteActionService, params)
    }
}

