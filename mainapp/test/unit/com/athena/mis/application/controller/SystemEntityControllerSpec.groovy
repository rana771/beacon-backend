package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.systementity.CreateSystemEntityActionService
import com.athena.mis.application.actions.systementity.DeleteSystemEntityActionService
import com.athena.mis.application.actions.systementity.ListSystemEntityActionService
import com.athena.mis.application.actions.systementity.ShowSystemEntityActionService
import com.athena.mis.application.actions.systementity.UpdateSystemEntityActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.service.SystemEntityTypeService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

import javax.sql.DataSource

/**
 * Created by Asif on 12/7/2015.
 */

@TestFor(SystemEntityController)
@Mock([
        SystemEntity,
        SystemEntityType,
        SystemEntityService,
        SystemEntityTypeService,
        ShowSystemEntityActionService,
        CreateSystemEntityActionService,
        UpdateSystemEntityActionService,
        ListSystemEntityActionService,
        AppUserEntityService,
        AppAttachmentService,
        AppNoteService,
        SupplierService,
        ItemService,
        ContentCategoryService,
        AppSystemEntityCacheService,
        BaseService,
        AppSessionService
])

class SystemEntityControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser();
        appUser.id = 1;
        appUser.companyId = 1
        controller.createSystemEntityActionService.appSessionService.setAppUser(appUser)
        controller.updateSystemEntityActionService.appSessionService.setAppUser(appUser)
        controller.listSystemEntityActionService.appSessionService.setAppUser(appUser)
    }


    def 'Show System Entity' () {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/systemEntity/show'
    }

    def 'Create System Entity' () {
        setup:
        SystemEntityType systemEntityType=new SystemEntityType(name:"Test",description:"N/A",pluginId:1)
        systemEntityType.version=0
        systemEntityType.id=1
        systemEntityType.save()

        controller.params.version = "0"
        controller.params.key = "KeyEntityy"
        controller.params.value = "Entity Value"
        controller.params.type = "1"
        controller.params.systemEntityTypeId ="1"
        controller.params.isActive = true
        controller.params.companyId = "1"
        controller.params.reservedId = "1"
        controller.params.pluginId = "1"
        controller.params.createdBy = 1
        controller.params.createdOn = new Date()
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
//        DataSource dataSource
//        controller.createSystemEntityActionService.systemEntityService.dataSource=dataSource
        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "System entity has been successfully saved"
        response.json.isError == false
    }

    def 'Update System Entity' () {

        setup:
        SystemEntity systemEntity = new SystemEntity(
                version : 0,
                key : "KeyEntityy",
                value : "Entity Value",
                type : 1,
                isActive : true,
                companyId : 1,
                reservedId : 1,
                pluginId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )

        systemEntity.id = 1
        systemEntity.save(flush: true)

        controller.params.id = systemEntity.id
        controller.params.version = 0
        controller.params.key = "KeyEntityy"
        controller.params.value = "Entity Value"
        controller.params.type = 1
        controller.params.isActive = true
        controller.params.companyId = 1
        controller.params.reservedId = 1
        controller.params.pluginId = 1
        controller.params.createdBy = 1
        controller.params.createdOn = new Date()
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "System entity has been updated successfully"
        response.json.isError == false
    }

    def 'List System Entity' (){
        SystemEntityType set = new SystemEntityType(

                version : 0,        // entity version in the persistence layer
                name : "Test Entity Type",        // name of SystemEntityType
                description : "This is for testing", // description of SystemEntityType
                pluginId : 1
        )
        set.id = 1
        set.save(flush: true)

        SystemEntity systemEntity = new SystemEntity(
                version : 0,
                key : "KeyEntityy",
                value : "Entity Value",
                type : set.id,
                isActive : true,
                companyId : 1,
                reservedId : 1,
                pluginId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )
        systemEntity.id = 1
        systemEntity.save(flush: true)

        controller.params.systemEntityTypeId = set.id

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
