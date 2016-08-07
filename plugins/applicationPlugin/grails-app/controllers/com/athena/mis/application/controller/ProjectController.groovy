package com.athena.mis.application.controller

import com.athena.mis.application.actions.project.CreateProjectActionService
import com.athena.mis.application.actions.project.DeleteProjectActionService
import com.athena.mis.application.actions.project.ListProjectActionService
import com.athena.mis.application.actions.project.UpdateProjectActionService

class ProjectController extends BaseController {

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    CreateProjectActionService createProjectActionService
    UpdateProjectActionService updateProjectActionService
    DeleteProjectActionService deleteProjectActionService
    ListProjectActionService listProjectActionService

    /**
     * Show project UI
     */
    def show() {
        render(view: '/application/project/show', model: [oId: params.oId?params.oId:null])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createProjectActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updateProjectActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deleteProjectActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listProjectActionService, params)
    }
}

