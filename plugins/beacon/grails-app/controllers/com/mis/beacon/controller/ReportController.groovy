package com.mis.beacon.controller

import com.athena.mis.application.session.AppSessionService

class ReportController {
    AppSessionService appSessionService
    def showReport = {
        render view: 'show'
    }

    def qrVendorReport = {
        try {
            params._format = "PDF"
            params._name = "QR_CODE_VENDOR_REPORT";
            params._file = "qrCodeVendorReport.jasper"
            params.user_id = appSessionService.getAppUser().id.toString()
            chain(controller: 'jasper', action: 'index', model: [data: []], params: params)
        }catch (Exception ex){
            println(ex.message)
        }
//        println("Hello")
    }

    def visitorReport = {
        try {
            params._format = "PDF"
            params._name = "QR_CODE_VISITOR_REPORT";
            params._file = "qrVisitorReport.jasper"
            params.user_id = appSessionService.getAppUser().id.toString()
            chain(controller: 'jasper', action: 'index', model: [data: []], params: params)
        }catch (Exception ex){
            println(ex.message)
        }
    }

    def visitorEngagementReport = {
        try {
            params._format = "PDF"
            params._name = "VISITOR_ENGAGEMENT_REPORT";
            params._file = "visitorEngagementReport.jasper"
            params.user_id = appSessionService.getAppUser().id.toString()
            chain(controller: 'jasper', action: 'index', model: [data: []], params: params)
        }catch (Exception ex){
            println(ex.message)
        }
    }
}
