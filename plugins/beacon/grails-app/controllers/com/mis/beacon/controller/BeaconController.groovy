package com.mis.beacon.controller

import com.athena.mis.application.controller.BaseController
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Beacon
import com.mis.beacon.Marchant
import com.mis.beacon.Zone
import com.mis.beacon.beacon.CreateBeaconActionService
import com.mis.beacon.beacon.DeleteBeaconActionService
import com.mis.beacon.beacon.ListBeaconActionService
import com.mis.beacon.beacon.UpdateBeaconActionService
import grails.converters.JSON

class BeaconController extends BaseController {

    CreateBeaconActionService createBeaconActionService
    UpdateBeaconActionService updateBeaconActionService
    ListBeaconActionService listBeaconActionService
    DeleteBeaconActionService deleteBeaconActionService

//    static allowedMethods = [
//            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
//    ]

    /**
     * Show project UI
     */
    def show() {
        AppUser appUser=createBeaconActionService.getAppUser();
        Marchant marchant=Marchant.findByAppUser(appUser)
        List<Zone> zoneList=Zone.findAllByMarchant(marchant)
        render(view: "/beacon/show",model: [marchant:marchant,zoneList:zoneList])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createBeaconActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updateBeaconActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deleteBeaconActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listBeaconActionService, params)
    }

    def beaconList(){
        AppUser appUser=listBeaconActionService.getAppUser();
        Marchant marchant=Marchant.findByAppUser(appUser)
        List <Beacon> beaconList=Beacon.findAllByMarchant(marchant)
        render beaconList as JSON
    }

}

