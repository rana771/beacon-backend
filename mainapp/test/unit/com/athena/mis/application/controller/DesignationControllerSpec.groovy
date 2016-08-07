package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appdesignation.CreateAppDesignationActionService
import com.athena.mis.application.actions.appdesignation.ListAppDesignationActionService
import com.athena.mis.application.actions.appdesignation.UpdateAppDesignationActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppDesignation
import com.athena.mis.application.service.AppDesignationService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/1/2015.
 */

@TestFor(AppDesignationController)

@Mock([
        AppDesignation,
        AppDesignationService,
        CreateAppDesignationActionService,
        UpdateAppDesignationActionService,
        ListAppDesignationActionService,
        BaseService,
        AppSessionService])

class DesignationControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createAppDesignationActionService.appSessionService.setAppUser(appUser)
        controller.updateAppDesignationActionService.appSessionService.setAppUser(appUser)
        controller.listAppDesignationActionService.appSessionService.setAppUser(appUser)
        //controller.deleteDesignationActionService.appSessionService.setAppUser(appUser)
    }

    def 'Test Show Action' () {
        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appDesignation/show'
    }


    def 'Test Create Action' () {
        setup:

        controller.params.id = "1"
        controller.params.version = "0"
        controller.params.companyId = "1"
        controller.params.name = "MANAGER"
        controller.params.shortName = "MNG"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()

        when:
        request.method = "POST"
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Designation has been successfully saved"
        response.json.isError == false
    }

    def 'Test Update Action' () {

        setup:
        AppDesignation desg = new AppDesignation(
                version : 0,
                companyId : 1,
                name : "MANAGER",
                shortName : "MNG",
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )

        desg.id = 45
        desg.save()

        controller.params.id = "45"
        controller.params.version = "0"
        controller.params.companyId = "1"
        controller.params.name = "MANAGER"
        controller.params.shortName = "MNG"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Designation has been updated successfully"
        response.json.isError == false
    }

    def 'Test List Action' () {
        setup:
        AppDesignation desg = new AppDesignation(
                version : 0,
                companyId : 1,
                name : "MANAGER",
                shortName : "MNG",
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )

        desg.save()

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
}
