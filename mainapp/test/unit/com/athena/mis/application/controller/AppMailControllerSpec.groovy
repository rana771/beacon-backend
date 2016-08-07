package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appMail.CreateAppAnnouncementActionService
import com.athena.mis.application.actions.appMail.ListAppAnnouncementForComposeActionService
import com.athena.mis.application.actions.appMail.ListAppMailActionService
import com.athena.mis.application.actions.appMail.ListForSendAppMailActionService
import com.athena.mis.application.actions.appMail.SendErrorMailActionService
import com.athena.mis.application.actions.appMail.ShowAppMailActionService
import com.athena.mis.application.actions.appMail.TestAppMailActionService
import com.athena.mis.application.actions.appMail.UpdateAppAnnouncementForComposeActionService
import com.athena.mis.application.actions.appMail.UpdateAppMailActionService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListAppAnnouncementActionServiceModel
import com.athena.mis.application.model.ListForSendAppMailActionServiceModel
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.ListAppAnnouncementActionServiceModelService
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.service.TestDataModelService
import com.athena.mis.application.session.AppSessionService
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/2/2015.
 */

@TestFor(AppMailController)
@Mock([
        AppMail,
        AppMailService,
        RoleService,
        TestDataModelService,
        UpdateAppAnnouncementForComposeActionService,
        ListAppAnnouncementActionServiceModelService,
        ListAppAnnouncementActionServiceModel,
        ListAppAnnouncementForComposeActionService,
        CompanyService,
        AppUserService,
        TestAppMailActionService,
        SendErrorMailActionService,
        ListForSendAppMailActionService,
        ListForSendAppMailActionServiceModel,
        SpringSecurityService,
        CreateAppAnnouncementActionService,
        ListAppMailActionService,
        UpdateAppMailActionService,
        ShowAppMailActionService,
        BaseService,
        AppSessionService

])

class AppMailControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createAppAnnouncementActionService.appSessionService.setAppUser(appUser)
        controller.updateAppMailActionService.appSessionService.setAppUser(appUser)
//        controller.deleteAppMailActionService.appSessionService.setAppUser(appUser)
        controller.listAppMailActionService.appSessionService.setAppUser(appUser)

    }

    def 'Show AppMail'() {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appMail/show'
    }

    def 'Show ForCompose'(){
        when:
        request.method = "POST"
        controller.showForComposeMail()

        then:
        view == '/application/appMail/showComposeMail'
    }

    def 'Show ForSend'(){
        when:
        request.method = "POST"
        controller.showForSend()

        then:
        view == '/application/appMail/showSend'
    }

    def 'Create AppMail'(){

        setup:
        controller.params.id = 2
        controller.params.version = "0"
        controller.params.recipients = "info@athena.com.bd"
        controller.params.subject = "Test AppMail"
        controller.params.body = "This is test"
        controller.params.mimeType = "html"
        controller.params.transactionCode = "TestAppMailActionService"
        controller.params.companyId = 1
        controller.params.isActive = true
        controller.params.isRequiredRoleIds = false
        controller.params.isRequiredRecipients = true
        controller.params.isManualSend = true
        controller.params.controllerName = "appMail"
        controller.params.actionName = "testAppMail"
        controller.params.pluginId = 1
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        controller.params.displayName = "Test Name"
        controller.params.hasSend = false

        when:
        request.method = 'POST'
        controller.createAnnouncement()

        then:
        response.redirectedUrl == null
        response.json.message == "Announcement has been saved successfully"
        response.json.isError == false
    }

    def 'Update AppMail'(){

        setup:

        AppMail appMail = new AppMail(

                version : 0,
                recipients : "info@athena.com.bd",
                subject : "Test AppMail",
                body : "This is test",
                mimeType : "html",
                transactionCode : "TestAppMailActionService",
                companyId : 1,
                isActive : true,
                isRequiredRoleIds : false,
                isManualSend : true,
                controllerName : "appMail",
                actionName : "testAppMail",
                pluginId : 1,
                updatedBy : 1,
                updatedOn : new Date(),
                createdOn : new Date(),
                displayName : "Test Name",
                hasSend : false
        )
        appMail.id = 100
        appMail.save(flush: true, failOnError: true, validate: false)

        controller.params.id = appMail.id
        controller.params.version = "0"
        controller.params.recipients = "info@athena.com.bd"
        controller.params.subject = "Test AppMail"
        controller.params.body = "This is test"
        controller.params.mimeType = "html"
        controller.params.transactionCode = "TestAppMailActionService"
        controller.params.companyId = 1
        controller.params.isActive = true
        controller.params.isRequiredRoleIds = false
        controller.params.isRequiredRecipients = true
        controller.params.isManualSend = true
        controller.params.controllerName = "appMail"
        controller.params.actionName = "testAppMail"
        controller.params.pluginId = 1
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        controller.params.createdOn = new Date()
        controller.params.displayName = "Test Name"
        controller.params.hasSend = false

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectedUrl == null
        response.json.message == "Mail has been updated successfully"
        response.json.isError == false
    }

    def 'Update ForCompose'(){
        setup:
        AppMail appMail = new AppMail(

                version : 0,
                recipients : "info@athena.com.bd",
                subject : "Test AppMail",
                body : "This is test",
                mimeType : "html",
                transactionCode : "TestAppMailActionService",
                companyId : 1,
                isActive : true,
                isRequiredRoleIds : false,
                isManualSend : true,
                controllerName : "appMail",
                actionName : "testAppMail",
                pluginId : 1,
                updatedBy : 1,
                updatedOn : new Date(),
                createdOn : new Date(),
                displayName : "Test Name",
                hasSend : false
        )
        appMail.id = 100
        appMail.save(flush: true, failOnError: true, validate: false)

        controller.params.id = appMail.id
        controller.params.version = "0"
        controller.params.recipients = "info@athena.com.bd"
        controller.params.subject = "Test AppMail"
        controller.params.body = "This is test"
        controller.params.mimeType = "html"
        controller.params.transactionCode = "TestAppMailActionService"
        controller.params.companyId = 1
        controller.params.isActive = true
        controller.params.isRequiredRoleIds = false
        controller.params.isRequiredRecipients = true
        controller.params.isManualSend = true
        controller.params.controllerName = "appMail"
        controller.params.actionName = "testAppMail"
        controller.params.pluginId = 1
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        controller.params.createdOn = new Date()
        controller.params.displayName = "Test Name"
        controller.params.hasSend = false

        when:
        request.method = 'POST'
        controller.updateAnnouncement()

        then:
        response.redirectedUrl == null
        response.json.message == "Announcement has been updated successfully."
        response.json.isError == false
    }

    def 'List AppMail'(){

        setup:

        controller.params.pluginId = 1
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List ForCompose'(){
        setup:

        controller.params.pluginId = 1
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listAnnouncement()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List ForSend'(){
        setup:

        controller.params.pluginId = 1
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listForSend()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
  }
