package com.athena.mis.application.controller

import com.athena.mis.application.actions.vendor.*

class VendorController extends BaseController {

    static allowedMethods = [
            show                  : "POST",
            create                : "POST",
            update                : "POST",
            delete                : "POST",
            list                  : "POST",
            renderVendorThumbImage: "GET",
            renderVendorSmallImage: "GET"
    ]

    ListAppVendorActionService listAppVendorActionService
    CreateAppVendorActionService createAppVendorActionService
    UpdateAppVendorActionService updateAppVendorActionService
    DeleteAppVendorActionService deleteAppVendorActionService
    RenderVendorThumbImageActionService renderVendorThumbImageActionService
    RenderVendorSmallImageActionService renderVendorSmallImageActionService

    /**
     * Show project UI
     */
    def show() {
        render(view: '/application/vendor/show', model: [oId: params.oId ? params.oId : null])
    }

    /**
     * List & Search vendor
     */
    def list() {
        renderOutput(listAppVendorActionService, params)
    }

    /**
     * Create new vendor
     */
    def create() {
        renderOutput(createAppVendorActionService, params)
    }

    /**
     * Update vendor
     */
    def update() {
        renderOutput(updateAppVendorActionService, params)
    }

    /**
     * Delete vendor
     *
     */
    def delete() {
        renderOutput(deleteAppVendorActionService, params)
    }

    def renderVendorThumbImage() {
        Map result = renderVendorThumbImageActionService.executePreCondition(params)
        boolean isError = result.isError
        if (isError) {
            result = renderVendorThumbImageActionService.buildFailureResultForUI(result)
            return result
        }
        result = renderVendorThumbImageActionService.execute(result)
        response.outputStream << result.stream
    }

    def renderVendorSmallImage() {
        Map result = renderVendorSmallImageActionService.executePreCondition(params)
        boolean isError = result.isError
        if (isError) {
            result = renderVendorSmallImageActionService.buildFailureResultForUI(result)
            return result
        }
        result = renderVendorSmallImageActionService.execute(result)
        response.outputStream << result.stream
    }

}
