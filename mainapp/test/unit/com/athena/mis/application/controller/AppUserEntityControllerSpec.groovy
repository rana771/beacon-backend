package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appuserentity.CreateAppUserEntityActionService
import com.athena.mis.application.actions.appuserentity.DeleteAppUserEntityActionService
import com.athena.mis.application.actions.appuserentity.ListAppUserByEntityTypeAndEntityService
import com.athena.mis.application.actions.appuserentity.ListAppUserEntityActionService
import com.athena.mis.application.actions.appuserentity.ShowAppUserEntityActionService
import com.athena.mis.application.actions.appuserentity.UpdateAppUserEntityActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListAppUserEntityActionServiceModel
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.service.ListAppUserEntityActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/23/2015.
 */

@TestFor(AppUserEntityController)

@Mock([
        AppUserEntity,
        SystemEntity,
        AppUserEntityService,
        ListAppUserEntityActionServiceModelService,
        ListAppUserEntityActionServiceModel,
        ShowAppUserEntityActionService,
        CreateAppUserEntityActionService,
        UpdateAppUserEntityActionService,
        DeleteAppUserEntityActionService,
        ListAppUserEntityActionService,
        ListAppUserByEntityTypeAndEntityService,
        AppSystemEntityCacheService,
        BaseService,
        AppSessionService
])

class AppUserEntityControllerSpec extends Specification {

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1
        appUser.companyId = 1

        controller.showAppUserEntityActionService.appSessionService.setAppUser(appUser)
        controller.createAppUserEntityActionService.appSessionService.setAppUser(appUser)
        controller.updateAppUserEntityActionService.appSessionService.setAppUser(appUser)
        controller.deleteAppUserEntityActionService.appSessionService.setAppUser(appUser)
        controller.listAppUserEntityActionService.appSessionService.setAppUser(appUser)
        controller.listAppUserByEntityTypeAndEntityService.appSessionService.setAppUser(appUser)
    }

    def 'Show AppUserEntity' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/appUserEntity/show"
    }

    def 'Create AppUserEntity' () {

        setup:

        controller.params.id = 1                         // primary key (Auto generated by its own sequence)
        controller.params.appUserId = 1                  // AppUser.id
        controller.params.entityTypeId = 1              // SystemEntity.id (e.g. Project, Inventory, Customer etc)
        controller.params.entityId = 1                  // id of corresponding entity (e.g. Project.id, Inventory.id)
        controller.params.companyId = 1

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "User entity mapping has been saved successfully"
        response.json.isError == false
    }

    def 'Update AppUserEntity'(){
        setup:

        AppUserEntity appUserEntity = new AppUserEntity(
                appUserId : 1,                  // AppUser.id
                entityTypeId : 1,              // SystemEntity.id (e.g. Project, Inventory, Customer etc)
                entityId : 1,                  // id of corresponding entity (e.g. Project.id, Inventory.id)
                companyId : 1
        )

        appUserEntity.id = 1
        appUserEntity.save(flush: true, failOnError: true, validate: false)

        controller.params.id = "1"                         // primary key (Auto generated by its own sequence)
        controller.params.appUserId = "1"                  // AppUser.id
        controller.params.entityTypeId = "1"              // SystemEntity.id (e.g. Project, Inventory, Customer etc)
        controller.params.entityId = "1"                  // id of corresponding entity (e.g. Project.id, Inventory.id)
        controller.params.companyId = "1"

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "User entity mapping has been updated successfully"
        response.json.isError == false
    }

    def 'List AppUserEntity' () {
        setup:

        AppUserEntity appUserEntity = new AppUserEntity(
                appUserId : 1,                  // AppUser.id
                entityTypeId : 1,              // SystemEntity.id (e.g. Project, Inventory, Customer etc)
                entityId : 1,                  // id of corresponding entity (e.g. Project.id, Inventory.id)
                companyId : 1
        )

        appUserEntity.id = 1
        appUserEntity.save(flush: true, failOnError: true, validate: false)

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.entityTypeId = appUserEntity.entityTypeId
        controller.params.entityId = appUserEntity.entityId

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List UserByEntityTypeAndEntity'(){

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
        systemEntity.companyId = 1

        systemEntity.reservedId=controller.listAppUserByEntityTypeAndEntityService.appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT
        systemEntity.save()

        controller.listAppUserByEntityTypeAndEntityService.appSystemEntityCacheService.sysEntityMap.put(controller.listAppUserByEntityTypeAndEntityService.appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY,[systemEntity])

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.reservedTypeId = systemEntity.reservedId
        controller.params.entityId = 1

        when:
        request.method = 'POST'
        controller.listUserByEntityTypeAndEntity()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
