package com.mis.beacon.controller

import com.athena.mis.application.controller.BaseController
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Marchant
import com.mis.beacon.Zone
import com.mis.beacon.zone.CreateZoneActionService
import com.mis.beacon.zone.DeleteZoneActionService
import com.mis.beacon.zone.ListZoneActionService
import com.mis.beacon.zone.UpdateZoneActionService
import grails.converters.JSON

class ZoneController extends BaseController {

    CreateZoneActionService createZoneActionService
    UpdateZoneActionService updateZoneActionService
    ListZoneActionService listZoneActionService
    DeleteZoneActionService deleteZoneActionService

//    static allowedMethods = [
//            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
//    ]

    /**
     * Show project UI
     */
    def show() {
        render(view: "/zone/show")
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createZoneActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updateZoneActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deleteZoneActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listZoneActionService, params)
    }

    def zoneList(){
        AppUser appUser=listZoneActionService.getAppUser();
        Marchant marchant=Marchant.findByAppUser(appUser)
        List <Zone> zoneList=Zone.findAllByMarchant(marchant)
        render zoneList as JSON
    }

}

