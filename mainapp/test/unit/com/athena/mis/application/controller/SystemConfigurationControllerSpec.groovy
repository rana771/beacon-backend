package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.sysconfiguration.ListSysConfigurationActionService
import com.athena.mis.application.actions.sysconfiguration.ShowSysConfigurationActionService
import com.athena.mis.application.actions.sysconfiguration.UpdateSysConfigurationActionService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/28/2015.
 */

@TestFor(SystemConfigurationController)

//        AppSysConfigCacheService,


@Mock([
        SysConfiguration,
        SysConfigurationService,
        Company,
        CompanyService,
        ShowSysConfigurationActionService,
        AppConfigurationService,
        AppSysConfigCacheService,
        UpdateSysConfigurationActionService,
        ListSysConfigurationActionService,
        BaseService,
        AppSessionService
])

class SystemConfigurationControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.showSysConfigurationActionService.appSessionService.setAppUser(appUser)
        controller.updateSysConfigurationActionService.appSessionService.setAppUser(appUser)
        controller.listSysConfigurationActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show SystemConfiguration' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/sysConfiguration/show'
    }

    def 'Update SystemConfiguration'(){
        setup:

        SysConfiguration sc = new SysConfiguration(
                version : 0,
                key : "mis.application.defaultPasswordExpireDuration",
                value : "180",
                description : "Test Config",
                transactionCode : "UpdateAppCompanyUserActionService",
                message : "This is for Test",
                pluginId : 1,
                companyId : 1,
                updatedBy : 1,
                updatedOn : new Date()
        )

        sc.id = 1
        sc.save(flush: true, failOnError: true, validate: false)

        controller.params.id = sc.id
        controller.params.version = "0"
        controller.params.key = "mis.application.defaultPasswordExpireDuration"
        controller.params.value = "240"
        controller.params.description = "Test Config"
        controller.params.transactionCode = "UpdateAppCompanyUserActionService"
        controller.params.message = "This is for Update Test"
        controller.params.pluginId = 1
        controller.params.companyId = 1
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        when:
        request.method = 'POST'
        controller.update()
        then:
        response.redirectUrl == null
        response.json.message == "System Configuration Information has been updated successfully"
        response.json.isError == false
    }

    def 'List SystemConfiguration' () {
        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.pluginId = 1

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
