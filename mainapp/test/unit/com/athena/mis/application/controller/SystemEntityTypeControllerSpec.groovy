package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.systementitytype.ListSystemEntityTypeActionService
import com.athena.mis.application.actions.systementitytype.ShowSystemEntityTypeActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.model.ListSystemEntityTypeActionServiceModel
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/7/2015.
 */


@TestFor(SystemEntityTypeController)

@Mock([
        SystemEntityType,
        ShowSystemEntityTypeActionService,
        ListSystemEntityTypeActionService,
        ListSystemEntityTypeActionServiceModel,
        BaseService,
        AppSessionService
])

class SystemEntityTypeControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.listSystemEntityTypeActionService.appSessionService.setAppUser(appUser)
    }

    def 'Test Show System Entity Type' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/systemEntityType/show'
    }
/*
    long id             // primary key (pluginId + sequence ex: pluginId = 1, sequence = 1, id = 11)
    long version        // entity version in the persistence layer
    String name         // name of SystemEntityType
    String description  // description of SystemEntityType
    long pluginId       // id of plugin (e.g. 1 for Application, 3 for Budget etc.)

    static constraints = {
        name(nullable: false)
        description(nullable: false)
        pluginId(nullable: false)
    }
  */

    def 'Test List System Entity Type'(){

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
