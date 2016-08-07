package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.sysconfiguration.UpdateSysConfigurationActionService
import com.athena.mis.application.actions.testData.AppCreateTestDataActionService
//import com.athena.mis.application.actions.testData.AppDeleteTestDataActionService
import com.athena.mis.application.actions.testData.ListTestDataActionService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppGroupService
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.AppCustomerService
import com.athena.mis.application.service.AppDesignationService
import com.athena.mis.application.service.AppEmployeeService
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ProjectService
import com.athena.mis.application.service.AppSmsService
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.service.TestDataModelService
import com.athena.mis.application.service.VehicleService
import com.athena.mis.application.session.AppSessionService
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/2/2015.
 */


@TestFor(TestDataController)

@Mock([
        AppUser,
        AppDbInstance,
        SystemEntity,
        ListTestDataActionService,
        AppDbInstanceService,
        AppSysConfigCacheService,
        AppSystemEntityCacheService,
        DataAdapterFactoryService,
        AppCreateTestDataActionService,
        SpringSecurityService,
        AppUserService,
        TestDataModelService,
        VehicleService,
        ProjectService,
        SupplierService,
        SystemEntityService,
        SupplierItemService,
        ItemService,
        ItemTypeService,
        AppCustomerService,
        AppDesignationService,
        AppEmployeeService,
        AppGroupService,
        AppMailService,
        CompanyService,
        AppSmsService,
        SysConfiguration,
        SysConfigurationService,
        AppConfigurationService,
        BaseService,
        AppSessionService

])

class TestDataControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1
        appUser.companyId = 1

        controller.appCreateTestDataActionService.appSessionService.setAppUser(appUser)
//        controller.appDeleteTestDataActionService.appSessionService.setAppUser(appUser)
        controller.listTestDataActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show TestData' () {

        setup:
        controller.params.oId = "TEST_ID"
        controller.params.url = "TEST_URL"

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/testData/show'
        model.oId == "TEST_ID"
        model.url == "TEST_URL"
    }
    def 'Create TestData' () {

        setup:
        SysConfiguration sc = new SysConfiguration(
                version : 0,
                key : "mis.application.deploymentMode",
                value : "2",
                description : "Test Config",
                transactionCode : "GetSysConfigLoginTemplateActionService, AppCreateTestDataActionService, AppDeleteTestDataActionService, AppMailService, AppSmsService, LoginController",
                message : "This is for Test",
                pluginId : 1,
                companyId : 1,
                updatedBy : 1,
                updatedOn : new Date()
        )
        sc.id = 8

        sc.save(flush: true, failOnError: true, validate: false)

//        controller.appCreateTestDataActionService.appConfigurationService.appSysConfigCacheService.lstMain
//        SysConfigurationService sysConfigurationService = new SysConfigurationService()
//        AppConfigurationService appConfigurationService = new AppConfigurationService()
//        AppSysConfigCacheService appSysConfigCacheService = new AppSysConfigCacheService()
//        appSysConfigCacheService.sysConfigurationService = sysConfigurationService
//        appConfigurationService.appSysConfigCacheService = appSysConfigCacheService
//        appConfigurationService.appSysConfigCacheService.init()

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,
                name : "mis_masud",
                vendorId : 1,
                typeId : 1,
                reservedVendorId : 1,
                driver : "org.postgresql.Driver",
                companyId  : 1,
                createdOn : new Date(),
                createdBy : 1,
                updatedBy : 1,
                isNative  : true,
                url  : "jdbc:postgresql://localhost:5432/mis_masud",
                dbName  : "mis_masud",
                userName : "postgres",
                password : "postgres",
                port : "5432",
                connectionString : "jdbc:postgresql://localhost:5432/mis_masud?user=postgres&password=postgres",
                isTested : true,
                isSlave  : false,
                isReadOnly : false,
                isDeletable : true
        )
        appDbInstance.id = 1
        appDbInstance.save(flush: true, failOnError: true, validate: false)

        AppUser appUser = new AppUser(
                version : 0,
                loginId : "masud@athena.com.bd",
                username : "Abdul Karim Mondol",
                password : "123456",
                enabled  : true,
                hasSignature : false,
                companyId : 1,
                employeeId : 1,
                accountExpired : false,
                accountLocked  : false,
                passwordExpired : false,
                isCompanyUser   :  false,
                nextExpireDate  : new Date(),
                activationLink  : "Test Link",
                isActivatedByMail : false,
                passwordResetLink  : "Test Link",
                isPowerUser  : true,
                isSystemUser : false,
                isDisablePasswordExpiration : false,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                isReseller : false
        )
        appUser.id = 1
        appUser.save(flush: true, failOnError: true, validate: false)

        controller.params.dbInstanceId = appDbInstance.id

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Test data has been loaded successfully"
        response.json.isError == false
    }

    def 'List TestData' () {

        setup:

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,                                               // Entity version in the persistence layer
                name : "mis_masud",                                    // Unique name of the DB Instance within company
                vendorId : 1,                                          // Vendor.id
                typeId : 1,                                            // SystemEntity.id (Source Instance or Target Instance)
                reservedVendorId : 1143,                                  // ReservedSystemEntity.id
                driver : "org.postgresql.Driver",                      // Driver of DB
                companyId  : 1,                                         // Company.id
                createdOn : new Date(),                                // Object Created Datetime
                createdBy : 1,                                         // AppUser.id
                updatedBy : 1,                                         // AppUser.id
                isNative  : true,                                      // Access to native database through jdbc
                url  : "jdbc:postgresql://localhost:5432/mis_masud",   // url of DB
                dbName  : "mis_masud",                                 // database name
                userName : "postgres",                                 // user name
                password : "postgres",                                // Can be blank
                port : "5432",                                         // port of DB
                connectionString : "jdbc:postgresql://localhost:5432/mis_masud?user=postgres&password=postgres", // complete connection string ie (url:port/dbName?user=userName&password=password)
                isTested : true,                                      // true if connection test is successful; otherwise false
                isSlave  : false,                                      // true for slave DB; otherwise false
                isReadOnly : false,                                   // if true then only read operation is executable; if false then all read/write operation is executable
                isDeletable : true
        )
        appDbInstance.id = 1
        appDbInstance.save(flush: true, failOnError: true, validate: false)

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

        systemEntity.reservedId = controller.listTestDataActionService.appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES
        systemEntity.save()

        controller.listTestDataActionService.appSystemEntityCacheService.sysEntityMap.put(controller.listTestDataActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR,[systemEntity])

        systemEntity = new SystemEntity()
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

        systemEntity.reservedId = controller.listTestDataActionService.appSystemEntityCacheService.SYS_ENTITY_DB_OBJECT_TABLE
        systemEntity.save()

        controller.listTestDataActionService.appSystemEntityCacheService.sysEntityMap.put(controller.listTestDataActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_OBJECT,[systemEntity])

        controller.params.dbInstanceId = appDbInstance.id
        controller.params.typeId = appDbInstance.typeId
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'caption'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params. entityTypeId

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
