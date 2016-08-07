package com.mis.beacon.controller

import com.athena.mis.application.controller.BaseController
import com.mis.beacon.place.CreatePlaceActionService
import com.mis.beacon.place.DeletePlaceActionService
import com.mis.beacon.place.ListPlaceActionService
import com.mis.beacon.place.UpdatePlaceActionService

class PlaceController extends BaseController {

    CreatePlaceActionService createPlaceActionService
    UpdatePlaceActionService updatePlaceActionService
    ListPlaceActionService listPlaceActionService
    DeletePlaceActionService deletePlaceActionService

//    static allowedMethods = [
//            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
//    ]

    /**
     * Show project UI
     */
    def show() {
        render(view: "/place/show", model: [oId: params.oId?params.oId:null])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createPlaceActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updatePlaceActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deletePlaceActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listPlaceActionService, params)
    }
}

