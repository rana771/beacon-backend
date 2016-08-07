package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appserverinstance.CreateAppServerInstanceActionService
import com.athena.mis.application.actions.appserverinstance.DeleteAppServerInstanceActionService
import com.athena.mis.application.actions.appserverinstance.ListAppServerInstanceActionService
import com.athena.mis.application.actions.appserverinstance.TestServerInstanceConnectionActionService
import com.athena.mis.application.actions.appserverinstance.UpdateAppServerInstanceActionService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListAppServerInstanceActionServiceModel
import com.athena.mis.application.service.AppServerInstanceService
import com.athena.mis.application.service.ListAppServerInstanceActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/23/2015.
 */

@TestFor(AppServerInstanceController)

@Mock([
        AppServerInstance,
        AppServerInstanceService,
        ListAppServerInstanceActionServiceModelService,
        ListAppServerInstanceActionServiceModel,
        ListAppServerInstanceActionService,
        CreateAppServerInstanceActionService,
        UpdateAppServerInstanceActionService,
        DeleteAppServerInstanceActionService,
        TestServerInstanceConnectionActionService,
        BaseService,
        AppSessionService
])

class AppServerInstanceSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createAppServerInstanceActionService.appSessionService.setAppUser(appUser)
        controller.listAppServerInstanceActionService.appSessionService.setAppUser(appUser)
        controller.updateAppServerInstanceActionService.appSessionService.setAppUser(appUser)
        //controller.deleteAppServerInstanceActionService.appSessionService.setAppUser(appUser)
        controller.testServerInstanceConnectionActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show AppServerInstance' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/appServerInstance/show"
    }

    def 'Create AppServerInstance' () {

        setup:

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.name = "Test Name"
        controller.params.sshUserName = "root"              // source server login user
        controller.params.sshPassword = "123"             // source server login pwd
        controller.params.sshHost = "127.0.0.1"                 // source db server host name
        controller.params.sshPort = 22                    // source server ssh port
        controller.params.osVendorId = 1000000045
        controller.params.isTested = true               // true if connection test is successful; otherwise false
        controller.params.isNative = true               // if native then true; otherwise false
        controller.params.companyId = 1                 // Company.id
        controller.params.createdOn = new Date()                 // Object creation DateTime
        controller.params.createdBy = 1                 // AppUser.id
        controller.params.updatedBy = 1                 // AppUser.id

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Server instance has been successfully created"
        response.json.isError == false
    }

    def 'Update AppServerInstance' () {
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

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.name = "Test Name"
        controller.params.sshUserName = "root"              // source server login user
        controller.params.sshPassword = "123"             // source server login pwd
        controller.params.sshHost = "127.0.0.1"                 // source db server host name
        controller.params.sshPort = 22                    // source server ssh port
        controller.params.osVendorId = 1000000045
        controller.params.isTested = true               // true if connection test is successful; otherwise false
        controller.params.isNative = true               // if native then true; otherwise false
        controller.params.companyId = 1                 // Company.id
        controller.params.createdOn = new Date()                 // Object creation DateTime
        controller.params.createdBy = 1                 // AppUser.id
        controller.params.updatedBy = 1

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Server instance has been successfully updated"
        response.json.isError == false
    }

    def 'List AppServerInstance' () {
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
}
