package com.athena.mis.application.controller

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.actions.item.CreateItemCategoryFixedAssetActionService
import com.athena.mis.application.actions.item.CreateItemCategoryInventoryActionService
import com.athena.mis.application.actions.item.CreateItemCategoryNonInvActionService
import com.athena.mis.application.actions.item.DeleteItemCategoryFixedAssetActionService
import com.athena.mis.application.actions.item.DeleteItemCategoryInventoryActionService
import com.athena.mis.application.actions.item.DeleteItemCategoryNonInvActionService
import com.athena.mis.application.actions.item.GetItemListByItemTypeActionService
import com.athena.mis.application.actions.item.ListItemCategoryFixedAssetActionService
import com.athena.mis.application.actions.item.ListItemCategoryInventoryActionService
import com.athena.mis.application.actions.item.ListItemCategoryNonInvActionService
import com.athena.mis.application.actions.item.UpdateItemCategoryFixedAssetActionService
import com.athena.mis.application.actions.item.UpdateItemCategoryInventoryActionService
import com.athena.mis.application.actions.item.UpdateItemCategoryNonInvActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListItemCategoryFixedAssetActionServiceModel
import com.athena.mis.application.model.ListItemCategoryInventoryActionServiceModel
import com.athena.mis.application.model.ListItemCategoryNonInvActionServiceModel
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ListItemCategoryFixedAssetActionServiceModelService
import com.athena.mis.application.service.ListItemCategoryInventoryActionServiceModelService
import com.athena.mis.application.service.ListItemCategoryNonInvActionServiceModelService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.service.SystemEntityTypeService
import com.athena.mis.application.service.TestDataModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/6/2015.
 */

@TestFor(ItemController)

@Mock([
        Item,
        ItemService,
        ItemTypeService,
        ListItemCategoryFixedAssetActionServiceModelService,
        ListItemCategoryInventoryActionServiceModelService,
        ListItemCategoryNonInvActionServiceModelService,
        ListItemCategoryFixedAssetActionServiceModel,
        ListItemCategoryInventoryActionServiceModel,
        ListItemCategoryNonInvActionServiceModel,
        SystemEntity,
        TestDataModelService,
        SystemEntityService,
        SystemEntityTypeService,
        AppSystemEntityCacheService,
        CreateItemCategoryFixedAssetActionService,
        UpdateItemCategoryFixedAssetActionService,
        ListItemCategoryFixedAssetActionService,
        GetItemListByItemTypeActionService,
        ListItemCategoryNonInvActionService,
        UpdateItemCategoryNonInvActionService,
        ListItemCategoryInventoryActionService,
        CreateItemCategoryInventoryActionService,
        UpdateItemCategoryInventoryActionService,
        CreateItemCategoryNonInvActionService,
        BaseService,
        AppSessionService
])

class ItemControllerSpec extends Specification {

    void setup() {
        AppUser appUser = new AppUser()
        appUser.companyId = 500
        appUser.id = 1

        controller.createItemCategoryFixedAssetActionService.appSessionService.setAppUser(appUser)
        controller.updateItemCategoryFixedAssetActionService.appSessionService.setAppUser(appUser)
        controller.listItemCategoryFixedAssetActionService.appSessionService.setAppUser(appUser)

        controller.createItemCategoryNonInvActionService.appSessionService.setAppUser(appUser)
        controller.updateItemCategoryNonInvActionService.appSessionService.setAppUser(appUser)
        controller.listItemCategoryNonInvActionService.appSessionService.setAppUser(appUser)

        controller.createItemCategoryInventoryActionService.appSessionService.setAppUser(appUser)
        controller.updateItemCategoryInventoryActionService.appSessionService.setAppUser(appUser)
        controller.listItemCategoryInventoryActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Fixed Asset Item'() {

        when:
        request.method = "POST"
        controller.showFixedAssetItem()

        then:
        view == "/application/item/showFixedAsset"
    }

    def 'Show Inventory Item'() {
        when:
        request.method = "POST"
        controller.showInventoryItem()

        then:
        view == "/application/item/showInventory"
    }

    def 'Show Non Inventory Item'() {
        when:
        request.method = "POST"
        controller.showNonInventoryItem()

        then:
        view == "/application/item/showNonInventory"
    }


    def 'Create Fixed Asset Item'() {

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
        systemEntity.companyId = 500;

        systemEntity.reservedId = controller.createItemCategoryFixedAssetActionService.appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_FIXED_ASSET
        systemEntity.save()

        controller.createItemCategoryFixedAssetActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createItemCategoryFixedAssetActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY,[systemEntity])

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

        systemEntity.companyId = 500;
        systemEntity.reservedId=controller.createItemCategoryFixedAssetActionService.appSystemEntityCacheService.SYS_ENTITY_VALUATION_AVG

        systemEntity.save()
        controller.createItemCategoryFixedAssetActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createItemCategoryFixedAssetActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_VALUATION,[systemEntity])

        controller.params.version = "0"                        // entity version in the persistence layer
        controller.params.name = "Test Item"                       // Unique item name by category
        controller.params.code = "ITM"                       // Item Code
        controller.params.unit = "Piece"                   // Item Unit
        controller.params.itemTypeId = "1"                   // ItemType.id
        controller.params.categoryId = "1"                   // SystemEntity.id (Inventory, NonInventory, FixedAsset)
        controller.params.valuationTypeId = systemEntity.id               // SystemEntity.id (e.g : FIFO, LIFO etc)
        controller.params.isIndividualEntity = true         // flag for individual entity (for item type Fixed Asset)
        controller.params.isFinishedProduct = false          // is the item finished product (for item type Inventory)
        controller.params.companyId = "1"                     // Company.id
        controller.params.createdBy = "1"                     // AppUser.id
        controller.params.createdOn = new Date()                    // Object creation DateTime
        controller.params.updatedBy = "1"                     // AppUser.id
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.createFixedAssetItem()

        then:
        response.redirectedUrl == null
        response.json.message == "Item has been successfully saved"
        response.json.isError == false
    }


    def 'Tes Create Inventory Item'() {
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
        systemEntity.companyId = 500;

        systemEntity.reservedId=controller.createItemCategoryInventoryActionService.appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_INVENTORY
        systemEntity.save()

        controller.createItemCategoryInventoryActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createItemCategoryInventoryActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY,[systemEntity])

        controller.params.version = "0"                        // entity version in the persistence layer
        controller.params.name = "Test Item"                       // Unique item name by category
        controller.params.code = "ITM"                       // Item Code
        controller.params.unit = "Piece"                   // Item Unit
        controller.params.itemTypeId = "2"                   // ItemType.id
        controller.params.categoryId = "2"                   // SystemEntity.id (Inventory, NonInventory, FixedAsset)
        controller.params.valuationTypeId = systemEntity.id              // SystemEntity.id (e.g : FIFO, LIFO etc)
        controller.params.isIndividualEntity = true         // flag for individual entity (for item type Fixed Asset)
        controller.params.isFinishedProduct = false          // is the item finished product (for item type Inventory)
        controller.params.companyId = "1"                     // Company.id
        controller.params.createdBy = "1"                     // AppUser.id
        controller.params.createdOn = new Date()                    // Object creation DateTime
        controller.params.updatedBy = "1"                     // AppUser.id
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.createInventoryItem()

        then:
        response.redirectedUrl == null
        response.json.message == "Item has been successfully saved"
        response.json.isError == false
    }



    def 'Test Create Non Inventory Item'() {
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
        systemEntity.companyId = 500;

        systemEntity.reservedId=controller.createItemCategoryNonInvActionService.appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_NON_INVENTORY
        systemEntity.save()

        controller.createItemCategoryNonInvActionService.appSystemEntityCacheService.sysEntityMap.put(controller.createItemCategoryNonInvActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY,[systemEntity])

        controller.params.version = "0"                        // entity version in the persistence layer
        controller.params.name = "Test Item"                       // Unique item name by category
        controller.params.code = "ITM"                       // Item Code
        controller.params.unit = "Piece"                   // Item Unit
        controller.params.itemTypeId = "3"                   // ItemType.id
        controller.params.categoryId = "3"                   // SystemEntity.id (Inventory, NonInventory, FixedAsset)
        controller.params.valuationTypeId = 100               // SystemEntity.id (e.g : FIFO, LIFO etc)
        controller.params.isIndividualEntity = true         // flag for individual entity (for item type Fixed Asset)
        controller.params.isFinishedProduct = false          // is the item finished product (for item type Inventory)
        controller.params.companyId = "1"                     // Company.id
        controller.params.createdBy = "1"                     // AppUser.id
        controller.params.createdOn = new Date()                    // Object creation DateTime
        controller.params.updatedBy = "1"                     // AppUser.id
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.createNonInventoryItem()

        then:
        response.redirectedUrl == null
        response.json.message == "Item has been successfully saved"
        response.json.isError == false
    }


    def 'Test Update Fixed Asset Item'() {
        setup:
        Item item = new Item(
                version: 0,                        // entity version in the persistence layer
                name: "Test Item",                       // Unique item name by category
                code: "ITM",                       // Item Code
                unit: "Piece",                   // Item Unit
                itemTypeId: 1,                   // ItemType.id
                categoryId: 1,                  // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId: 1,              // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity: true,        // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct: false,          // is the item finished product (for item type Inventory)
                companyId: 1,                     // Company.id
                createdBy: 1,                     // AppUser.id
                createdOn: new Date(),                    // Object creation DateTime
                updatedBy: 1,                     // AppUser.id
                updatedOn: new Date()
        )

        item.id = 100
        item.save(flush: true, failOnError: true, validate: false)

        controller.params.version = "0"                        // entity version in the persistence layer
        controller.params.name = "Test Item"                       // Unique item name by category
        controller.params.code = "ITM"                       // Item Code
        controller.params.unit = "Piece"                   // Item Unit
        controller.params.itemTypeId = "3"                   // ItemType.id
        controller.params.categoryId = "3"                   // SystemEntity.id (Inventory, NonInventory, FixedAsset)
        controller.params.valuationTypeId = "1"               // SystemEntity.id (e.g : FIFO, LIFO etc)
        controller.params.isIndividualEntity = true         // flag for individual entity (for item type Fixed Asset)
        controller.params.isFinishedProduct = false          // is the item finished product (for item type Inventory)
        controller.params.companyId = "1"                     // Company.id
        controller.params.createdBy = "1"                     // AppUser.id
        controller.params.createdOn = new Date()                    // Object creation DateTime
        controller.params.updatedBy = "1"                     // AppUser.id
        controller.params.updatedOn = new Date()
        controller.params.id = item.id

        when:
        request.method = 'POST'
        controller.updateFixedAssetItem()

        then:
        response.redirectUrl == null
        response.json.message == controller.updateItemCategoryFixedAssetActionService.ITEM_UPDATE_SUCCESS_MESSAGE
        response.json.isError == false
    }



    def 'Test Update Inventory Item'() {
        setup:
        Item item = new Item(
                version: 0,                        // entity version in the persistence layer
                name: "Test Item",                       // Unique item name by category
                code: "ITM",                       // Item Code
                unit: "Piece",                   // Item Unit
                itemTypeId: 1,                   // ItemType.id
                categoryId: 1,                  // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId: 1,              // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity: true,        // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct: false,          // is the item finished product (for item type Inventory)
                companyId: 1,                     // Company.id
                createdBy: 1,                     // AppUser.id
                createdOn: new Date(),                    // Object creation DateTime
                updatedBy: 1,                     // AppUser.id
                updatedOn: new Date()
        )

        item.id = 100
        item.save(flush: true, failOnError: true, validate: false)

        controller.params.version = "0"                        // entity version in the persistence layer
        controller.params.name = "Test Item"                       // Unique item name by category
        controller.params.code = "ITM"                       // Item Code
        controller.params.unit = "Piece"                   // Item Unit
        controller.params.itemTypeId = "3"                   // ItemType.id
        controller.params.categoryId = "3"                   // SystemEntity.id (Inventory, NonInventory, FixedAsset)
        controller.params.valuationTypeId = "1"               // SystemEntity.id (e.g : FIFO, LIFO etc)
        controller.params.isIndividualEntity = true         // flag for individual entity (for item type Fixed Asset)
        controller.params.isFinishedProduct = false          // is the item finished product (for item type Inventory)
        controller.params.companyId = "1"                     // Company.id
        controller.params.createdBy = "1"                     // AppUser.id
        controller.params.createdOn = new Date()                    // Object creation DateTime
        controller.params.updatedBy = "1"                     // AppUser.id
        controller.params.updatedOn = new Date()
        controller.params.id = item.id

        when:
        request.method = 'POST'
        controller.updateFixedAssetItem()

        then:
        response.redirectUrl == null
        response.json.message == controller.updateItemCategoryInventoryActionService.ITEM_UPDATE_SUCCESS_MESSAGE
        response.json.isError == false
    }


    def 'Test Update Non Inventory Item'() {
        setup:
        Item item = new Item(
                version: 0,                        // entity version in the persistence layer
                name: "Test Item",                       // Unique item name by category
                code: "ITM",                       // Item Code
                unit: "Piece",                   // Item Unit
                itemTypeId: 1,                   // ItemType.id
                categoryId: 1,                  // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId: 1,              // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity: true,        // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct: false,          // is the item finished product (for item type Inventory)
                companyId: 1,                     // Company.id
                createdBy: 1,                     // AppUser.id
                createdOn: new Date(),                    // Object creation DateTime
                updatedBy: 1,                     // AppUser.id
                updatedOn: new Date()
        )

        item.id = 100
        item.save(flush: true, failOnError: true, validate: false)

        controller.params.version = "0"                        // entity version in the persistence layer
        controller.params.name = "Test Item"                       // Unique item name by category
        controller.params.code = "ITM"                       // Item Code
        controller.params.unit = "Piece"                   // Item Unit
        controller.params.itemTypeId = "3"                   // ItemType.id
        controller.params.categoryId = "3"                   // SystemEntity.id (Inventory, NonInventory, FixedAsset)
        controller.params.valuationTypeId = "1"               // SystemEntity.id (e.g : FIFO, LIFO etc)
        controller.params.isIndividualEntity = true         // flag for individual entity (for item type Fixed Asset)
        controller.params.isFinishedProduct = false          // is the item finished product (for item type Inventory)
        controller.params.companyId = "1"                     // Company.id
        controller.params.createdBy = "1"                     // AppUser.id
        controller.params.createdOn = new Date()                    // Object creation DateTime
        controller.params.updatedBy = "1"                     // AppUser.id
        controller.params.updatedOn = new Date()
        controller.params.id = item.id

        when:
        request.method = 'POST'
        controller.updateFixedAssetItem()

        then:
        response.redirectUrl == null
        response.json.message == "Item has been updated successfully"
        response.json.isError == false
    }

    def 'Test List Fixed Asset Item'() {
        setup:
        Item item = new Item(
                version: 0,                        // entity version in the persistence layer
                name: "Test Item",                       // Unique item name by category
                code: "ITM",                       // Item Code
                unit: "Piece",                   // Item Unit
                itemTypeId: 1,                   // ItemType.id
                categoryId: 1,                  // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId: 1,              // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity: true,        // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct: false,          // is the item finished product (for item type Inventory)
                companyId: 1,                     // Company.id
                createdBy: 1,                     // AppUser.id
                createdOn: new Date(),                    // Object creation DateTime
                updatedBy: 1,                     // AppUser.id
                updatedOn: new Date()
        )

        item.id = 100
        item.save(flush: true, failOnError: true, validate: false)

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

        systemEntity.reservedId=controller.listItemCategoryFixedAssetActionService.appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_FIXED_ASSET
        systemEntity.save()

        controller.listItemCategoryFixedAssetActionService.appSystemEntityCacheService.sysEntityMap.put(controller.listItemCategoryFixedAssetActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY,[systemEntity])

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listFixedAssetItem()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'Test List Inventory Item'() {
        setup:
        Item item = new Item(
                version: 0,                        // entity version in the persistence layer
                name: "Test Item",                       // Unique item name by category
                code: "ITM",                       // Item Code
                unit: "Piece",                   // Item Unit
                itemTypeId: 1,                   // ItemType.id
                categoryId: 1,                  // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId: 1,              // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity: true,        // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct: false,          // is the item finished product (for item type Inventory)
                companyId: 1,                     // Company.id
                createdBy: 1,                     // AppUser.id
                createdOn: new Date(),                    // Object creation DateTime
                updatedBy: 1,                     // AppUser.id
                updatedOn: new Date()
        )

        item.id = 100
        item.save(flush: true, failOnError: true, validate: false)

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

        systemEntity.reservedId=controller.listItemCategoryInventoryActionService.appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_INVENTORY
        systemEntity.save()

        controller.listItemCategoryInventoryActionService.appSystemEntityCacheService.sysEntityMap.put(controller.listItemCategoryInventoryActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY,[systemEntity])

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listInventoryItem()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'Test List Non Inventory Item'() {
        setup:
        Item item = new Item(
                version: 0,                        // entity version in the persistence layer
                name: "Test Item",                       // Unique item name by category
                code: "ITM",                       // Item Code
                unit: "Piece",                   // Item Unit
                itemTypeId: 1,                   // ItemType.id
                categoryId: 1,                  // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId: 1,              // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity: true,        // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct: false,          // is the item finished product (for item type Inventory)
                companyId: 1,                     // Company.id
                createdBy: 1,                     // AppUser.id
                createdOn: new Date(),                    // Object creation DateTime
                updatedBy: 1,                     // AppUser.id
                updatedOn: new Date()
        )

        item.id = 100
        item.save(flush: true, failOnError: true, validate: false)

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

        systemEntity.reservedId=controller.listItemCategoryNonInvActionService.appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_NON_INVENTORY
        systemEntity.save()

        controller.listItemCategoryNonInvActionService.appSystemEntityCacheService.sysEntityMap.put(controller.listItemCategoryNonInvActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY,[systemEntity])

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listNonInventoryItem()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'Test Item By Item Type ID'() {
        setup:
        controller.params.version = "0"                        // entity version in the persistence layer
        controller.params.name = "Test Item"                       // Unique item name by category
        controller.params.code = "ITM"                       // Item Code
        controller.params.unit = "Piece"                   // Item Unit
        controller.params.itemTypeId = 1                  // ItemType.id
        controller.params.categoryId = 3                   // SystemEntity.id (Inventory, NonInventory, FixedAsset)
        controller.params.valuationTypeId = 1               // SystemEntity.id (e.g : FIFO, LIFO etc)
        controller.params.isIndividualEntity = true         // flag for individual entity (for item type Fixed Asset)
        controller.params.isFinishedProduct = false          // is the item finished product (for item type Inventory)
        controller.params.companyId = "1"                     // Company.id
        controller.params.createdBy = "1"                     // AppUser.id
        controller.params.createdOn = new Date()                    // Object creation DateTime
        controller.params.updatedBy = "1"                     // AppUser.id
        controller.params.updatedOn = new Date()
        controller.params.id = 1

        when:
        request.method = 'POST'
        controller.listItemByItemTypeId()

        then:
        response.json.isError == false
    }

}
