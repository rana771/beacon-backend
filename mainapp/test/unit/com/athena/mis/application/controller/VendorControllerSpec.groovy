package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.vendor.CreateAppVendorActionService
import com.athena.mis.application.actions.vendor.ListAppVendorActionService
import com.athena.mis.application.actions.vendor.UpdateAppVendorActionService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.model.ListAppVendorActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppVendorActionServiceModelService
import com.athena.mis.application.service.VendorService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/14/2015.
 */

@TestFor(VendorController)

@Mock([
        Vendor,
        VendorService,
        AppSystemEntityCacheService,
        ListAppVendorActionServiceModelService,
        ListAppVendorActionServiceModel,
        CreateAppVendorActionService,
        UpdateAppVendorActionService,
        ListAppVendorActionService,
        AppDbInstance,
        AppDbInstanceService,
        SystemEntity,
        BaseService,
        AppSessionService
])

class VendorControllerSpec extends Specification {

    void setup() {
        AppUser appUser = new AppUser()
        appUser.companyId = 1
        appUser.id = 1

//        controller.showBankBranchActionService.appSessionService.setAppUser(appUser)
        controller.createAppVendorActionService.appSessionService.setAppUser(appUser)
        controller.updateAppVendorActionService.appSessionService.setAppUser(appUser)
//        controller.deleteAppVendorActionService.appSessionService.setAppUser(appUser)
        controller.listAppVendorActionService.appSessionService.setAppUser(appUser)

//        SystemEntity systemEntity = new SystemEntity()
//        systemEntity.key = "abc"
//        systemEntity.value = "abc"
//        systemEntity.type = 10
//        systemEntity.isActive = true
//        systemEntity.companyId = 1
//        systemEntity.reservedId = 1
//        systemEntity.pluginId = 1
//        systemEntity.createdBy = appUser.id
//        systemEntity.createdOn = new Date()
//        systemEntity.updatedBy = appUser.id
//        systemEntity.version = 1
//        systemEntity.id = 1
//        systemEntity.save()
//        List<SystemEntity> systemEntities = [systemEntity]
//
//        controller.createAppVendorActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createAppVendorActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, systemEntities)
    }

    def 'Show Vendor'() {
        setup:

        controller.params.oId = "TEST_ID"

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/vendor/show'
        model.oId == "TEST_ID"
    }

    def 'Create Vendor'() {
        setup:

        SystemEntity systemEntity = new SystemEntity()
        systemEntity.id = 1
        systemEntity.version = 0
        systemEntity.key = "Test Key"
        systemEntity.type = 1
        systemEntity.isActive = true
        systemEntity.pluginId = 1
        systemEntity.createdBy = 1
        systemEntity.createdOn = new Date()
        systemEntity.updatedOn = new Date()
        systemEntity.updatedBy = 1
        systemEntity.companyId = 1;

        systemEntity.reservedId = controller.createAppVendorActionService.appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES
        systemEntity.save()

        controller.createAppVendorActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createAppVendorActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR,[systemEntity])

        controller.params.id = "1"
        controller.params.version = "0"
        controller.params.name = "Test Vendor"
        controller.params.driver = "org.postgresql.Driver"
        controller.params.description = "This is for testing purpoe"
        controller.params.dbTypeId = "1"
        controller.params.vendorId = "1"
        controller.params.sequence = "1"
        controller.params.companyId = "1"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Vendor has been created successfully"
        response.json.isError == false
    }

    def 'Update Vendor' () {

        setup:

        Vendor vendor = new Vendor(
                id : 1,
                version : 0,
                name : "Test Vendor",
                driver :"org.postgresql.Driver",
                description : "This is for testing purpoe",
                dbTypeId : 1,
                vendorId : 1,
                sequence   : 1,
                companyId  : 1,
                createdBy  : 1,
                createdOn  : new Date(),
                updatedBy  : 1,
                updatedOn  : new Date()
        )

        vendor.id = 100
        vendor.save(flush: true, failOnError: true, validate: false)

        controller.params.id = vendor.id
        controller.params.version ="0"
        controller.params.name = "Test Vendor"
        controller.params.driver ="org.postgresql.Driver"
        controller.params.description = "This is for testing purpoe"
        controller.params.dbTypeId = "Source"
        controller.params.vendorId = "1"
        controller.params.sequence   = "1"
        controller.params.companyId  = "1"
        controller.params.createdBy  = "1"
        controller.params.createdOn  = new Date()
        controller.params.updatedBy  = "1"
        controller.params.updatedOn  = new Date()

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Vendor has been updated successfully"
        response.json.isError == false
    }

    def 'List Vendor' () {

        setup:

        Vendor vendor = new Vendor(
                id : 1,
                version : 0,
                name : "Test Vendor",
                driver :"org.postgresql.Driver",
                description : "This is for testing purpoe",
                dbTypeId : 1,
                vendorId : 1,
                sequence   : 1,
                companyId  : 1,
                createdBy  : 1,
                createdOn  : new Date(),
                updatedBy  : 1,
                updatedOn  : new Date()
        )

        vendor.save(flush: true, failOnError: true, validate: false)

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
