package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.apptheme.ListAppThemeActionService
import com.athena.mis.application.actions.apptheme.ShowAppThemeActionService
import com.athena.mis.application.actions.apptheme.UpdateAppThemeActionService
import com.athena.mis.application.entity.AppTheme
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppThemeService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/28/2015.
 */

@TestFor(AppThemeController)

@Mock([
        AppTheme,
        AppThemeService,
        ShowAppThemeActionService,
        UpdateAppThemeActionService,
        ListAppThemeActionService,
        BaseService,
        AppSessionService
])

class AppThemeControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.updateAppThemeActionService.appSessionService.setAppUser(appUser)
        controller.listAppThemeActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Theme' (){
        when:
        request.method = "POST"
        controller.showTheme()

        then:
        view == '/application/appTheme/show'
    }

    def 'Update Theme'(){
        setup:

        AppTheme appTheme = new AppTheme(
                version : 0,
                key : "app.welcomeTitle",
                value : "Welcome to MIS",
                description : "Welcome title of the company",
                updatedOn  : new Date(),
                updatedBy : 1,
                companyId : 1,
                pluginId : 1
        )

        appTheme.id = 100
        appTheme.save(flush: true, failOnError: true, validate: false)

        controller.params.id = appTheme.id
        controller.params.version = 0
        controller.params.key = "app.welcomeTitle"
        controller.params.value = "Welcome to MIS"
        controller.params.description = "Welcome title of the company"
        controller.params.updatedOn  = new Date()
        controller.params.updatedBy = 1
        controller.params.companyId = 1
        controller.params.pluginId = 1

        when:
        request.method = 'POST'
        controller.updateTheme()

        then:
        response.redirectUrl == null
        response.json.message == "Theme Information has been updated successfully"
        response.json.isError == false
    }

    def 'List Theme' () {
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
        controller.listTheme()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
