package com.mis.beacon.controller

import com.athena.mis.application.controller.BaseController
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Marchant
import com.mis.beacon.campaign.CreateCampaignActionService
import com.mis.beacon.campaign.DeleteCampaignActionService
import com.mis.beacon.campaign.ListCampaignActionService
import com.mis.beacon.campaign.UpdateCampaignActionService

class CampaignController extends BaseController {

    CreateCampaignActionService createCampaignActionService
    UpdateCampaignActionService updateCampaignActionService
    ListCampaignActionService listCampaignActionService
    DeleteCampaignActionService deleteCampaignActionService

//    static allowedMethods = [
//            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
//    ]

    /**
     * Show project UI
     */
    def show() {
        AppUser appUser=createCampaignActionService.getAppUser();
        Marchant marchant=Marchant.findByAppUser(appUser)
        render(view: "/campaign/show",model: [marchant:marchant])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(createCampaignActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(updateCampaignActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(deleteCampaignActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(listCampaignActionService, params)
    }
}

