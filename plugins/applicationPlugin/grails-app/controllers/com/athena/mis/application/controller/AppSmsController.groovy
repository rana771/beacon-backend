package com.athena.mis.application.controller

import com.athena.mis.application.actions.appsms.*

class AppSmsController extends BaseController {

    static allowedMethods = [
            showSms: "POST", updateSms: "POST", listSms: "POST", sendSms: "POST", create: "POST",
            delete: "POST", showForCompose: "POST", listForCompose: "POST", updateForCompose: "POST",
            sendForCompose: "POST", showForSend: "POST", listForSend: "POST", reCompose: "POST"
    ]

    ShowAppSmsActionService showAppSmsActionService
    SelectAppSmsActionService selectAppSmsActionService
    ListAppSmsActionService listAppSmsActionService
    UpdateAppSmsActionService updateAppSmsActionService
    SendSmsActionService sendSmsActionService
    CreateAppSmsActionService createAppSmsActionService
    DeleteAppSmsActionService deleteAppSmsActionService
    ListForComposeSmsActionService listForComposeSmsActionService
    UpdateForComposeSmsActionService updateForComposeSmsActionService
    SendForComposeSmsActionService sendForComposeSmsActionService
    ListForSendSmsActionService listForSendSmsActionService
    ReComposeSmsActionService reComposeSmsActionService

    /**
     * Show SMS
     */
    def showSms() {
        String view = '/application/appSms/show'
        renderView(showAppSmsActionService, params, view)
    }

    /**
     * Select SMS
     */
    def selectSms() {
        renderOutput(selectAppSmsActionService, params)
    }

    /**
     * Update SMS
     */
    def updateSms() {
        renderOutput(updateAppSmsActionService, params)
    }

    /**
     * List SMS
     */
    def listSms() {
        renderOutput(listAppSmsActionService, params)
    }

    /**
     * Send test Sms
     */
    def sendSms() {
        renderOutput(sendSmsActionService, params)
    }

    /**
     * Create SMS
     */
    def create() {
        renderOutput(createAppSmsActionService, params)
    }

    /**
     * Delete SMS
     */
    def delete() {
        renderOutput(deleteAppSmsActionService, params)
    }

    /**
     * show UI for compose sms
     */
    def showForCompose() {
        render(view: '/application/appSms/showCompose')
    }

    /**
     * List compose SMS
     */
    def listForCompose() {
        renderOutput(listForComposeSmsActionService, params)
    }

    /**
     * Update SMS for compose
     */
    def updateForCompose() {
        renderOutput(updateForComposeSmsActionService, params)
    }

    /**
     * Send composed SMS
     */
    def sendForCompose() {
        renderOutput(sendForComposeSmsActionService, params)
    }

    /**
     * show UI for send sms
     */
    def showForSend() {
        render(view: '/application/appSms/showSend')
    }

    /**
     * List send SMS
     */
    def listForSend() {
        renderOutput(listForSendSmsActionService, params)
    }

    /**
     * Re-Compose SMS
     */
    def reCompose() {
        renderOutput(reComposeSmsActionService, params)
    }
}
