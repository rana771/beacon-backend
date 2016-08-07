package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.dbinstancequery.CreateDbInstanceQueryActionService
import com.athena.mis.application.actions.dbinstancequery.DeleteDbInstanceQueryActionService
import com.athena.mis.application.actions.dbinstancequery.DownloadCsvDbInstanceQueryActionService
import com.athena.mis.application.actions.dbinstancequery.DownloadDbInstanceQueryActionService
import com.athena.mis.application.actions.dbinstancequery.ExecuteDbInstanceQueryActionService
import com.athena.mis.application.actions.dbinstancequery.ListDbInstanceQueryActionService
import com.athena.mis.application.actions.dbinstancequery.ListDbInstanceQueryResultActionService
import com.athena.mis.application.actions.dbinstancequery.ShowDbInstanceQueryActionService
import com.athena.mis.application.actions.dbinstancequery.ShowDbInstanceQueryResultActionService
import com.athena.mis.application.actions.dbinstancequery.UpdateDbInstanceQueryActionService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.DbInstanceQueryService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/23/2015.
 */

@TestFor(DbInstanceQueryController)

@Mock([
        DbInstanceQuery,
        DbInstanceQueryService,
        AppDbInstance,
        AppDbInstanceService,
        DataAdapterFactoryService,
        ShowDbInstanceQueryActionService,
        ListDbInstanceQueryActionService,
        CreateDbInstanceQueryActionService,
        UpdateDbInstanceQueryActionService,
        DeleteDbInstanceQueryActionService,
        DownloadCsvDbInstanceQueryActionService,
        DownloadDbInstanceQueryActionService,
        ListDbInstanceQueryResultActionService,
        ShowDbInstanceQueryResultActionService,
        ExecuteDbInstanceQueryActionService,
        BaseService,
        AppSessionService
])

class DbInstanceQueryControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.showDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
        controller.listDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
        controller.createDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
        controller.updateDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
        controller.deleteDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
        controller.downloadCsvDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
        controller.downloadDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
        controller.listDbInstanceQueryResultActionService.appSessionService.setAppUser(appUser)
        controller.showDbInstanceQueryResultActionService.appSessionService.setAppUser(appUser)
        controller.executeDbInstanceQueryActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show DbInstanceQuery' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/dbInstanceQuery/show'
    }

    def 'Show QueryResult'(){
        when:
        request.method = "POST"
        controller.showQueryResult()

        then:
        view == '/application/dbInstanceQuery/showDbInstanceQueryResult'
    }

    def 'Create DbInstanceQuery' () {

        setup:

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

        controller.params.id = 1                   // primary key (Auto generated by its own sequence)
        controller.params.version = "0"                // entity version in the persistence layer
        controller.params.name = "Test Query"                // Unique name within DocOfflineDataFeed
        controller.params.sqlQuery = "SELECT * FROM app_db_instance"            // any sql e.g. (select * from table_name)
        controller.params.dbInstanceId = appDbInstance.id         // DbInstance.id
        controller.params.companyId = 1             // Company.id
        controller.params.createdOn = new Date()             // Object creation DateTime
        controller.params.createdBy = 1             // AppUser.id
        controller.params.updatedBy = 1             // AppUser.id
        controller.params.resultPerPage = 10           // result per page
        controller.params.numberOfExecution = 0      // count of execution
        controller.params.isReserved = false         // if reserved query then true; otherwise false
        controller.params.queryTypeId = 1           // Sys

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == 'Query has been successfully saved'
        response.json.isError == false
    }

    def 'Update DbInstanceQuery'(){

        setup:

        DbInstanceQuery dbInstanceQuery = new DbInstanceQuery(
                version : 0,                // entity version in the persistence layer
                name : "Test Query",                // Unique name within DocOfflineDataFeed
                sqlQuery : "SELECT * FROM app_db_instance",           // any sql e.g. (select * from table_name)
                dbInstanceId : 1,          // DbInstance.id
                companyId : 1,             // Company.id
                createdOn : new Date(),             // Object creation DateTime
                createdBy : 1,             // AppUser.id
                updatedBy : 1,             // AppUser.id
                resultPerPage : 10,           // result per page
                numberOfExecution : 0,      // count of execution
                isReserved : false ,        // if reserved query then true; otherwise false
                queryTypeId : 0
        )

        dbInstanceQuery.id = 1
        dbInstanceQuery.save(flush: true, failOnError: true, validate: false)

        controller.params.id = 1                   // primary key (Auto generated by its own sequence)
        controller.params.version = "0"                // entity version in the persistence layer
        controller.params.name = "Test Query"                // Unique name within DocOfflineDataFeed
        controller.params.sqlQuery = "SELECT * FROM app_db_instance"            // any sql e.g. (select * from table_name)
        controller.params.dbInstanceId = 1          // DbInstance.id
        controller.params.companyId = 1             // Company.id
        controller.params.createdOn = new Date()             // Object creation DateTime
        controller.params.createdBy = 1             // AppUser.id
        controller.params.updatedBy = 1             // AppUser.id
        controller.params.resultPerPage = 10           // result per page
        controller.params.numberOfExecution = 0      // count of execution
        controller.params.isReserved = false         // if reserved query then true; otherwise false
        controller.params.queryTypeId = 0

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Query has been successfully updated"
        response.json.isError == false
    }

    def 'List DbInstanceQuery' () {
        setup:

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

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.dbInstanceId = appDbInstance.id

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List QueryResult'(){

        setup:

        DbInstanceQuery dbInstanceQuery = new DbInstanceQuery(
                version : 0,                // entity version in the persistence layer
                name : "Test Query",                // Unique name within DocOfflineDataFeed
                sqlQuery : "SELECT * FROM app_db_instance",           // any sql e.g. (select * from table_name)
                dbInstanceId : 1,          // DbInstance.id
                companyId : 1,             // Company.id
                createdOn : new Date(),             // Object creation DateTime
                createdBy : 1,             // AppUser.id
                updatedBy : 1,             // AppUser.id
                resultPerPage : 10,           // result per page
                numberOfExecution : 0,      // count of execution
                isReserved : false ,        // if reserved query then true; otherwise false
                queryTypeId : 0
        )

        dbInstanceQuery.id = 1
        dbInstanceQuery.save(flush: true, failOnError: true, validate: false)

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

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.dbInstanceQueryId = dbInstanceQuery.id

        when:
        request.method = 'POST'
        controller.listQueryResult()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
