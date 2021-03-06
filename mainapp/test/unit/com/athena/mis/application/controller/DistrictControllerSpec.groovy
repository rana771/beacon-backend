package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.district.CreateDistrictActionService
import com.athena.mis.application.actions.district.ListDistrictActionService
import com.athena.mis.application.actions.district.UpdateDistrictActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.District
import com.athena.mis.application.model.ListDistrictActionServiceModel
import com.athena.mis.application.service.DistrictService
import com.athena.mis.application.service.ListDistrictActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/2/2015.
 */

@TestFor(DistrictController)
@Mock([

        District,
        DistrictService,
        ListDistrictActionServiceModelService,
        ListDistrictActionServiceModel,
        ListDistrictActionService,
        CreateDistrictActionService,
        UpdateDistrictActionService,
        BaseService, AppSessionService
])

class DistrictControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createDistrictActionService.appSessionService.setAppUser(appUser)
        controller.updateDistrictActionService.appSessionService.setAppUser(appUser)
//        controller.deleteBankBranchActionService.appSessionService.setAppUser(appUser)
        controller.listDistrictActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show District'() {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/district/show'
    }

    def 'Create District' () {

        setup:
        controller.params.id = "1"                  // primary key (Auto generated by its own sequence)
        controller.params.version = "1"             // entity version in the persistence layer
        controller.params.name = "MyDistrict"       // Unique name within a company
        controller.params.companyId = "1"           // Company.id
        controller.params.isGlobal = false          // flag for global District (for ARMS plugin)
        controller.params.countryId = "1"           // Country.id
        controller.params.createdBy = "1"           // AppUser.id
        controller.params.createdOn = new Date()    // Object creation DateTime
        controller.params.updatedBy = "1"           // AppUser.id
        controller.params.updatedOn = new Date()    // Object Updated DateTime

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "District has been successfully saved"
        response.json.isError == false
    }

    def 'Update District' () {
        setup:
        District district = new District(

                version : 0,             // entity version in the persistence layer
                name : "MyDistrict",       // Unique name within a company
                companyId : 1,           // Company.id
                isGlobal : false,          // flag for global District (for ARMS plugin)
                countryId : 1,           // Country.id
                createdBy : 1,           // AppUser.id
                createdOn : new Date() ,   // Object creation DateTime
                updatedBy : 1,           // AppUser.id
                updatedOn : new Date()    // Ob
        )

        district.id = 100
        district.save(flush: true, failOnError: true, validate: false)

        controller.params.id = district.id                  // primary key (Auto generated by its own sequence)
        controller.params.version = "0"             // entity version in the persistence layer
        controller.params.name = "MyDistrict"       // Unique name within a company
        controller.params.companyId = "1"           // Company.id
        controller.params.isGlobal = false          // flag for global District (for ARMS plugin)
        controller.params.countryId = "1"           // Country.id
        controller.params.createdBy = "1"           // AppUser.id
        controller.params.createdOn = new Date()    // Object creation DateTime
        controller.params.updatedBy = "1"           // AppUser.id
        controller.params.updatedOn = new Date()    // Object Updated DateTime

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == controller.updateDistrictActionService.UPDATE_SUCCESS_MSG
        response.json.isError == false
    }

    def 'List District'() {
        setup:
        District district = new District(

                version : 1,             // entity version in the persistence layer
                name : "MyDistrict",       // Unique name within a company
                companyId : 1,           // Company.id
                isGlobal : false,          // flag for global District (for ARMS plugin)
                countryId : 1,           // Country.id
                createdBy : 1,           // AppUser.id
                createdOn : new Date() ,   // Object creation DateTime
                updatedBy : 1,           // AppUser.id
                updatedOn : new Date()    // Ob
        )

        district.id = 100
        district.save(flush: true, failOnError: true, validate: false)

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
