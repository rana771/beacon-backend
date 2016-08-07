package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.SmsSenderService
import com.athena.mis.application.actions.appsms.CreateAppSmsActionService
import com.athena.mis.application.actions.appsms.DeleteAppSmsActionService
import com.athena.mis.application.actions.appsms.ListAppSmsActionService
import com.athena.mis.application.actions.appsms.ListForComposeSmsActionService
import com.athena.mis.application.actions.appsms.ListForSendSmsActionService
import com.athena.mis.application.actions.appsms.ReComposeSmsActionService
import com.athena.mis.application.actions.appsms.SelectAppSmsActionService
import com.athena.mis.application.actions.appsms.SendForComposeSmsActionService
import com.athena.mis.application.actions.appsms.SendSmsActionService
import com.athena.mis.application.actions.appsms.ShowAppSmsActionService
import com.athena.mis.application.actions.appsms.UpdateAppSmsActionService
import com.athena.mis.application.actions.appsms.UpdateForComposeSmsActionService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.model.ListForComposeSmsActionServiceModel
import com.athena.mis.application.model.ListForSendSmsActionServiceModel
import com.athena.mis.application.service.AppSmsService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.ListForComposeSmsActionServiceModelService
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

import javax.sql.DataSource

/**
 * Created by Asif on 12/28/2015.
 */

@TestFor(AppSmsController)

@Mock([
        AppSms,
        AppSmsService,
        RoleService,
        SmsSenderService,
        AppConfigurationService,
        AppSysConfigCacheService,
        SysConfiguration,
        CompanyService,
        ListForComposeSmsActionServiceModelService,
        ListForSendSmsActionServiceModel,
        ListForComposeSmsActionServiceModel,
        ShowAppSmsActionService,
        SelectAppSmsActionService,
        ListAppSmsActionService,
        UpdateAppSmsActionService,
        SendSmsActionService,
        CreateAppSmsActionService,
        DeleteAppSmsActionService,
        ListForComposeSmsActionService,
        UpdateForComposeSmsActionService,
        SendForComposeSmsActionService,
        ListForSendSmsActionService,
        ReComposeSmsActionService,
        BaseService,
        AppSessionService

])

class AppSmsControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.companyId = 1
        appUser.id = 1

        controller.showAppSmsActionService.appSessionService.setAppUser(appUser)
        controller.selectAppSmsActionService.appSessionService.setAppUser(appUser)
        controller.listAppSmsActionService.appSessionService.setAppUser(appUser)
        controller.updateAppSmsActionService.appSessionService.setAppUser(appUser)
        controller.sendSmsActionService.appSessionService.setAppUser(appUser)
        controller.createAppSmsActionService.appSessionService.setAppUser(appUser)
        controller.deleteAppSmsActionService.appSessionService.setAppUser(appUser)
        controller.listForComposeSmsActionService.appSessionService.setAppUser(appUser)
        controller.updateForComposeSmsActionService.appSessionService.setAppUser(appUser)
        controller.sendForComposeSmsActionService.appSessionService.setAppUser(appUser)
        controller.listForSendSmsActionService.appSessionService.setAppUser(appUser)
        controller.reComposeSmsActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Sms' (){

        when:
        request.method = "POST"
        controller.showSms()

        then:
        view == '/application/appSms/show'
    }

    def 'Show ForCompose' (){

        when:
        request.method = "POST"
        controller.showForCompose()

        then:
        view == '/application/appSms/showCompose'
    }

    def 'Show ForSend' (){

        when:
        request.method = "POST"
        controller.showForSend()

        then:
        view == '/application/appSms/showSend'
    }

    def 'Select SMS'(){

        setup:

        AppSms sms = new AppSms(
                version : 0,
                body : "This is for Testing",
                transactionCode : "SendSmsActionService",
                companyId : 1,
                isActive : true,
                recipients : "01711114599",
                isRequiredRecipients : true,
                isManualSend : true,
                pluginId : 1,
                updatedBy : 1,
                hasSend : true,
                roleId : 1
        )
        sms.id = 100
        sms.save(flush: true, failOnError: true, validate: false)

        controller.params.id = sms.id

        when:
        request.method = "POST"
        controller.selectSms()

        then:
        response.redirectedUrl == null
    }

    def 'Send SMS'(){
        setup:

        AppSms sms = new AppSms(
                version : 0,
                body : "This is for Testing",
                transactionCode : "SendSmsActionService",
                companyId : 1,
                isActive : true,
                recipients : "01711114599",
                isRequiredRecipients : true,
                isManualSend : true,
                pluginId : 1,
                updatedBy : 1,
                hasSend : true,
                roleId : 1
        )
        sms.id = 100
        sms.save(flush: true, failOnError: true, validate: false)

        SysConfiguration sc = new SysConfiguration(
                version : 0,
                key : "mis.application.deploymentMode",
                value : "2",
                description : "Test Config",
                transactionCode : " GetSysConfigLoginTemplateActionService, AppCreateTestDataActionService, AppDeleteTestDataActionService, AppMailService, AppSmsService, LoginController ",
                message : "This is for Test",
                pluginId : 1,
                companyId : 1,
                updatedBy : 1,
                updatedOn : new Date()
        )
        sc.id = 1
        sc.save(flush: true, failOnError: true, validate: false)

        controller.params.transactionCode = sms.transactionCode



        when:
        request.method = "POST"
        controller.sendSms()

        then:
        response.redirectUrl == null
        response.json.message == "SMS has been sent successfully"
        response.json.isError == false
    }

    def 'Send ForCompose'(){

        setup:

        AppSms sms = new AppSms(
                version : 0,
                body : "This is for Testing",
                transactionCode : "SendSmsActionService",
                companyId : 1,
                isActive : true,
                recipients : "01711114599",
                isRequiredRecipients : true,
                isManualSend : true,
                pluginId : 1,
                updatedBy : 1,
                hasSend : true,
                roleId : 1
        )
        sms.id = 100
        sms.save(flush: true, failOnError: true, validate: false)

        controller.params.id = sms.id

        when:
        request.method = "POST"
        controller.sendForCompose()

        then:
        response.redirectUrl == null
        response.json.message == "SMS has been sent successfully"
        response.json.isError == false
    }

    def 'Create SMS'(){
        setup:

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.body = "This is for Testing"
        controller.params.transactionCode = "SendSmsActionService"
        controller.params.companyId = 1
        controller.params.isActive = true
        controller.params.isRequiredRecipients = true
        controller.params.isManualSend = true
        controller.params.pluginId = 1
        controller.params.updatedBy = 1
        controller.params.hasSend = true
        controller.params.roleId = 1

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "SMS has been saved successfully"
        response.json.isError == false
    }

    def 'Update SMS'(){
        setup:

        AppSms sms = new AppSms(
                version : 0,
                body : "This is for Testing",
                transactionCode : "SendSmsActionService",
                companyId : 1,
                isActive : true,
                recipients : "01711114599",
                isRequiredRecipients : true,
                isManualSend : true,
                pluginId : 1,
                updatedBy : 1,
                hasSend : true,
                roleId : 1
        )
        sms.id = 100
        sms.save(flush: true, failOnError: true, validate: false)

        controller.params.id = sms.id
        controller.params.version = "0"
        controller.params.body = "This is for Testing"
        controller.params.transactionCode = "SendSmsActionService"
        controller.params.companyId = 1
        controller.params.isActive = true
        controller.params.recipients = "01711114599"
        controller.params.isRequiredRecipients = true
        controller.params.isManualSend = true
        controller.params.pluginId = 1
        controller.params.updatedBy = 1
        controller.params.hasSend = true
        controller.params.roleId = 1

        when:
        request.method = 'POST'
        controller.updateSms()

        then:
        response.redirectUrl == null
        response.json.message == "SMS has been updated successfully"
        response.json.isError == false
    }

    def 'Update ForCompose'(){
        setup:

        AppSms sms = new AppSms(
                version : 0,
                body : "This is for Testing",
                transactionCode : "SendSmsActionService",
                companyId : 1,
                isActive : true,
                recipients : "01711114599",
                isRequiredRecipients : true,
                isManualSend : true,
                pluginId : 1,
                updatedBy : 1,
                hasSend : true,
                roleId : 1
        )
        sms.id = 100
        sms.save(flush: true, failOnError: true, validate: false)


        controller.params.id = sms.id
        controller.params.version = "0"
        controller.params.body = "This is for Testing"
        controller.params.transactionCode = "SendSmsActionService"
        controller.params.companyId = 1
        controller.params.isActive = true
        controller.params.recipients = "01711114599"
        controller.params.isRequiredRecipients = true
        controller.params.isManualSend = true
        controller.params.pluginId = 1
        controller.params.updatedBy = 1
        controller.params.hasSend = true
        controller.params.roleId = 1

        when:
        request.method = 'POST'
        controller.updateForCompose()

        then:
        response.redirectUrl == null
        response.json.message == "SMS has been updated successfully"
        response.json.isError == false
    }

    def 'List SMS' () {
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
        controller.listSms()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List ForCompose' () {
        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listForCompose()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List ForSend' () {
        setup:

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

    def 'Recompose' (){
        setup:

        AppSms sms = new AppSms(
                version : 0,
                body : "This is for Testing",
                transactionCode : "SendSmsActionService",
                companyId : 1,
                isActive : true,
                recipients : "01711114599",
                isRequiredRecipients : true,
                isManualSend : true,
                pluginId : 1,
                updatedBy : 1,
                hasSend : true,
                roleId : 1
        )
        sms.id = 100
        sms.save(flush: true, failOnError: true, validate: false)

        controller.params.id = sms.id

        when:
        request.method = 'POST'
        controller.reCompose()

        then:
        response.redirectUrl == null
        response.json.message == "SMS has been re-composed successfully"
        response.json.isError == false
    }
}
