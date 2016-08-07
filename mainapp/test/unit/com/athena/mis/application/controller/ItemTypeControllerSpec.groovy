package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.itemtype.CreateItemTypeActionService
import com.athena.mis.application.actions.itemtype.ListItemTypeActionService
import com.athena.mis.application.actions.itemtype.UpdateItemTypeActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.model.ListItemTypeActionServiceModel
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ListItemTypeActionServiceModelService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.service.TestDataModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/1/2015.
 */

@TestFor(ItemTypeController)

@Mock([ItemType, ItemTypeService, ItemType,CreateItemTypeActionService,UpdateItemTypeActionService, ItemService, TestDataModelService,
        ListItemTypeActionService,
        ListItemTypeActionServiceModelService, ListItemTypeActionServiceModel, BaseService, AppSessionService])

class ItemTypeControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createItemTypeActionService.appSessionService.setAppUser(appUser)
        controller.updateItemTypeActionService.appSessionService.setAppUser(appUser)
        controller.listItemTypeActionService.appSessionService.setAppUser(appUser)
//        controller.deleteItemTypeActionService.appSessionService.setAppUser(appUser)
    }

    def 'Test Show Action' () {
        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/itemType/show'
    }

    def 'Test Create Action' () {

        setup:
        controller.params.id = "1"
        controller.params.version = "0"
        controller.params.name = "Test Item"
        controller.params.categoryId = "1"
        controller.params.createdOn = new Date()
        controller.params.createdBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.updatedBy  = "1"
        controller.params.companyId ="1"

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Item type has been successfully saved"
        response.json.isError == false
    }

    def 'Test Update Action'() {

        setup:
        ItemType itemtype = new ItemType(
                id : 1,
                version : 0,
                name : "Test Item",
                categoryId : 1,
                createdOn : new Date(),
                createdBy : 1,
                updatedOn : new Date(),
                updatedBy  : 1,
                companyId : 1
        )

        itemtype.id = 100
        itemtype.save(flush: true, failOnError: true, validate: false)

        controller.params.id = itemtype.id
        controller.params.version = "0"
        controller.params.name = "Test Item"
        controller.params.categoryId = "1"
        controller.params.createdOn = new Date()
        controller.params.createdBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.updatedBy  = "1"
        controller.params.companyId ="1"

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Item type has been updated successfully"
        response.json.isError == false
    }

    def 'Test List Action' () {

        setup:
        ItemType itemtype = new ItemType(
                id : 1,
                version : 0,
                name : "Test Item",
                categoryId : 1,
                createdOn : new Date(),
                createdBy : 1,
                updatedOn : new Date(),
                updatedBy  : 1,
                companyId : 1
        )
        itemtype.save(flush: true, failOnError: true, validate: false)

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
