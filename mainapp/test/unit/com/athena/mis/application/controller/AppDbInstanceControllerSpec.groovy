package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.dbinstance.ConnectAppDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.CreateAppDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.CreateAppDbInstanceForDplDashboardActionService
import com.athena.mis.application.actions.dbinstance.DeleteAppDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.DisconnectAppDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.ListAppDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.ListAppDbInstanceForDashboardActionService
import com.athena.mis.application.actions.dbinstance.ListDbForDplDashboardActionService
import com.athena.mis.application.actions.dbinstance.ListResultForDplSqlConsoleActionService
import com.athena.mis.application.actions.dbinstance.ListTableForDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.ListVendorForDplDashboardActionService
import com.athena.mis.application.actions.dbinstance.ReconnectAppDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.ShowResultForDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.TestDbConnectionForDashboardActionService
import com.athena.mis.application.actions.dbinstance.TestDbInstanceConnectionActionService
import com.athena.mis.application.actions.dbinstance.UpdateAppDbInstanceActionService
import com.athena.mis.application.actions.dbinstance.UpdateAppDbInstanceForDplDashboardActionService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.model.ListAppDbInstanceActionServiceModel
import com.athena.mis.application.model.ListVendorForDplDashboardActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppDbInstanceActionServiceModelService
import com.athena.mis.application.service.VendorService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/22/2015.
 */

@TestFor(AppDbInstanceController)

@Mock([
        AppDbInstance,
        Vendor,
        VendorService,
        SystemEntity,
        CreateAppDbInstanceActionService,
        UpdateAppDbInstanceActionService,
        DeleteAppDbInstanceActionService,
        ListAppDbInstanceActionService,
        ShowResultForDbInstanceActionService,
        TestDbInstanceConnectionActionService,
        ListTableForDbInstanceActionService,
        ListAppDbInstanceForDashboardActionService,
        ListVendorForDplDashboardActionService,
        ListAppDbInstanceActionServiceModel,
        ListDbForDplDashboardActionService,
        CreateAppDbInstanceForDplDashboardActionService,
        UpdateAppDbInstanceForDplDashboardActionService,
        TestDbConnectionForDashboardActionService,
        ListResultForDplSqlConsoleActionService,
        ConnectAppDbInstanceActionService,
        DisconnectAppDbInstanceActionService,
        ReconnectAppDbInstanceActionService,
        AppSystemEntityCacheService,
        AppDbInstanceService,
        ListAppDbInstanceActionServiceModelService,
        ListAppDbInstanceActionServiceModel,
        ListVendorForDplDashboardActionServiceModel,
        DataAdapterFactoryService,
        BaseService,
        AppSessionService

])

class AppDbInstanceControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.companyId = 1
        appUser.id = 1

        controller.createAppDbInstanceActionService.appSessionService.setAppUser(appUser)
        controller.listAppDbInstanceActionService.appSessionService.setAppUser(appUser)
        controller.updateAppDbInstanceActionService.appSessionService.setAppUser(appUser)
        //controller.deleteAppGroupActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show AppDbInstance' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appDbInstance/show'
    }

    def 'Show ForDplDashboard'(){

        when:
        request.method = "POST"
        controller.showForDplDashboard()

        then:
        view == '/dataPipeLine/dplDashboard/show'
    }

    def 'Show ForSqlConsole'(){

        setup:
        controller.params.oId = "TEST_ID"

        when:
        request.method = "POST"
        controller.showForSqlConsole()

        then:
        model.oId == "TEST_ID"
        view == '/dataPipeLine/dplSqlConsole/show'
    }

    def 'Create AppDbInstance' () {

        setup:

        Vendor vendor = new Vendor(

                version : 0,
                name : "Test Vendor",
                driver :"org.postgresql.Driver",
                description : "This is for testing purpoe",
                dbTypeId : 1,
                vendorId : 1,
                sequence   : 1,
                companyId  : 500,
                createdBy  : 1,
                createdOn  : new Date(),
                updatedBy  : 1,
                updatedOn  : new Date()
        )
        vendor.id = 100
        vendor.save(flush: true, failOnError: true, validate: false)

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
        systemEntity.companyId = 500;

        systemEntity.reservedId=controller.createAppDbInstanceActionService.appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES
        systemEntity.save()

        controller.createAppDbInstanceActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createAppDbInstanceActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR,[systemEntity])

        controller.params.id = 1
        controller.params.version
        controller.params.name = "mis_masud"
        controller.params.vendorId = vendor.id
        controller.params.typeId = 1
        controller.params.reservedVendorId = 1
        controller.params.driver = "org.postgresql.Driver"
        controller.params.companyId = 1
        controller.params.createdOn = new Date()
        controller.params.createdBy = 1
        controller.params.updatedBy = 1
        controller.params.isNative  = true
        controller.params.url  = "jdbc:postgresql://localhost:5432/mis_masud"
        controller.params.dbName  = "mis_masud"
        controller.params.userName = "postgres"
        controller.params.password = "postgres"
        controller.params.port = "5432"
        controller.params.connectionString = "jdbc:postgresql://localhost:5432/mis_masud?user=postgres&password=postgres"
        controller.params.isTested = true
        controller.params.isSlave  = false
        controller.params.isReadOnly = false
        controller.params.isDeletable = true

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == 'DB Instance has been successfully saved'
        response.json.isError == false
    }

    def 'Create DbInstanceForDplDashboard'(){

        setup:
        Vendor vendor = new Vendor(

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

        systemEntity.reservedId = controller.createAppDbInstanceForDplDashboardActionService.appSystemEntityCacheService.SYS_ENTITY_DB_INSTANCE_SOURCE
        systemEntity.save()

        controller.createAppDbInstanceForDplDashboardActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createAppDbInstanceForDplDashboardActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE,[systemEntity])

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

        systemEntity.reservedId = controller.createAppDbInstanceForDplDashboardActionService.appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES
        systemEntity.save()

        controller.createAppDbInstanceForDplDashboardActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createAppDbInstanceForDplDashboardActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR,[systemEntity])

        controller.params.name = "mis_masud"
        controller.params.vendorId = vendor.id
        controller.params.url  = "jdbc:postgresql://localhost:5432/mis_masud"
        controller.params.vendorId = vendor.id
        controller.params.port = "5432"
        controller.params.dbName  = "mis_masud"
        controller.params.userName = "postgres"

        when:
        request.method = 'POST'
        controller.createDbInstanceForDplDashboard()

        then:
        response.redirectedUrl == null
        response.json.message == 'DB Instance has been successfully saved'
        response.json.isError == false
    }

    def 'Update AppDbInstance' (){

        setup:

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,
                name : "mis_masud",
                vendorId : 1,
                typeId : 1,
                reservedVendorId : 1143,
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

        Vendor vendor = new Vendor(
                version : 0,
                name : "Test Vendor",
                driver :"org.postgresql.Driver",
                description : "This is for testing purpoe",
                dbTypeId : 1,
                vendorId : 1,
                sequence   : 1,
                companyId  : 500,
                createdBy  : 1,
                createdOn  : new Date(),
                updatedBy  : 1,
                updatedOn  : new Date()
        )
        vendor.id = 100
        vendor.save(flush: true, failOnError: true, validate: false)

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
        systemEntity.companyId = 500;

        systemEntity.reservedId = controller.updateAppDbInstanceActionService.appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES
        systemEntity.save()

        controller.updateAppDbInstanceActionService.appSystemEntityCacheService.sysEntityMap.put(controller.updateAppDbInstanceActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR,[systemEntity])

        controller.params.id = 1
        controller.params.version  = "0"
        controller.params.name = "mis_masud"
        controller.params.vendorId = vendor.id
        controller.params.typeId = 1
        controller.params.reservedVendorId = 1
        controller.params.driver = "org.postgresql.Driver"
        controller.params.companyId  =1
        controller.params.createdOn = new Date()
        controller.params.createdBy = 1
        controller.params.updatedBy = 1
        controller.params.isNative  = true
        controller.params.url  = "jdbc:postgresql://localhost:5432/mis_masud"
        controller.params.dbName  = "mis_masud"
        controller.params.userName = "postgres"
        controller.params.password = "postgres"
        controller.params.port = "5432"
        controller.params.connectionString = "jdbc:postgresql://localhost:5432/mis_masud?user=postgres&password=postgres"
        controller.params.isTested = true
        controller.params.isSlave  = false
        controller.params.isReadOnly = false
        controller.params.isDeletable = true

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == 'DB Instance has been successfully updated.'
        response.json.isError == false
    }

    def 'Update DbInstanceForDplDashboard'(){

        setup:

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,
                name : "mis_masud",
                vendorId : 1,
                typeId : 1,
                reservedVendorId : 1143,
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

        controller.params.id = 1
        controller.params.version  = "0"
        controller.params.name = "mis_masud"
        controller.params.vendorId = 1
        controller.params.typeId = 1
        controller.params.reservedVendorId = 1
        controller.params.driver = "org.postgresql.Driver"
        controller.params.companyId  =1
        controller.params.createdOn = new Date()
        controller.params.createdBy = 1
        controller.params.updatedBy = 1
        controller.params.isNative  = true
        controller.params.url  = "jdbc:postgresql://localhost:5432/mis_masud"
        controller.params.dbName  = "mis_masud"
        controller.params.userName = "postgres"
        controller.params.password = "postgres"
        controller.params.port = "5432"
        controller.params.connectionString = "jdbc:postgresql://localhost:5432/mis_masud?user=postgres&password=postgres"
        controller.params.isTested = true
        controller.params.isSlave  = false
        controller.params.isReadOnly = false
        controller.params.isDeletable = true

        when:
        request.method = 'POST'
        controller.updateDbInstanceForDplDashboard()

        then:
        response.redirectUrl == null
        response.json.message == "DB Instance updated successfully"
        response.json.isError == false
    }

    def 'List AppDbInstance' () {
        setup:

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

    def 'List ForDashboard'(){
        setup:

        controller.params.vendorId = 1
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listForDashboard()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List DBTable'(){
        setup:

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,
                name : "mis_masud",
                vendorId : 1,
                typeId : 1,
                reservedVendorId : 1143,
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

        controller.params.id = appDbInstance.id
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listDbTable()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List VendorForDplDashboard'(){

        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listVendorForDplDashboard()

        then:
        response.json.isError == false
//        response.json.count >= 0
    }

    def 'List DbForDplDashboard'(){
        setup:

        controller.params.vendorId = 1
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listDbForDplDashboard()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List ResultForSqlConsole'(){

        setup:

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,
                name : "mis_masud",
                vendorId : 1,
                typeId : 1,
                reservedVendorId : 1143,
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

        controller.params.dbInstanceId = appDbInstance.id
        controller.params.script = "SELECT * FROM app_db_instance"
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listResultForSqlConsole()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
