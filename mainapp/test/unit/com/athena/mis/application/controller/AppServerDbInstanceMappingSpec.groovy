package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appserverdbinstancemapping.CreateAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.DeleteAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.ListAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.ShowAppServerDbInstanceMappingActionService
import com.athena.mis.application.actions.appserverdbinstancemapping.UpdateAppServerDbInstanceMappingActionService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppServerDbInstanceMapping
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListAppServerDbInstanceMappingActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppServerDbInstanceMappingService
import com.athena.mis.application.service.ListAppServerDbInstanceMappingActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/23/2015.
 */

@TestFor(AppServerDbInstanceMappingController)

@Mock([
        AppDbInstance,
        AppServerDbInstanceMapping,
        AppDbInstanceService,
        AppServerInstance,
        AppServerDbInstanceMappingService,
        ListAppServerDbInstanceMappingActionServiceModelService,
        ListAppServerDbInstanceMappingActionServiceModel,
        ShowAppServerDbInstanceMappingActionService,
        ListAppServerDbInstanceMappingActionService,
        CreateAppServerDbInstanceMappingActionService,
        UpdateAppServerDbInstanceMappingActionService,
        DeleteAppServerDbInstanceMappingActionService,
        BaseService,
        AppSessionService
])

class AppServerDbInstanceMappingSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.companyId = 1
        appUser.id = 1

        controller.showAppServerDbInstanceMappingActionService.appSessionService.setAppUser(appUser)
        controller.listAppServerDbInstanceMappingActionService.appSessionService.setAppUser(appUser)
        controller.createAppServerDbInstanceMappingActionService.appSessionService.setAppUser(appUser)
        controller.updateAppServerDbInstanceMappingActionService.appSessionService.setAppUser(appUser)
        controller.deleteAppServerDbInstanceMappingActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show AppServerDbInstanceMapping' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/appServerDbInstanceMapping/show"
    }

    def 'Create AppServerDbInstanceMapping' () {

        setup:

        AppServerInstance appServerInstance = new AppServerInstance(
                version : 0,
                name : "Test Name",
                sshUserName : "root",              // source server login user
                sshPassword : "123",             // source server login pwd
                sshHost : "127.0.0.1",                 // source db server host name
                sshPort : 22,                    // source server ssh port
                osVendorId : 1000000045,
                isTested : true,               // true if connection test is successful; otherwise false
                isNative : true ,              // if native then true; otherwise false
                companyId : 1,                 // Company.id
                createdOn : new Date(),                 // Object creation DateTime
                createdBy : 1,                 // AppUser.id
                updatedBy : 1
        )

        appServerInstance.id = 1
        appServerInstance.save(flush: true, failOnError: true, validate: false)

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,                                               // Entity version in the persistence layer
                name : "mis_masud",                                    // Unique name of the DB Instance within company
                vendorId : 1,                                          // Vendor.id
                typeId : 1,                                            // SystemEntity.id (Source Instance or Target Instance)
                reservedVendorId : 1,                                  // ReservedSystemEntity.id
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

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.appServerInstanceId = appServerInstance.id
        controller.params.appDbInstanceId = appDbInstance.id
        controller.params.dbVendorId = 1
        controller.params.companyId = 1                 // Company.id
        controller.params.createdOn = new Date()                 // Object creation DateTime
        controller.params.createdBy = 1                 // AppUser.id
        controller.params.updatedOn = new Date()                 // Object Updated DateTime
        controller.params.updatedBy = 1

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == 'Server Db Instance Mapping has been successfully saved'
        response.json.isError == false
    }

    def 'Update AppServerDbInstanceMapping'(){
        setup:

        AppServerDbInstanceMapping appServerDbInstanceMapping = new AppServerDbInstanceMapping(
                version : 0,
                appServerInstanceId : 1,
                appDbInstanceId : 1,
                dbVendorId : 1,
                companyId : 1,                 // Company.id
                createdOn : new Date(),                 // Object creation DateTime
                createdBy : 1,                 // AppUser.id
                updatedOn : new Date(),                 // Object Updated DateTime
                updatedBy : 1
        )

        appServerDbInstanceMapping.id = 1
        appServerDbInstanceMapping.save(flush: true, failOnError: true, validate: false)

        AppDbInstance appDbInstance = new AppDbInstance(

                version : 0,                                               // Entity version in the persistence layer
                name : "mis_masud",                                    // Unique name of the DB Instance within company
                vendorId : 1,                                          // Vendor.id
                typeId : 1,                                            // SystemEntity.id (Source Instance or Target Instance)
                reservedVendorId : 1,                                  // ReservedSystemEntity.id
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


        controller.params.id = 1
        controller.params.version = "0"
        controller.params.appServerInstanceId = appServerDbInstanceMapping.id
        controller.params.appDbInstanceId = appDbInstance.id
        controller.params.dbVendorId = appDbInstance.vendorId
        controller.params.companyId = 1                 // Company.id
        controller.params.createdOn = new Date()                 // Object creation DateTime
        controller.params.createdBy = 1                 // AppUser.id
        controller.params.updatedOn = new Date()                 // Object Updated DateTime
        controller.params.updatedBy = 1

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == 'Server Db Instance Mapping has been successfully update'
        response.json.isError == false
    }

    def 'List AppServerDbInstanceMapping' () {
        setup:

        AppServerInstance appServerInstance = new AppServerInstance(
                version : 0,
                name : "Test Name",
                sshUserName : "root",              // source server login user
                sshPassword : "123",             // source server login pwd
                sshHost : "127.0.0.1",                 // source db server host name
                sshPort : 22,                    // source server ssh port
                osVendorId : 1000000045,
                isTested : true,               // true if connection test is successful; otherwise false
                isNative : true ,              // if native then true; otherwise false
                companyId : 1,                 // Company.id
                createdOn : new Date(),                 // Object creation DateTime
                createdBy : 1,                 // AppUser.id
                updatedBy : 1
        )

        appServerInstance.id = 1
        appServerInstance.save(flush: true, failOnError: true, validate: false)


        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.appServerInstanceId = appServerInstance.id

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
