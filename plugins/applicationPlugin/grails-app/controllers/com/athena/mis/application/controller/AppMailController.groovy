package com.athena.mis.application.controller

import com.athena.mis.application.actions.appMail.*
import com.athena.mis.application.session.AppSessionService
import org.springframework.beans.factory.annotation.Autowired

class AppMailController extends BaseController {

    static allowedMethods = [
            createAnnouncement          : "POST",
            show                        : "POST",
            update                      : "POST",
            deleteAnnouncement          : "POST",
            list                        : "POST",
            testAppMail                 : "POST",
            sendMailForError            : "POST",
            listAnnouncement            : "POST",
            listAnnouncementForDashboard: "POST",
            updateAnnouncement          : "POST",
            sendAnnouncement            : "POST",
            showForSend                 : "POST",
            listForSend                 : "POST",
            reComposeAnnouncement       : "POST",
            showForComposeMail          : "POST",
            listMail                    : "POST",
            createMail                  : "POST",
            updateMail                  : "POST",
            deleteMail                  : "POST",
            sendMail                    : "POST",
            showForSentMail             : "POST",
            listForSentMail             : "POST",
            createAndSend               : "POST",
            uploadAttachment            : "POST"
    ]

    ShowAppMailActionService showAppMailActionService
    UpdateAppMailActionService updateAppMailActionService
    ListAppMailActionService listAppMailActionService
    TestAppMailActionService testAppMailActionService
    SendErrorMailActionService sendErrorMailActionService
    CreateAppAnnouncementActionService createAppAnnouncementActionService
    DeleteAppAnnouncementActionService deleteAppAnnouncementActionService
    SendAppAnnouncementActionService sendAppAnnouncementActionService
    ListAppAnnouncementForComposeActionService listAppAnnouncementForComposeActionService
    UpdateAppAnnouncementForComposeActionService updateAppAnnouncementForComposeActionService
    ListForSendAppMailActionService listForSendAppMailActionService
    ReComposeAppAnnouncementActionService reComposeAppAnnouncementActionService
    ListAppMailForMailActionService listAppMailForMailActionService
    CreateAppMailForMailActionService createAppMailForMailActionService
    DeleteAppMailForMailActionService deleteAppMailForMailActionService
    UpdateAppMailForMailActionService updateAppMailForMailActionService
    SendAppMailForMailActionService sendAppMailForMailActionService
    ListAnnouncementAppMailActionService listAnnouncementAppMailActionService
    ListAppMailForSentMailActionService listAppMailForSentMailActionService
    PreviewAppMailActionService previewAppMailActionService
    CreateAndSendAppMailForMailActionService createAndSendAppMailForMailActionService
    UploadAppAttachmentForMailActionService uploadAppAttachmentForMailActionService

    @Autowired
    AppSessionService appSessionService

    /**
     * Show AppMail
     */
    def show() {
        String view = '/application/appMail/show'
        renderView(showAppMailActionService, params, view)
    }

    /**
     * Update AppMail
     */
    def update() {
        renderOutput(updateAppMailActionService, params)
    }

    /**
     * Get list of AppMail
     */
    def list() {
        renderOutput(listAppMailActionService, params)
    }

    /**
     * Send mail for testing purpose
     */
    def testAppMail() {
        renderOutput(testAppMailActionService, params)
    }

    /**
     * Send mail to report unhandled  error
     */
    def sendMailForError() {
        renderOutput(sendErrorMailActionService, params)
    }

    /**
     * create announcement
     */
    def createAnnouncement() {
        renderOutput(createAppAnnouncementActionService, params)
    }

    /**
     * delete announcement
     */
    def deleteAnnouncement() {
        renderOutput(deleteAppAnnouncementActionService, params)
    }

    /**
     * send announcement
     */
    def sendAnnouncement() {
        renderOutput(sendAppAnnouncementActionService, params)
    }

    /**
     * show UI for compose mail
     */
    def showAnnouncement() {
        String userName = appSessionService.getAppUser().username
        render(view: '/application/appMail/showCompose', model: [userName: userName])
    }

    /**
     * list compose mail
     */
    def listAnnouncement() {
        renderOutput(listAppAnnouncementForComposeActionService, params)
    }

    def listAnnouncementForDashboard() {
        renderOutput(listAnnouncementAppMailActionService, params)
    }

    /**
     * update composed mail
     */
    def updateAnnouncement() {
        renderOutput(updateAppAnnouncementForComposeActionService, params)
    }

    /**
     * show UI for sent mail
     */
    def showForSend() {
        render(view: '/application/appMail/showSend')
    }

    /**
     * show list of sent mail
     */
    def listForSend() {
        renderOutput(listForSendAppMailActionService, params)
    }

    /**
     * re-compose mail
     */
    def reComposeAnnouncement() {
        renderOutput(reComposeAppAnnouncementActionService, params)
    }

    /**
     * show mail compose
     */
    def showForComposeMail() {
        render(view: '/application/appMail/showComposeMail')
    }

    /**
     * list of mail
     */
    def listMail() {
        renderOutput(listAppMailForMailActionService, params)
    }

    /**
     * create mail
     */
    def createMail() {
        renderOutput(createAppMailForMailActionService, params)
    }

    /**
     * update mail
     */
    def updateMail() {
        renderOutput(updateAppMailForMailActionService, params)
    }

    /**
     * delete mail
     */
    def deleteMail() {
        renderOutput(deleteAppMailForMailActionService, params)
    }

    /**
     * send mail
     */
    def sendMail() {
        renderOutput(sendAppMailForMailActionService, params)
    }

    def showForSentMail() {
        render(view: '/application/appMail/showForSentMail')
    }

    def listForSentMail() {
        renderOutput(listAppMailForSentMailActionService, params)
    }

    def previewMail() {
        renderOutput(previewAppMailActionService, params)
    }

    def createAndSend() {
        renderOutput(createAndSendAppMailForMailActionService, params)
    }

    def uploadAttachment() {
        renderOutput(uploadAppAttachmentForMailActionService, params)
    }
}
