package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.actions.appversion.ListAppVersionActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppVersion
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/2/2015.
 */

@TestFor(AppVersionController)
@Mock([
        AppVersion,
        ListAppVersionActionService,
        BaseService,
        AppSessionService
])

class AppVersionControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.listAppVersionActionService.appSessionService.setAppUser(appUser)
    }

    def 'Test Show Action' () {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appVersion/show'
    }

    /*
    long id             // Object id (auto generated)
    int span            // release time span
    int pluginId        // Plugin id (For example: PluginConnector.APPLICATION_ID)
    int releaseNo       // release number
    Date releasedOn     // release date
    boolean isCurrent   // if current release
    */

    def 'Test List Action' () {
        AppVersion appVersion = new AppVersion(
                span : 1,
                pluginId : 1,
                releaseNo : 1,
                releasedOn : new Date(),
                isCurrent : false
        )

        appVersion.save(flush: true, failOnError: true, validate: false)

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.pluginId = appVersion.pluginId

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
