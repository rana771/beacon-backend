package com.athena.mis.application.controller

import com.athena.mis.application.actions.district.*

class DistrictController extends BaseController{

    static allowedMethods = [
            show: "POST",
            create: "POST",
            update: "POST",
            list: "POST",
            delete: "POST",
            reloadDistrictDropDown: "POST"
    ];

    ListDistrictActionService listDistrictActionService
    CreateDistrictActionService createDistrictActionService
    DeleteDistrictActionService deleteDistrictActionService
    UpdateDistrictActionService updateDistrictActionService

    /**
     * show District
     */
    def show() {
        render(view: "/application/district/show")
    }

    /**
     * delete District
     */
    def delete() {
        renderOutput(deleteDistrictActionService,params)
    }

    /*
     * build a closure to create a new district
     */
    def create() {
       renderOutput(createDistrictActionService,params)
    }

    /**
     * list and search district
     */
    def list() {
        renderOutput(listDistrictActionService,params)
    }

    /**
     * update district
     */
    def update() {
       renderOutput(updateDistrictActionService,params)
    }

    def reloadDistrictDropDown() {
        render app.dropDownDistrict(params)
    }
}


