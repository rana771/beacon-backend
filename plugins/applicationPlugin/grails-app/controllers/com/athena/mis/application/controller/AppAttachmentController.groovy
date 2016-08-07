package com.athena.mis.application.controller

import com.athena.mis.application.actions.appattachment.*
import com.athena.mis.application.entity.AppAttachment

class AppAttachmentController extends BaseController {

    static allowedMethods = [list: "POST",  show: "POST", create: "POST", upload: "POST",
            update: "POST", delete: "POST", downloadContent: "GET", downloadImageContent: "GET"]

    ShowAppAttachmentActionService showAppAttachmentActionService
    CreateAppAttachmentActionService createAppAttachmentActionService
    DeleteAppAttachmentActionService deleteAppAttachmentActionService
    ListAppAttachmentActionService listAppAttachmentActionService
    UpdateAppAttachmentActionService updateAppAttachmentActionService
    DownloadAppAttachmentActionService downloadAppAttachmentActionService
    UploadAppAttachmentActionService uploadAppAttachmentActionService
    DownloadAppImageAttachmentActionService downloadAppImageAttachmentActionService

    def show() {
        String view = "/application/appAttachment/show"
        renderView(showAppAttachmentActionService, params, view)
    }

    def list() {
        renderOutput(listAppAttachmentActionService, params)
    }

    def create() {
        renderOutput(createAppAttachmentActionService, params)
    }

    def upload() {
        renderOutput(uploadAppAttachmentActionService, params)
    }

    def update() {
        renderOutput(updateAppAttachmentActionService, params)
    }

    def delete() {
        renderOutput(deleteAppAttachmentActionService, params)
    }

    def downloadContent() {
        Map resultMap = (Map) getServiceResponse(downloadAppAttachmentActionService, params)
        AppAttachment appAttachment = (AppAttachment) resultMap.appAttachment
        response.contentType = grailsApplication.config.grails.mime.types[appAttachment.extension]
        response.setHeader("Content-disposition", "attachment;filename=${appAttachment.fileName}")
        response.outputStream << appAttachment.content
    }

    def downloadImageContent() {
        Map resultMap = (Map) getServiceResponse(downloadAppImageAttachmentActionService, params)
        boolean isError = resultMap.isError
        if (isError) {
            response.outputStream << resultMap.stream
            return
        }
        AppAttachment appAttachment = (AppAttachment) resultMap.appAttachment
        response.contentType = grailsApplication.config.grails.mime.types[appAttachment.extension]
        response.setHeader("Content-disposition", "attachment;filename=${appAttachment.fileName}")
        response.outputStream << appAttachment.content
    }
}
