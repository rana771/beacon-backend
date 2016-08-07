package com.athena.mis.application.controller

import com.athena.mis.application.actions.testdomain.CreateTestDomainActionService
import com.athena.mis.application.actions.testdomain.DeleteTestDomainActionService
import com.athena.mis.application.actions.testdomain.ListTestDomainActionService
import com.athena.mis.application.actions.testdomain.UpdateTestDomainActionService

class TestDomainController extends BaseController {

    CreateTestDomainActionService createTestDomainActionService
    UpdateTestDomainActionService updateTestDomainActionService
    ListTestDomainActionService listTestDomainActionService
    DeleteTestDomainActionService deleteTestDomainActionService

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    /**
     * Show project UI
     */
    def show() {
        render(view: "/application/testdomain/show", model: [oId: params.oId?params.oId:null])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createTestDomainActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updateTestDomainActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deleteTestDomainActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listTestDomainActionService, params)
    }
}

