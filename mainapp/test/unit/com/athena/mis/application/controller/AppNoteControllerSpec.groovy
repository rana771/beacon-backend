package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appnote.CreateAppNoteActionService
import com.athena.mis.application.actions.appnote.DeleteAppNoteActionService
import com.athena.mis.application.actions.appnote.ListAppNoteActionService
import com.athena.mis.application.actions.appnote.ListAppNoteForListViewActionService
import com.athena.mis.application.actions.appnote.ShowAppNoteActionService
import com.athena.mis.application.actions.appnote.UpdateAppNoteActionService
import com.athena.mis.application.entity.AppNote
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.model.ListAppNoteActionServiceModel
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppNoteActionServiceModelService
import com.athena.mis.application.service.SystemEntityTypeService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/7/2015.
 */

@TestFor(AppNoteController)

@Mock([
        AppNote,
        AppUser,
        CreateAppNoteActionService,
        AppNoteService,
        ListAppNoteActionServiceModelService,
        ListAppNoteActionServiceModel,
        ListAppNoteActionService,
        ShowAppNoteActionService,
        UpdateAppNoteActionService,
        DeleteAppNoteActionService,
        ListAppNoteForListViewActionService,
        AppSystemEntityCacheService,
        SystemEntity,
        SystemEntityTypeService,
        BaseService,
        SystemEntityType,
        AppSessionService
])

class AppNoteControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1
        appUser.companyId = 1

        SystemEntityType systemEntityType = new SystemEntityType(
                version : 0,
                name : "Test Entity",
                description : "This is for Test",
                pluginId : 1
        )
        systemEntityType.id = 1
        systemEntityType.save()

        systemEntityType = new SystemEntityType(
                version : 0,
                name : "Test Entity2",
                description : "This is for Test2",
                pluginId : 1
        )
        systemEntityType.id = AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE
        systemEntityType.save()

        SystemEntity systemEntity = new SystemEntity(
                version : 0,
                key : "Test Key",
                type : 1,
                reservedId : AppSystemEntityCacheService.SYS_ENTITY_NOTE_NONE,
                isActive : true,
                pluginId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedOn : new Date(),
                updatedBy : 1,
                companyId : 1
        )
        systemEntity.id = 1
        systemEntity.save()

        SystemEntity systemEntity1 = new SystemEntity(
                version : 0,
                key : "Test1 Key",
                type : AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE,
                reservedId : AppSystemEntityCacheService.SYS_ENTITY_NOTE_NONE,
                isActive : true,
                pluginId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedOn : new Date(),
                updatedBy : 1,
                companyId : 1
        )
        systemEntity1.id = 2
        systemEntity1.save()

        controller.createAppNoteActionService.appSessionService.setAppUser(appUser)
        controller.updateAppNoteActionService.appSessionService.setAppUser(appUser)
        controller.listAppNoteActionService.appSessionService.setAppUser(appUser)
        controller.listAppNoteForListViewActionService.appSessionService.setAppUser(appUser)

        controller.createAppNoteActionService.appSystemEntityCacheService.init()
    }

    def 'Show AppNote' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appNote/show'
    }

    def 'Create AppNote' () {

        setup:

//        controller.createAppNoteActionService.appSystemEntityCacheService.sysEntityMap.put(AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE,[systemEntity])

        controller.params.version = "0"
        controller.params.entityTypeId  = "1"
        controller.params.entityId = "1"
        controller.params.entityNoteTypeId = "1"
        controller.params.note  = "This is test note"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.companyId = "1"
        controller.params.pluginId  = "1"

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Note has been successfully saved"
        response.json.isError == false
    }

    def 'Update AppNote' () {
        setup:
        AppNote appNote = new AppNote(
                version : 0,
                entityTypeId  : 1,
                entityId : 1,
                entityNoteTypeId : 1,
                note : "This is test note",
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                companyId : 1,
                pluginId  : 1
        )

        appNote.id = 100
        appNote.save(flush: true, failOnError: true, validate: false)

        controller.params.id = 100
        controller.params.version = "0"
        controller.params.entityTypeId  = "1"
        controller.params.entityId = "1"
        controller.params.entityNoteTypeId = "1"
        controller.params.note  = "This is test note"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.companyId = "1"
        controller.params.pluginId  = "1"

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Note has been updated successfully"
        response.json.isError == false
    }

    def 'List AppNote' () {
        setup:
        AppNote appNote = new AppNote(
                version : 0,
                entityTypeId  : 1,
                entityId : 1,
                entityNoteTypeId : 1,
                note : "This is test note",
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                companyId : 1,
                pluginId  : 1
        )

        appNote.id = 1
        appNote.save(flush: true, failOnError: true, validate: false)

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

    def 'View EntityNote' () {

       /* setup:
        AppNote appNote = new AppNote(
                version : 0,
                entityTypeId  : 1,
                entityId : 1,
                entityNoteTypeId : 1,
                note : "This is test note",
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                companyId : 1,
                pluginId  : 1
        )

        appNote.id = 100
        appNote.save(flush: true, failOnError: true, validate: false)

        controller.params.id = 100*/

        when:
        request.method = "POST"
        controller.viewEntityNote()

        then:
        view == '/application/appNote/showNotes'
//        model.note == "This is test note"
    }

    def 'List EntityNote' () {
        setup:

        SystemEntityType systemEntityType = new SystemEntityType(
                version : 0,
                name : "Test Entity",
                description : "This is for Test",
                pluginId : 1
        )
        systemEntityType.id = AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY
        systemEntityType.save()

        SystemEntity systemEntity = new SystemEntity(
                version : 0,
                key : "Test Key",
                type : systemEntityType.id,
                reservedId : AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY,
                isActive : true,
                pluginId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedOn : new Date(),
                updatedBy : 1,
                companyId : 1
        )
        systemEntity.id = 5
        systemEntity.save()

        AppNote appNote = new AppNote(
                version : 0,
                entityTypeId  : systemEntityType.id,
                entityId : systemEntity.id,
                entityNoteTypeId : 1,
                note : "This is test note for list action",
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                companyId : 1,
                pluginId  : 1
        )
        appNote.id = 1
        appNote.save(flush: true, failOnError: true, validate: false)

        controller.listAppNoteForListViewActionService.appSystemEntityCacheService.init()

//        controller.params.entityId = appNote.entityId.toString()
//        long entityTypeId = AppSystemEntityCacheService.SYS_ENTITY_NOTE_DB_QUERY
//        controller.params.entityTypeId = entityTypeId.toString()

//        SystemEntity systemEntity = new SystemEntity()
//        systemEntity.id = 1
//        systemEntity.version = 0
//        systemEntity.key = "Test Key"
//        systemEntity.type = AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY
//        systemEntity.isActive = true
//        systemEntity.pluginId = 1
//        systemEntity.createdBy = 1
//        systemEntity.createdOn = new Date()
//        systemEntity.updatedOn = new Date()
//        systemEntity.updatedBy = 1
//        systemEntity.companyId = 1;
//
//        systemEntity.reservedId = AppSystemEntityCacheService.SYS_ENTITY_NOTE_TASK
//        systemEntity.save()
//
//        controller.listAppNoteForListViewActionService.appSystemEntityCacheService.sysEntityMap.put(AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY,[systemEntity])

        controller.params.entityId = systemEntity.id.toString()
        controller.params.entityTypeId = systemEntityType.id.toString()
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = "10"
        controller.params.skip = "0"

        when:
        request.method = 'POST'
        controller.listEntityNote()

        then:
        response.json.isError == false
        response.json.count >= 0
    }


}
