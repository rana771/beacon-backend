package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appmyfavourite.CreateAppMyFavouriteActionService
import com.athena.mis.application.actions.appmyfavourite.DeleteAppMyFavouriteActionService
import com.athena.mis.application.actions.appmyfavourite.ListAppMyFavouriteActionService
import com.athena.mis.application.actions.appmyfavourite.SelectAppMyFavouriteActionService
import com.athena.mis.application.actions.appmyfavourite.SetDefaultAppMyFavouriteActionService
import com.athena.mis.application.actions.appmyfavourite.SetDirtyAppMyFavouriteActionService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.RequestMap
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/22/2015.
 */

@TestFor(AppMyFavouriteController)

@Mock([
        CreateAppMyFavouriteActionService,
        AppMyFavourite,
        AppMyFavouriteService,
        RequestMap,
        RequestMapService,
        ListAppMyFavouriteActionService,
        SetDefaultAppMyFavouriteActionService,
        SelectAppMyFavouriteActionService,
        SetDirtyAppMyFavouriteActionService,
        BaseService,
        AppSessionService

])

class AppMyFavouriteControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createAppMyFavouriteActionService.appSessionService.setAppUser(appUser)
        controller.listAppMyFavouriteActionService.appSessionService.setAppUser(appUser)
        //controller.deleteAppGroupActionService.appSessionService.setAppUser(appUser)
    }

    def 'Create AppMyFavourite' () {

        setup:

        RequestMap rm = new RequestMap(
                version : 0,
                url : "/vehicle/show",
                configAttribute : "ROLE_-2,ROLE_RESELLER,ROLE_-12_1,ROLE_-3_1",
                featureName : "Application Module",
                pluginId : 1,
                transactionCode : "APP-92",
                isViewable : false,
                isCommon : false
        )
        rm.id = 1
        rm.save(flush: true)

        controller.params.id = 2
        controller.params.version = "0"
        controller.params.userId = 1
        controller.params.url = "vehicle/show"
        controller.params.featureName = "Show vehicle"
        controller.params.pluginId = 1
        controller.params.isDefault = false
        controller.params.companyId = 1
        controller.params.createdOn = new Date()
        controller.params.path = "vehicle/show"

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "This page has been added to 'My Favourite' list"
        response.json.isError == false
    }

      def 'List AppMyFavourite' () {
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
