package com.mis.beacon.controller

import com.athena.mis.application.controller.BaseController
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Marchant
import com.mis.beacon.marchant.CreateMarchantActionService
import com.mis.beacon.marchant.DeleteMarchantActionService
import com.mis.beacon.marchant.ListMarchantActionService
import com.mis.beacon.marchant.UpdateMarchantActionService

class MarchantController extends BaseController {

    CreateMarchantActionService createMarchantActionService
    UpdateMarchantActionService updateMarchantActionService
    ListMarchantActionService listMarchantActionService
    DeleteMarchantActionService deleteMarchantActionService

//    static allowedMethods = [
//            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
//    ]

    /**
     * Show project UI
     */
    def show() {
        AppUser appUser=createMarchantActionService.getAppUser()
        Marchant marchant=Marchant.findByAppUser(appUser)
        render(view: "/marchant/show",model: [marchant:marchant,appUser:appUser])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createMarchantActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updateMarchantActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deleteMarchantActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listMarchantActionService, params)
    }
}

